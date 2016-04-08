package jp.ne.ruru.park.ando.diejavaco2;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XMLデータ置き場。
 * データ関連の管理を担当します。
 * @author 安藤
 *
 */
public class XMLData {
	
	/** データの読み込み */
	public XMLData() {
		DocumentBuilderFactory factory;
		DocumentBuilder        builder;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setIgnoringComments(true);
			factory.setValidating(true);
			Node root    = builder.parse("src/main/config/xml/data.xml");
			Set<String> sameNameChek = new TreeSet<String>();
			invokeLoop(root,null,sameNameChek);
		} catch (ParserConfigurationException e0) {
			System.out.println(e0.getMessage());
		} catch (SAXException e1){
			System.out.println(e1.getMessage());
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
		}
	}
	/**
	 * ループ処理 
	 * @param parentNode 親ノード
	 * @param parentEvent 親イベント
	 * @param sameNameChek 同名チェック用
	 */
	protected void invokeLoop(Node parentNode,Event parentEvent,Set<String> sameNameChek) {
		final NodeList nodes = parentNode.getChildNodes();
		if (nodes == null) {
			return;
		}
		IntStream
			.range(0,nodes.getLength())
			.mapToObj(nodes::item)
			.filter(n->n instanceof Element)
			.map(n->(Element)n)
			.forEach(e->{
				switch (e.getTagName()) {
				case "root": {
					this.invokeLoop(e, parentEvent,sameNameChek);
					break;
				}
				case "sinario":{
					EventSinario event = new EventSinario(e,parentEvent,sameNameChek);
					getSinarioList().add(event);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "contry":{
					EventContry event =
							new EventContry(e,sameNameChek,getContryList().size());
					getContryList().add(event);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "person":{
					EventPerson event = new EventPerson(e,sameNameChek);
					// 親を接続する
					((EventContry)parentEvent).getPersonList().add(event);
					event.setContry((EventContry)parentEvent);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "message": {
					String target = e.getTextContent();
					Arrays.asList(target.split("[\r\n]+"))
					.stream()
					.forEachOrdered(
							message->{
								String ans = message.replaceFirst("^\\s+","");
								if ((ans != null) && (!ans.equals(""))) {
									parentEvent.addMessage(ans);
								}
							});
					break;
				}
				case "location":{
					EventLocation event = new EventLocation(e,sameNameChek);
					this.getLocationSet().add(event);
					this.invokeLoop(e, event,sameNameChek);
					break;
				}
				case "if": {
					break;
				}
				case "selection": {
					String name = e.getAttribute("name");
					if (name == null) {
						name = "";
					}
					//
					String value = e.getAttribute("value");
					int valueInt;
					if ((value == null) || value.equals("")) {
						valueInt = 0;
					} else {
						try {
							valueInt = Integer.parseInt(value);
						} catch(NumberFormatException e2) {
						valueInt = 0;
						}
					}
					if (parentEvent instanceof EventContry) {
						for (int i = 0 ; i < SelectionData.DATA.length ; i++ ) {
							String signature = SelectionData.DATA[i].signature.name;
							if (signature.equals(name)) {
								((EventContry)parentEvent).selectRate[i] = valueInt;
								break;
							}
						}
					} else if (parentEvent instanceof EventSinario) {
						if (((EventSinario)parentEvent).selectRate == null) {
							((EventSinario)parentEvent).selectRate = new int[SelectionData.DATA.length];
						}
						for (int i = 0 ; i < SelectionData.DATA.length ; i++ ) {
							String signature = SelectionData.DATA[i].signature.name;
							if (signature.equals(name)) {
								((EventSinario)parentEvent).selectRate[i] = valueInt;
								break;
							}
						}
					} else {
						System.out.println("sinario error parent=" + parentEvent.toString());
					}
					break;
				}
				default:
					System.out.println("unkown tag tag=" + e.getTagName());
				}
				//this.invokeLoop(parentNode, parentEvent);
			});
	}
	
	/**
	 * ロケーションの取得 
	 * @return ロケーション
	 */
	public Set<EventLocation> getLocationSet() {
		return locationList;
	}
	/**
	 * シナリオの取得 
	 * @return シナリオ
	 */
	public List<EventSinario> getSinarioList() {
		return sinaroList;
	}
	/**
	 * ロケーションから所属国を取得する 
	 * @param location ロケーション
	 * @return 所属国
	 */
	public EventContry getLocationToContry(EventLocation location) {
		// 支配国を検索する
		EventContry targetConttry = null;
		for (EventContry contry : this.getContryList()) {
			if (contry.getLocationSet().stream()
					.anyMatch(loc-> loc== location)) {
				targetConttry = contry;
				break;
			}
		}
		return targetConttry;
	}
	
	/** 仮想敵国の取得
	 * @param myContry
	 * @return カントリー番号
	 */
	protected EventContry getEnemyContry(EventContry myContry) {
		EventContry targetContryNum = null;
		int like = Integer.MAX_VALUE;
		for (EventContry nowContry: this.getContryList()) {
			int i = nowContry.getContryNumber();
			if (nowContry == myContry) {
				continue; // 自国だから
			}
			if (!nowContry.isAlive()) {
				continue; // その国は死んでいる
			}
			if (myContry.getLike()[i] < like) {
				targetContryNum = nowContry;
				like =  myContry.getLike()[i];
			}
		}
		return targetContryNum;
	}
	/**
	 * 移動先の決定 
	 * @param person 人情報
	 */
	public void invokeMovePersion(EventPerson person) {
		// 自地域のロケーションを取得
		if (person.getLocation() == null) {
			person.setToLocation(null); // 移動しない
			return; // 移動しないというかできない
		}

		//
		// 移動可能なロケーションのリスト
		Set<EventLocation> ansLocationList;
		switch (person.getSelection().move) {
		case 1: // ランダム
			// ランダムな人は候補としてすべてを挙げる
			ansLocationList = person.getLocation().getLocationSet();
			break;
		case 2: // 防衛
			ansLocationList = findNearEnemyContry(person);
			//
			break;
		case 3: // 戦闘
			ansLocationList = findNearEnemyContry(person);
			break;
		case 4: // 逃げる
			// 逃げる人用
			// 移動可能なロケーションに敵がいない場所へ移動
			ansLocationList =
					person.getLocation().getLocationSet().stream()
					.filter(
							nowLocation->
							! this.getContryList().stream()
							.filter(contry->contry != person.getContry())
							.flatMap(contry->contry.getArivePersonList().stream())
							.anyMatch(pson->pson.getLocation() == nowLocation))
					.collect(Collectors.toSet());
			if (ansLocationList.isEmpty()) {
				//四面楚歌ならランダムに動いて玉砕する
				ansLocationList = person.getLocation().getLocationSet();
			}
			break;
		default:
			// 移動しない
			ansLocationList = new TreeSet<EventLocation>();
		}		
		// 移動先リストから移動先を決定
		if (ansLocationList.isEmpty()) {
			person.setToLocation(null); // 移動しない
			return;
		} 
		Random rand = new Random();
		EventLocation location =
				ansLocationList.stream()
				.collect(Collectors.toList())
				.get(rand.nextInt(ansLocationList.size()));
		person.setToLocation(location);
	}

	/**
	 * 一番近いロケーションを返す 
	 * @param person 人情報
	 * @return 結果のリスト
	 */
	public Set<EventLocation> findNearEnemyContry(EventPerson person) {
		Set<EventLocation> targetLocationList = new TreeSet<EventLocation>();
		FildResult result = null;
		//
		//現在地点が戦場ならうごいちゃかん
		result = this.getLocationType(-1,person.getContry(),person.getLocation());
		if (result != null) {
			return targetLocationList;
		}
		//
		//System.out.println("------------------------");
		for (EventLocation targetLocation : person.getLocation().getLocationSet()) {
			if (person.getSelection().move == 2) {
				if (!person.getContry().getLocationSet().contains(targetLocation)) {
					// 移動モードなら自国内以外は選択枝から外すが、
					// 「現時点で動かない」が確定する
					return new TreeSet<EventLocation>();
				}
			}
			//System.out.println("地域=" + targetLocation);
			FildResult nowResult = this.findNearEnemyContry(person.getContry(),targetLocation);
			//if (nowResult != null) {
			//	nowResult.display();
			//}
			if (result == null) {
				result = nowResult;
				targetLocationList.add(targetLocation);
			} else {
				int index = result.compareTo(nowResult);
				if (index == 0) {
					targetLocationList.add(targetLocation);
				} else if (0 < index) {
					targetLocationList.clear();
					targetLocationList.add(targetLocation);
					result = nowResult;
				}
			}
		}
		return targetLocationList;
	}
	/** 結果として得られる移動先優先度 */
	protected class FildResult {
		/**
		 * コンストラクタ
		 * @param level レベル
		 * @param myContryFlag 自国ならtrue
		 * @param notEnemyContryFlag 敵国がいるならtrue/自国内で敵がいてもtrue
		 * @param like 隣接国の友好度
		 * @param hp 場の敵HP合計-味方HP合計(少ない方攻めやすい)
		 */
		public FildResult(int level,boolean myContryFlag,boolean notEnemyContryFlag,int like,int hp) {
			this.level = level;
			this.myContryFlag = myContryFlag;
			this.notEnemyContryFlag = notEnemyContryFlag;
			this.like = like;
			this.hp = hp;
		}
		/**
		 * 位置情報の優劣を決める評価関数
		 * @param o 右側の値
		 * @return 右側が優先なら正の数値になる
		 */
		public int compareTo(FildResult o) {
			if (o == null) {
				return -1; // 右側がnullなので左側しか選べない
			}
			if (this.level > o.level) {
				return 1; // 探索レベルはちいさい方が優先される
			}
			if (this.level < o.level) {
				return -1;
			}
			if (this.myContryFlag && o.myContryFlag) {
				// もし両方が自国であれば、敵がいる方を大きくする
				if (this.notEnemyContryFlag && o.notEnemyContryFlag) {
					// もし両方が自国で敵がいる
					return 0;
				} else if (!this.notEnemyContryFlag && o.notEnemyContryFlag) {
					return 1; // 右側に敵がいる
				} else if (this.notEnemyContryFlag && !o.notEnemyContryFlag) {
					return -1; // 左側に敵がいる
				}
				// 両方に敵がいる
				return 0;
			} else if (this.myContryFlag && !o.myContryFlag) {
				// 右側が自国なら
				if (!this.notEnemyContryFlag) {
					return -1; // 自国に敵がいる
				}
				return 1; //
			} else if (!this.myContryFlag && o.myContryFlag) {
				// 左側が自国なら
				if (!o.notEnemyContryFlag) {
					return 1; // 自国に敵がいる
				}
				return -1;
			}
			// 両方とも自国でないなら
			if (this.notEnemyContryFlag && o.notEnemyContryFlag) {
				// 両方ともに敵国でない(空いている)
				return 0;
			} else if (this.notEnemyContryFlag && !o.notEnemyContryFlag) {
				return -1; // 右に敵国がいる
			} else if (!this.notEnemyContryFlag && o.notEnemyContryFlag) {
				return 1; // 左に敵国がいる
			}
			// 両方とも敵国なら
			if (this.like < o.like) {
				return -1; // 左側の方が嫌われている
			}
			if (this.like > o.like) {
				return 1; // 右側の方が嫌われている
			}
			// 両方とも友好値が同じなら
			if (this.hp < o.hp) {
				return -1; // 左側の方がHPが少ない
			}
			if (this.hp > o.hp) {
				return 1; // 右側の方がHPが少ない
			}
			return 0; 
		}
		/** 状態を表示
		 * デバッグ用です
		 */
		public void display() {
			System.out.println("  lev=" + level + " 自国？=" + myContryFlag + " not敵国？=" + notEnemyContryFlag + " 好き？=" + like);
		}
		
		/** 探索階層 */
		public final int level;

		/** 自国情報 */
		public final boolean myContryFlag;
		
		/** 敵国でない？ */
		public final boolean notEnemyContryFlag;
		
		/** 友好値 */
		public final int like;
		
		/** 場の敵HP合計-味方HP合計(少ない方攻めやすい) */
		public final int hp;
	}
	
	/** 探索階層と地域情報をまとめたクラス */
	public class LevelLocation {
		/**
		 * コンストラクタ
		 * @param level 探索階層
		 * @param location 地域情報
		 */
		public LevelLocation(int level,EventLocation location) {
			this.level = level;
			this.location = location;
		}
		/** 探索階層 */
		public final int level;
		
		/** 位置 */
		public final EventLocation location;
	}
	
	/**
	 * 最も近い隣接地を横型探索で探す
	 * @param contry 評価対象の自国
	 * @param loc 評価対象地
	 * @return 隣接地の評価値
	 */
	public FildResult findNearEnemyContry(EventContry contry,EventLocation loc) {
		Set<EventLocation> oldLocation = new TreeSet<EventLocation>();
		Set<EventLocation> targetLocationList = new TreeSet<EventLocation>();
		Deque<LevelLocation> queue = new ArrayDeque<LevelLocation>();
		int level = 0;
		queue.offer(new LevelLocation(level,loc));
		FildResult result = null;
		while (!queue.isEmpty()) {
			LevelLocation levelLocation = queue.poll();
			if (oldLocation.contains(levelLocation.location)) {
				//古いのは無視してよい
				continue;
			}
			oldLocation.add(levelLocation.location);
			targetLocationList.add(levelLocation.location);
			final int nextLevel = level + 1;
			levelLocation.location.getLocationSet().stream().forEachOrdered(
				location->queue.offer(new LevelLocation(nextLevel,location)));
			if (queue.isEmpty() || (queue.peek().level != level)) {
				// 横型検索で１レベル分の最後にたどり着いたから、まとめて１本分処理をここで計算
				//
				for (EventLocation targetLoation : targetLocationList) {
					FildResult nowResult = getLocationType(level,contry,targetLoation);
					if (result == null) {
						result = nowResult;
					} else if ((nowResult != null) && (0 < result.compareTo(nowResult))) {
						result = nowResult;
					}
				}
				if (result != null) {
					break;
				}
				//
				// 次の探索のために消す
				targetLocationList.clear();
				// 次のレベルを設定
				level++;
			}
		}
		return result;
	}	
	
	/**
	 * その地域の友好情報を取得
	 * @param level 探索階層
	 * @param contry 敵国
	 * @param targetLoctaion 地域
	 * @return 友好情報
	 */
	public FildResult getLocationType(int level,EventContry contry,EventLocation targetLoctaion) {
		boolean myContryFlag;
		boolean notEnemyContryFlag;
		int disLike = Integer.MAX_VALUE;
		int hp = 0;
		if (contry.getLocationSet().contains(targetLoctaion)) {
			myContryFlag = true;
			notEnemyContryFlag = 
					! this.getContryList().stream()
					.filter(cont->cont != contry)
					.flatMap(cont->cont.getArivePersonList().stream())
					.anyMatch(p->p.getLocation() == targetLoctaion);
			if (notEnemyContryFlag) {
				// もし敵がいないのなら
				return null;
			}
		} else {
			myContryFlag = false;
			EventContry enemyContry = null;
			for (EventContry cont : this.getContryList()) {
				if (cont == contry) {
					continue;
				}
				if (cont.getLocationSet().contains(targetLoctaion)) {
					enemyContry = cont;
					break;
				}
			}
			if (enemyContry == null) {
				notEnemyContryFlag = true;
			} else {
				notEnemyContryFlag = false;
				disLike = contry.getLike()[enemyContry.getContryNumber()];
			}
			
			// 敵国のその場のHPから、味方のHPを引いた結果を得る
			hp  = 
				 this.getContryList().stream()
				 .filter(cont->cont != contry) // 敵をプラス
				 .flatMap(cont->cont.getArivePersonList().stream())
				 .filter(person->person.getLocation() == targetLoctaion)
				 .mapToInt(person->person.getHp()).sum()
				 - this.getContryList().stream()
				 .filter(cont->cont == contry) // 味方をマイナス
				 .flatMap(cont->cont.getArivePersonList().stream())
				 .filter(person->person.getLocation() == targetLoctaion)
				 .mapToInt(person->person.getHp()).sum();
		}
		return new FildResult(level,myContryFlag,notEnemyContryFlag,disLike,hp);
	}
	
    /**
     * 全国家一覧
     * @return 全国家のリスト
     */
    public List<EventContry> getContryList() {
    	return this.contryList;
    }
	
	/** マップデータ */
	private Set<EventLocation> locationList = new TreeSet<EventLocation>();
	
	/** 国データ */
	private List<EventContry> contryList = new ArrayList<EventContry>();

	/** シナリオデータ */
	private List<EventSinario> sinaroList = new ArrayList<EventSinario>();

}
