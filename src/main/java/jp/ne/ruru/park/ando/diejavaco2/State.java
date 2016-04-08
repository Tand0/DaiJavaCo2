package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jp.ne.ruru.park.ando.diejavaco2.swt.FrameInvokMediator;
import jp.ne.ruru.park.ando.diejavaco2.swt.ImageFactory;

/** 動作状態を監視するクラス
 * 実質上のプログラム本体です。
 * 状態関連の管理を担当します。
 * @author 安藤
 *
 */
public class State {
	
	/** 現在の状態 */
	public enum NOW {
		
		/** 始まる前処理 */
		START_PRE(    "START_PRE"),
		
		/** 始まる処理 */
		START(        "START"),
		
		/** 始まる処理の後処理 */
		START_POST(   "START_POST"),
		
		/** マップ生成の前処理 */
		MAP_PRE(   "MAP_PRE"),
		
		/** マップ生成の後処理 */
		MAP_CREATE("MAP_CREATE"),
		
		/** 地殻変動 */
		MAP_MOVE(  "MAP_MOVE"),
		
		/** マップ生成の後処理 */
		MAP_POST(  "MAP_POST"),

		/** 国フェーズの前処理 */
		SELECT_PRE(     "SELECT_PRE"),
		
		/** 1国単位処理の前処理 */
		SELECT_CONT_PRE("SELECT_CONT_PRE"),
		
		/** 1濃く単位の処理 */
		SELECT_CONT(    "SELECT_CONT"),
		
		/** ユーザ処理の処理 */
		SELECT_CONT_WAIT_PRE( "SELECT_CONT_WAIT_PRE"),
		
		/** ユーザ処理(終了ボタンを押されるまで動かない) */
		SELECT_CONT_WAIT(     "SELECT_CONT_WAIT"),
		
		/** ユーザ処理の後処理 */
		SELECT_CONT_WAIT_POST("SELECT_CONT_WAIT_POST"),
		
		/** 1国単位処理の後処理 */
		SELECT_CONT_POST(     "SELECT_CONT_POST"),
		
		/** 選択の前処理 */
		SELECT_POST("SELECT_POST"),
		
		/** 移動の前処理 */
		MOVE_PRE(        "MOVE_PRE"),
		
		/** 個人単位移動の前処理 */
		MOVE_PERSOPN_PRE("MOVE_PERSON_PRE"),

		/** 個人単位移動 */
		MOVE_PERSON(     "MOVE_PERSON"),
		
		/** 個人単位移動の後処理 */
		MOVE_PERSON_POST("MOVE_PERSON_POST"),

		/** 移動の後処理 */
		MOVE_POST("MOVE_POST"),
		
		/** プレーヤーが死んだ */
		END_GAME_OVER("END_GAME_OVER"),
		
		/** 世界制覇が達成された */
		END_ONE("END_ONE"),
		
		/** 終了 */
		END("END");

		/**
		 * コンストラクタ 
		 * @param name 名前
		 */
		private NOW(String name) {
			this.name = name;
		}
		/** 文字情報（イベント用） */
		public final String name;
	}
	
	/** ボタン選択処理の種別 */ 
	public enum ACTION_TYPE {
		/** 停止処理 */
		IGNRE,
		
		/** 終了ボタンを押したとき */
		TURN_END,
		
		/** stopボタンを押したとき */
		EXTRNAL_TURNEND,
		
		/** autoボタンを押したとき */
		EXTRNAL_TURNSTART,
		
		/** スピードアップボタンを押したとき */
		SPEED_UP
	}

	/** コンストラクタ */
	public State() {
		this.targetLocationList = new ArrayList<EventLocation>();
		this.personMoveList =  new ArrayList<EventPerson>();
		this.contryMoveList = new ArrayList<EventContry>();
		this.messageList = new ArrayList<Message>();
		this.bQueue = new LinkedBlockingDeque<Runnable>();
	}
	
	/** 実処理部分 */
	public void run() {
		//
		// 共通状態設定
		now = NOW.START_PRE;
		turnEndFlag = false;
		skipTurnFlag = false;
		speedUpFlag = 0;
	    d = new XMLData();
		//
	    fInvoker = new FrameInvokMediator(State.this);
	    fInvoker.createFrameToSwing();
		//
		// 強制描画
	    fInvoker.showBaseToSwing(this.bigEvent,false,false);
		//
		//
		turn = 0;
		//
		//
		while (true) {
			long speed = 100;
			//
			if (!messageList.isEmpty()) {
				// イベント発生
				speed = 3000;
				startEvent();
			} else {
				//
				/// イベント以外の処理
				mainLoop();
				//
				//イベントのチェック
				findEvent();
				//
				if ((now == NOW.SELECT_CONT_WAIT)
						|| (now == NOW.START)
						|| (now == NOW.END)) {
					speed = Integer.MAX_VALUE;
				}
			}
			
			// 速度の変更
			if ((200 < speed) && (now != NOW.END)) {
				if (speedUpFlag <= 0) {
					speed = Integer.MAX_VALUE;
				} else if (speedUpFlag <= 1) {
					speed = 50000;
				} else if (speedUpFlag <= 2) {
					speed = 1000;
				} else if (speedUpFlag <= 3) {
					speed = 100;
				} else if (speedUpFlag <= 4) {
					speed = 10;
				}
			}
			try {
				//
				// 待ちを入れる処理
				Runnable data = bQueue.poll(speed,TimeUnit.MILLISECONDS);
				if (data != null) {
					data.run();
					boolean showTableEtc = this.getNow() == NOW.SELECT_CONT_WAIT;
					fInvoker.showBaseToSwing(this.bigEvent,showTableEtc,showTableEtc);
				}
			} catch (InterruptedException e) {
				/*EMPTY*/
			}
    	}
	}
	
	/**
	 * メインループ
	 */
	public void mainLoop() {
		// その他のチェック
		switch (this.getNow()) {
		case START_PRE:
			now = NOW.START; 
			break;
		case START:
			now = NOW.START_POST; 
			break;
		case START_POST:
			now = NOW.MAP_PRE; 
			break;
		case MAP_PRE:
			now = NOW.MAP_CREATE; 
			break;
		case MAP_CREATE:
			createMap();
			timer = 0;
			now = NOW.MAP_MOVE; 
			break;
		case MAP_MOVE:
			timer++;
			fInvoker.changeMapToSwing();
			fInvoker.showBaseToSwing(this.bigEvent,false,false);
			if (50 < timer) {
				now = NOW.MAP_POST;
			}
			break;
		case MAP_POST: // イベントで引っ掛ける用
			now = NOW.SELECT_PRE; 
			break;
		case SELECT_PRE:
			turn++;
			addMessage(Message.TYPE.NAL,"なるみや「ターン " + turn + " 開始します」");
			this.contryMoveList.addAll(d.getContryList());
			myContry = null;
			now = NOW.SELECT_CONT_PRE; 
			break;
		case SELECT_CONT_PRE:
			// 死人チェック
			if (!this.contryMoveList.isEmpty()) {
				myContry = this.contryMoveList.remove(rand.nextInt(this.contryMoveList.size()));
				myPerson = null;
				if (!myContry.isAlive()) {
					// 生存者がいない
					if (createSecondLife()) {
						// 復活できた
						now = NOW.SELECT_CONT;
					} else {
						// 復活できない
						now = NOW.SELECT_CONT_POST;
					}
				} else {
					now = NOW.SELECT_CONT;
				}
			} else {
				// 全ての国をチェックした
				this.now = NOW.SELECT_POST;
			}
			break;
		case SELECT_CONT:
			// ターン先頭
			fInvoker.showMapToSwing();
			if (myContry.isPc()) {
				addMessage(Message.TYPE.FRIEND,"なるみや「" + myContry.getTitle() + "のターンだよ」");
			} else {
				addMessage(Message.TYPE.ENEMY,"なるみや「" + myContry.getTitle() + "のターンを開始します」");
			}
			//
			// 行動処理本体
			thinkingTime();
			//
			if (myContry.isPc() && (!skipTurnFlag)) {
				now = NOW.SELECT_CONT_WAIT_PRE;
			} else {
				this.targetLocationList.clear();
				this.targetLocationList.addAll(myContry.getLocationSet());
				now = NOW.SELECT_CONT_POST;
			}
			break;
		case SELECT_CONT_WAIT_PRE:
			// 表データの更新
			fInvoker.updateTableValueToSwing();
			now = NOW.SELECT_CONT_WAIT;
			// 状態表示
			fInvoker.showBaseToSwing(this.bigEvent,true,true);
			break;
		case SELECT_CONT_WAIT:
			if (turnEndFlag) {
				turnEndFlag = false;
				now = NOW.SELECT_CONT_WAIT_POST;
			}
			break;
		case SELECT_CONT_WAIT_POST:
			if (myContry.isPc()) {
				addMessage(Message.TYPE.FRIEND,"なるみや「" + myContry.getTitle() + "のターンを終えました」");
			} else {
				addMessage(Message.TYPE.ENEMY,"なるみや「" + myContry.getTitle() + "のターンが終わりました」");
			}
			this.now = NOW.SELECT_CONT_POST;
			break;
		case SELECT_CONT_POST:
			if (this.d.getContryList().stream()
					.filter(cont->cont.isPc())
					.mapToInt(cont->cont.getLocationSet().size()).sum()
					<= 0){
				// 担当国家で支配地域が１つもない
				this.now = NOW.END_GAME_OVER;
			} else if (this.d.getContryList().stream()
					.filter(cont-> 0 < cont.getLocationSet().size())
					.mapToInt(cont->1).sum() <= 1) {
				// 残国が１つしかいないのなら世界征服終了！
				this.now = NOW.END_ONE;
			} else {
				// 国の最初に戻る
				this.now = NOW.SELECT_CONT_PRE;
			}
			break;
		case SELECT_POST:
			this.now = NOW.MOVE_PRE;
			break;
		case MOVE_PRE:
			now = NOW.MOVE_PERSOPN_PRE;
			List<EventPerson> personList = this.d.getContryList().stream()
			.flatMap(contry->contry.getPersonList().stream())
			.filter(e->e.isAlive())
			.collect(Collectors.toList());
			this.personMoveList.addAll(personList);
			//
			addMessage(Message.TYPE.NAL,"なるみや「すべての国の行動方針が決定されました」");
			break;
		case MOVE_PERSOPN_PRE:
			myPerson = personMoveList.remove(rand.nextInt(personMoveList.size()));
			now = NOW.MOVE_PERSON;
			break;
		case MOVE_PERSON:
			invokePerson();
			now = NOW.MOVE_PERSON_POST;
			break;
		case MOVE_PERSON_POST:
			this.targetLocationList.clear();
			if (personMoveList.isEmpty()) {
				now = NOW.SELECT_PRE;
			} else {
				now = NOW.MOVE_PERSOPN_PRE;
			}
			break;
		case MOVE_POST:
			addMessage(Message.TYPE.NAL,"なるみや「全員の行動が終わりました」");
			this.now = NOW.SELECT_PRE;
			break;
		case END_GAME_OVER:
		case END_ONE: // ここでイベントを引っ掛ける
			this.now = NOW.END;
			break;
		case END:
			// 表データの更新
			fInvoker.updateTableValueToSwing();
			// 結果表示(自国にして表示)
			myContry = this.d.getContryList().get(0);
			myContry.setPc(true);
			fInvoker.showBaseToSwing(this.bigEvent,true,false);
			break;
		default:
		}
		//
	}
	
	/**
	 * イベントメッセージを表示させる
	 */
	public void startEvent() {
		Message messageClass = messageList.remove(0);
		String message = messageClass.getMessage();
		//
		String title = "ターン" + turn;
		EventPerson selectPerson = myPerson;
		if (myPerson == null) {
			if ((myContry != null) && (!myContry.getArivePersonList().isEmpty())) {
				selectPerson = myContry.getArivePersonList().get(0);
			}
		}
		if (selectPerson != null) {
			title = title + "/" + selectPerson.getTitle();
			message = message.replace("%a",selectPerson.getTitle());
			if (selectPerson.getSelection() != null) {
				title = title + "/" + selectPerson.getSelection().title;
				message = message.replace("%d",selectPerson.getSelection().title);
			}
		}
		if (myContry != null) {
			title = title + "/" + myContry.getTitle();
			message = message.replace("%b",myContry.getGobi());
			message = message.replace("%c",myContry.getTitle());
		}
		//
		this.bigEvent = (messageClass.getBigEventFileName() != null)
				&& (!messageClass.getBigEventFileName().equals(""));
		fInvoker.addMessageToSwing(
				messageClass.getType(),
				title,
				message,
				messageClass.getMiniEventFileName(),
				messageClass.getBigEventFileName());
		fInvoker.showBaseToSwing(this.bigEvent,false,false);
	}
	
	/** 現状の取得
	 * @return 現状
	 */
	public State.NOW getNow() {
		return this.now;
	}
	/**
	 * マウスがクリックされた 
	 * @param type クリックされたマウスイベントの内容
	 */
	public void action(ACTION_TYPE type) {
		try {
			bQueue.put(()->{
				if (type == ACTION_TYPE.SPEED_UP) {
					speedUpFlag = speedUpFlag < 3 ? speedUpFlag + 1: 0;
				} else if (type == ACTION_TYPE.IGNRE) {
					speedUpFlag = 0;
				} else if (type == ACTION_TYPE.EXTRNAL_TURNSTART) {
					this.skipTurnFlag = false;
				} else if (type == ACTION_TYPE.EXTRNAL_TURNEND) {
					this.skipTurnFlag = true;
				} else {
					this.turnEndFlag = true;
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 選択肢の反映ボタンが押された
	 * @param selectRate レート情報
	 */
	public void action(int[] selectRate) {
		try {
			bQueue.put(()->{
				for (int j = 0 ; j < selectRate.length ; j++) {
					myContry.selectRate[j] = selectRate[j];
				}
				//
				// 再度学習
				thinkingTime();
				//
				// テーブルに反映
				fInvoker.updateTableValueToSwing();
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 標的地域が選択された 
	 * @param targetLocationList 標的地域
	 * @param person 人
	 */
	public void action(List<EventLocation> targetLocationList,EventPerson person) {
		try {
			bQueue.put(()->{
				if (targetLocationList != null) {
					this.targetLocationList.clear();
					this.targetLocationList.addAll(targetLocationList);
				}
				this.myPerson = person;
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 現在のカ国情報 
	 * @return 現在の国情報
	 */
	public EventContry getMyContry() {
		return myContry;
	}
	/**
	 * イベントの検索 
	 * @return マッチしたイベント
	 */
	protected EventSinario findEvent() {
		if (now == NOW.MAP_MOVE) {
			return null; // ここではイベントは絶対に起きない
		}
		int priority = Integer.MAX_VALUE;
		EventSinario sinario = null;
		for (EventSinario child : d.getSinarioList()) {
			boolean sinarioNow = true;
			//
			// 親チェック
			Event parent = child.getParentEvent();
			if (parent != null) {
				if ((parent instanceof EventContry) &&
						((myContry == null) || (parent != myContry))) {
					sinarioNow = false;
				} else if ((parent instanceof EventPerson) &&
						((now != NOW.MOVE_PERSON_POST) || (myPerson == null) || (parent != myPerson))) {
					sinarioNow = false;					
				} else if (parent instanceof EventLocation) {
					if  ((this.myPerson == null)
							|| (this.myPerson.getToLocation() == null)
							|| (this.myPerson.getToLocation() != parent)) {
						sinarioNow = false;	
					}
				} else if ((parent instanceof EventSinario)
							&& (((EventSinario)parent).getCount() <= 0)) {
					// 親のシナリオがまだ実行されていない
					sinarioNow = false;
				}
			}
			//
			// 条件チェック
			for (IFData ifData : child.getIfDataList()) {
				if (!sinarioNow) {
					break;
				}
				switch (ifData.getName()) {
				case "1ST":
					// 条件：実行回数（０のときしかありえないはず)
					if (child.getCount() != 0) {
						sinarioNow = false;
					}
					break;
				case "CONT_PRI":
					// 国チェック
					if ((myContry != null)
						&& (!myContry.isPc())) {
						sinarioNow = false;
					}
					break;
				case "TURN":
					if (ifData.getValueInt() != this.turn) {
						sinarioNow = false;
					}
					break;
				case "NOW":
					// 実行処理
					String target = this.getNow().name;
					if (!ifData.getValue().equals(target)) {
						sinarioNow = false;
					}
					break;
				case "SELECT":
					// 条件に従ってイベントが発生する
					if (now == NOW.MOVE_PERSON_POST) {
						if ((myPerson.getSelection() == null)
								|| (!ifData.getValue().equals(myPerson.getSelection().signature))) {
							sinarioNow = false;
						}
					} else {
						// 人の行動が終わった時以外はfalse
						sinarioNow = false;
					}
					break;
				default:
					System.out.println("unkown if name=" + ifData.getName());
				}
			}
			// 
			if (sinarioNow) {
				int priNow = child.getPriority();
				if (priNow < priority) {
					priority = priNow;
					sinario = child;
				}
			}
		}
		//
		// シナリオの実行
		if (sinario != null) {
			sinario.setCount(sinario.getCount()+1);
			String img = sinario.getImage();
			sinario.getMessage().stream().forEachOrdered(message->addMessage(message,null,img));
			if (sinario.selectRate != null) {
				for (int i = 0 ; i < myContry.selectRate.length ; i++) {
					myContry.selectRate[i] = sinario.selectRate[i];
				}
			}
		}
		return sinario;
	}
	
	/** 初期マップの生成 */
	protected void createMap() {
		d.getLocationSet().forEach(a->{
			a.setX(rand.nextInt(ImageFactory.WINDOW_MAP_X -6)+3);
			a.setY(rand.nextInt(ImageFactory.WINDOW_MAP_Y-6)+3);
		});
		//
		// 必ず地域を接続する
		Set<EventLocation> toEventList = new TreeSet<EventLocation>();
		List<EventLocation> fromEventList = new LinkedList<EventLocation>(d.getLocationSet());
		while (!fromEventList.isEmpty()) {
			if (toEventList.isEmpty()) {
				EventLocation fromTarget = fromEventList.remove(0);
				toEventList.add(fromTarget);
				fromEventList.remove(fromTarget);
			} else {
				EventLocation fromTarget = null;
				EventLocation toTarge = null;
				int range = Integer.MAX_VALUE;
				for (EventLocation fromEvent : fromEventList) {
					for (EventLocation toEvent : toEventList) {
						int nowRange = fromEvent.getRange(toEvent);
						if (nowRange < range) {
							range = nowRange;
							fromTarget = fromEvent;
							toTarge = toEvent;
						}
					}
				}
				fromTarget.addLocation(toTarge);
				toTarge.addLocation(fromTarget);
				toEventList.add(toTarge);
				toEventList.add(fromTarget);
				fromEventList.remove(toTarge);
				fromEventList.remove(fromTarget);
			}
		}
		//地域に技術力を配布する
		for (EventLocation fromEvent : d.getLocationSet()) {
			fromEvent.setTecnic(rand.nextInt(80) + 20);
		}
		//１か所接続する
		for (EventLocation fromEvent : d.getLocationSet()) {
			//if (3 <= fromEvent.getLocationList().size()) {
			//	continue;
			//}
			EventLocation fromTarget = null;
			EventLocation toTarge = null;
			int range = Integer.MAX_VALUE;
			for (EventLocation toEvent : d.getLocationSet()) {
				if ((fromEvent == toEvent)
						|| fromEvent.getLocationSet().contains(toEvent)) {
					continue;
				}
				int nowRange = fromEvent.getRange(toEvent);
				if (nowRange < range) {
					range = nowRange;
					fromTarget = fromEvent;
					toTarge = toEvent;
				}
			}
			if (fromTarget != null) {
				fromTarget.addLocation(toTarge);
				toTarge.addLocation(fromTarget);
			}
		}
		//
		// 国家を適当に配置する
		List<EventContry> contryList = new ArrayList<EventContry>(this.d.getContryList());
		List<EventLocation> locationList  = new ArrayList<EventLocation>(this.d.getLocationSet());
		while ((!contryList.isEmpty()) && (!locationList.isEmpty())) {
			EventLocation location = locationList.remove(rand.nextInt(locationList.size()));
			EventContry contry = contryList.remove(0);
			//
			// 国に適当な地域を配置する
			contry.addLocation(location);
			//
			// 人に地域を配布する
			contry.getPersonList().stream().forEach(ev->ev.setLocation(location));
		}
		//
		// 友好値を適当に配布する
		this.d.getContryList().stream().forEachOrdered(cont->{
			cont.setLike(new int[this.d.getContryList().size()]);
			for (int i= 0 ; i < cont.getLike().length ; i++) {
				cont.getLike()[i] = rand.nextInt(80) + 20;
			}
		});
	}
	
	/** 国家思考時間 */
	protected void thinkingTime() {
		//
		//支配地域の国の数に技術を足した分だけお金を増やす
		int money = myContry.getMoney()
				+ (myContry.getLocationSet().stream().mapToInt(e->e.getTecnic()).sum()/10);//補正付き
		myContry.setMoney(money);
		//
		// 生きている人にランダムに行動を配布
		this.myContry.getArivePersonList().stream().forEachOrdered(person->thinkingTimeOne(person));
	}
	
	/**
	 * 人ひとりの行動決定 
	 * @param person 対象となる人
	 */
	protected void thinkingTimeOne(EventPerson person) {
		//
		// 選択肢の決定
		int sum = 0;
		for (int i = 0 ; i < myContry.selectRate.length ; i++) {
			sum += myContry.selectRate[i];
		}
		// 選択肢の合計が0の場合、数値を配布しなおし
		if (sum <= 0) {
			sum = 0;
			for (int i = 0 ; i < myContry.selectRate.length ; i++) {
				int plus = rand.nextInt(9) + 1;
				myContry.selectRate[i] = plus;
				sum += plus;
			}
		}
		// 合計値が決定
		final int fSum = sum;
		//
		int randInt = rand.nextInt(fSum);
		int targetSelect = 0;
		int randIntSum = 0;
		for (int i = 0 ; i < myContry.selectRate.length ; i++) {
			randIntSum += myContry.selectRate[i];
			if (randInt < randIntSum) {
				targetSelect = i;
				break;
			}
		}
		//
		SelectionData sd = SelectionData.DATA[targetSelect];
		//
		// 特殊条件の設定
		if ((person.getMp() + sd.mp <= 0) && (sd.mp < 0)) {
			// mpが足りるかチェック
			// 増える方向のものは実行できる
			// MPがない場合は休憩する
			sd = SelectionData.getSignatureToSelectionData(SelectionData.SG.SL);
		} else if (!person.getContry().getLocationSet().contains(person.getLocation())) {
			// 敵国にいるなら
			// 自動的に戦うを選択
			sd = SelectionData.getSignatureToSelectionData(SelectionData.SG.FT);
		} else if (person.getContry().getMoney() - (50 * person.getContry().getArivePersonList().size()) <= 0) {
			// お金が足りるかチェック(国家予算が人数の100を切ったら非常事態だろう)
			sd = SelectionData.getSignatureToSelectionData(SelectionData.SG.HN);
		}
		//
		// 動作内容
		person.setSelection(sd);
		//
		// 移動先決定
		d.invokeMovePersion(person);

	}
	
	
	/**
	 * 人ひとりの実際の行動
	 * @param person
	 */
	protected void invokePerson() {
		// 国情報を設定
		this.myContry = myPerson.getContry();
		this.targetLocationList.clear();
		this.targetLocationList.add(myPerson.getLocation());
		//
		if (!myPerson.isAlive()) {
			addMessage("%aはもう死んでいる。",null,null);
			return;
		}
		// ↑までで行動決定
		// 行動内容をイベントに追加
		addMessage(
				myPerson.getContry().getTitle() + "所属:"
				+ myPerson.getTitle() + "は「"
				+ myPerson.getSelection().title + "」した%b");
		//
		// mpの消費
		int mp = myPerson.getStr() < 5 ?  myPerson.getStr() : 5; //補正
		mp = mp + myPerson.getSelection().mp ;
		if ((myPerson.getMp() + mp <= 0) && (mp < 0)) {
			// 自身のMPが０以下でも行動が増える方向のものは実行できる
			addMessage("%aには行動するためのMPがなかった%b");
			myPerson.setMp(myPerson.getMp() + 10); // 回復させてあげる
			return;
		}
		//
		// 仮想敵国の設定
		EventContry enemyContry = this.d.getEnemyContry(myContry);
		if (enemyContry == null) {
			addMessage("%aに敵はいなかった%b");// もう世界征服してまんがな
			return;
		}
		//
		// enemyの友好度は？
		int enemyLike = enemyContry.getLike()[myContry.getContryNumber()];
		//
		// パラメータ更新フェーズ
		int yuukou;
		int tekiTekiYukou;
		int tec;
		int money;
		if (0 < enemyLike) {
			// 友好的なとき
			yuukou = this.myPerson.getSelection().yuukouA;
			tekiTekiYukou = this.myPerson.getSelection().tekiTekiYukouA;
			tec = this.myPerson.getSelection().tecA;
			money = this.myPerson.getSelection().moneyB;
		} else {
			// 親しみを持っていないとき
			yuukou = this.myPerson.getSelection().yuukouB;
			tekiTekiYukou = this.myPerson.getSelection().tekiTekiYukouB;
			tec = this.myPerson.getSelection().tecB;
			money = this.myPerson.getSelection().moneyB;
		}
		// お金があるかチェック
		if ((myContry.getMoney() <= 0) && (money < 0)) {
			// お金が減る方向で、かつ、お金がマイナスになる方向のときはNG
			addMessage("%aには行動するお金がなかった%b");
			return;
		}
		//
		// まずは移動
		EventLocation toLocation = this.myPerson.getToLocation();
		if (toLocation != null) {
			addMessage("%aは " + this.myPerson.getLocation() + " から " + toLocation + " へ移動した");
			this.myPerson.setLocation(toLocation);
			this.targetLocationList.add(toLocation);
		}
		//
		//攻撃フェーズ
		// まず敵を探す
		List<EventPerson> vsPersonList =
				d.getContryList().stream()
				.filter(contry->contry != myPerson.getContry()) // 自国は除く
				.flatMap(cont->cont.getArivePersonList().stream())
				.filter(person->0 < person.getHp())
				.filter(person->person.getLocation() == myPerson.getLocation())
				.collect(Collectors.toList());
		if ( ! vsPersonList.isEmpty()) {
			// 敵の人からもっとも弱いやつを探す
			EventPerson vsPerson = vsPersonList.get(0); // 選択なし対策
			for (EventPerson targetPerson : vsPersonList) {
				if ((0 < targetPerson.getHp())
						&& (targetPerson.getHp() <= vsPerson.getHp())) {
					vsPerson = targetPerson; // 最もHPの低いやつを優先して叩く
				}
			}
			// 敵の自国の友好度を-2減らす
			vsPerson.getContry().getLike()[myPerson.getContry().getContryNumber()]
					+= - 2;

			// その地域の技術を 減らす
			myPerson.getLocation().setTecnic(myPerson.getLocation().getTecnic()-20);
			
			// 他国でその場所に人がいるか確認
			int dummage = rand.nextInt(30) + 10 + myPerson.getStr();
			vsPerson.setHp(vsPerson.getHp() - dummage);
			//
			addMessage("%aは" + vsPerson + "に" + dummage + "のダメージを与えた!残("
					+ vsPerson.getHp() + ")",ImageFactory.EVENT_ATTACK,null);
			int tuika = myPerson.getSelection().move;
			if ((1 <= tuika) && (tuika <= 4)) {
				tuika = rand.nextInt(20) + 1 + myPerson.getStr()*2;
				vsPerson.setHp(vsPerson.getHp() - dummage);
				addMessage("攻撃選択による追加" + tuika + "のダメージを与えた!残("
						+ vsPerson.getHp() + ")",ImageFactory.EVENT_ATTACK,null);
			}
			// 死んだ判定
			if (!vsPerson.isAlive()) {
				addMessage(
						vsPerson.getContry() + "所属:"
								+ vsPerson + "は死んだ",ImageFactory.EVENT_DEAD,null);
				//
				// もう一度敵を探す(その土地の敵が全滅しているか確認)
				vsPersonList =
						d.getContryList().stream()
						.filter(contry->contry != myPerson.getContry()) // 自国は除く
						.flatMap(cont->cont.getArivePersonList().stream())
						.filter(person->person.isAlive())
						.filter(person->person.getLocation() == myPerson.getLocation())
						.collect(Collectors.toList());
			}
		}
		if (vsPersonList.isEmpty()) {
			//もしいなければ
			if (!myPerson.getContry().getLocationSet().contains(myPerson.getLocation())) {
				// 自国にその土地を含んでいないなら以下を実行
				//
				List<EventContry> enemyContryList = d.getContryList().stream()
						.filter(contry->contry != myPerson.getContry()) // 自国は除く
						.filter(cont->cont.getLocationSet().contains(myPerson.getLocation()))
						.collect(Collectors.toList());
				if (enemyContryList.isEmpty()) {
					// だれもいない
					addMessage(myPerson.getTitle() + "は" + myPerson.getLocation() + "を領土と宣言した%b",ImageFactory.EVENT_VOTE,null);
					myPerson.getContry().addLocation(myPerson.getLocation());
				} else {
					enemyContryList.stream().forEachOrdered(toContry->{
						// だれもいない
						addMessage("%aは" + myPerson.getLocation() + "を" + toContry + "から奪取した",ImageFactory.EVENT_GET,null);
						myPerson.getContry().getLocationSet().add(myPerson.getLocation());
						toContry.getLocationSet().remove(myPerson.getLocation());
					});
				}
			}
		}
		//
		// 生産フェーズ(増える)
		if ((myPerson.getSelection().move == 5)
				&& (80 - myPerson.getStr() < myPerson.getLocation().getTecnic())) {
			// 技術力が低いと仲間は増えません
			List<EventPerson> friendList =
					myPerson.getContry().getPersonList().stream()
					.filter(son->!son.isAlive())
					.collect(Collectors.toList());
			//
			//
			if (!friendList.isEmpty()) {
				EventPerson vsPerson = friendList.get(rand.nextInt(friendList.size()));
				vsPerson.setHp(50);
				vsPerson.setMp(30);
				vsPerson.setLocation(myPerson.getLocation());
				addMessage("%aは" + vsPerson + "を" + myPerson.getLocation() + "で仲間にした");
				//
				// 技術力は人が引っこ抜かれるので減ります
				// その地域の技術を減らす
				myPerson.getLocation().setTecnic(myPerson.getLocation().getTecnic()-40);
			}
		}
		
		//
		// ステータス変更
		// ここでHP消費
		myPerson.setHp(myPerson.getHp() + this.myPerson.getSelection().hp);
		//
		// ここでMP消費
		myPerson.setMp(myPerson.getMp() + mp); 
		// お金
		myContry.setMoney(myContry.getMoney() + money);
		//
		// 技術力
		myPerson.getLocation().setTecnic(myPerson.getLocation().getTecnic() + tec);
		//
		// 友好度調整
		// // 敵の友好度の調整
		// // 他国から見た状況
		for (EventContry targetContry: d.getContryList()) {
			if (targetContry == myPerson.getContry()) {
				// 敵の敵国＝自国を調整（半分を加算)
				targetContry.getLike()[enemyContry.getContryNumber()]
						+= (yuukou / 2);
			} else if (targetContry == enemyContry){
				targetContry.getLike()[myPerson.getContry().getContryNumber()]
						+= yuukou;
			} else {
				// 他国から見て、敵国が友好度に応じて自国を調整
				// 他国の敵国の友好度
				int yuuko = targetContry.getLike()[enemyContry.getContryNumber()];
				if (0 < yuuko) {
					// 敵国が好きな国(敵国の味方)
					targetContry.getLike()[myPerson.getContry().getContryNumber()]
							-= tekiTekiYukou;
				} else {
					// 敵国の敵
					targetContry.getLike()[myPerson.getContry().getContryNumber()]
							+= tekiTekiYukou;
				}
			}
		}
	}

	/**
	 * セカンドライフのチェック 
	 * @return 復活させることができたのであれば true
	 */
	protected boolean createSecondLife() {
		if (!myContry.getLocationSet().isEmpty()) {
			// まだ領地がある
			EventLocation location =
					myContry.getLocationSet().stream()
					.collect(Collectors.toList()).get(0);
			addMessage(Message.TYPE.NAL,"なるみや「" + myContry.getTitle() + "が全滅しましので"
					+ location + "で復活させました」",
					ImageFactory.EVENT_CREATE,null);
			addMessage(Message.TYPE.NAL,"なるみや「まだまだいきますよー」",ImageFactory.EVENT_CREATE,null);
			EventPerson person = myContry.getPersonList().get(0);
			person.setHp(1);
			person.setMp(1);
			person.setLocation(location);
			return true;
		} else {
			// 空いている土地を探す
			List<EventLocation> contryLocationList =
					this.getData().getContryList().stream()
					.flatMap(cont->cont.getLocationSet().stream())
					.collect(Collectors.toList());
			List<EventLocation> nonContryLocationList =
					new ArrayList<EventLocation>(this.getData().getLocationSet());
			nonContryLocationList.removeAll(contryLocationList);
			if (!nonContryLocationList.isEmpty()) {
				EventLocation location = nonContryLocationList.get(rand.nextInt(nonContryLocationList.size()));
				addMessage(Message.TYPE.NAL,
						"なるみや「" + myContry.getTitle() + "が全滅しましので新たに"
						+ location.getTitle()
						+ "で復活させました」",
						ImageFactory.EVENT_CREATE,null);
				addMessage(Message.TYPE.NAL,"なるみや「土地の有効利用ですね」",ImageFactory.EVENT_CREATE,null);
				myContry.getLocationSet().add(location);
				EventPerson person = myContry.getPersonList().get(0);
				person.setHp(1);
				person.setMp(1);
				person.setLocation(location);
				return true;
			}
		}
		return false;
	}

    /**
     * 標的の地域。
     * マップ表示で主に使われる
     * @return 地域情報
     */
    public List<EventLocation> getTargetLocationList() {
    	return this.targetLocationList;
    }
    /**
     * 人の情報  
     * @return 現在作業中の人の情報
     */
    public EventPerson getMyPerson() {
    	return this.myPerson;
    }
    
	/**
	 * XMLデータの取得 
	 * @return 地域情報
	 */
    public XMLData getData() {
    	return d;
    }
    
    /**
     * スピードアップフラグの取得 
     * @return 結果
     */
    public int getSpeedUpFlag() {
    	return this.speedUpFlag;
    }
    /**
     * 通常メッセージの設定 
     * @param message メッセージ 
     */
    public void addMessage(String message) {
    	addMessage(message,null,null);
    }
    /**
     * 通常メッセージの設定 
     * @param type メッセージ種別
     * @param message メッセージ
     */
    public void addMessage(Message.TYPE type,String message) {
    	addMessage(type,message,null,null);
    }
    /**
     * 通常メッセージの設定 
     * @param message メッセージ
     * @param miniEventFile ミニイベント画像
     * @param bigEvent でかいイベント画像
     */
    public void addMessage(String message,String miniEventFile,String bigEvent) {
    	addMessage(getMessageType(),message,miniEventFile,bigEvent);
    }
    /**
     * 通常メッセージの設定 
     * @return メッセージ色
     */
    public Message.TYPE getMessageType() {
    	Message.TYPE type;
    	if (this.myPerson == null) {
    		if (this.myContry == null) {
    			type = Message.TYPE.NAL;
    		} else if (this.myContry.isPc()) {
        		type = Message.TYPE.FRIEND;
        	} else {
        		type = Message.TYPE.ENEMY;
        	}
    	} else if (this.myPerson.getContry().isPc()) {
    		type = Message.TYPE.FRIEND;
    	} else {
    		type = Message.TYPE.ENEMY;
    	}
    	return type;
    }
    /**
     * 通常メッセージの設定 
     * @param type 種別
     * @param message メッセージ
     * @param miniEventFile ミニイベント画像
     * @param bigEvent でかいイベント画像
     */
    public void addMessage(Message.TYPE type,String message,String miniEventFile,String bigEvent) {
    	messageList.add(new Message(type,message,miniEventFile,bigEvent));
    }
	
	/** タイマー */
	private int timer = 0;

    /** 乱数発生用 */
    private final Random rand = new Random();
    
	/** フレーム処理 */
	private FrameInvokMediator fInvoker;

	/** 待ち用 */
	private final BlockingQueue<Runnable> bQueue;

	/** 現在の状態 */
	private NOW now;
	
	/** XMLデータ */
	private XMLData d;

	/** 標的国家の表示(表示用) */
	private final List<EventLocation> targetLocationList;
	
	/** 現在のメッセージ */
	private final List<Message> messageList;
	
	/** 人の行動リスト(残っている人) */
	private final List<EventPerson> personMoveList;
	
	/** ターンエンドフラグ */
	private boolean turnEndFlag;
	
	/** 全体のターン番号 */
	private int turn;
	
	/** 国のターン番号 */
	private final List<EventContry> contryMoveList;
	
	/** 国情報 */
	private EventContry myContry;

	/** 人物情報 */
	private EventPerson myPerson;
	
	/** スピードアップ */
	private int speedUpFlag;

	/** スキップフラグ */
	private boolean skipTurnFlag;
	
	/** ビッグイベント開始フラグ */
	private boolean bigEvent;
	
}
