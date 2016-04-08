package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

/**
 * 国情報
 * @author 安藤
 *
 */
public class EventContry extends Event {
	/**
	 * イベントコンストラクタ
	 * @param element XMLのエレメント
	 * @param sameNameChek 同じ名前をチェックする用です
	 * @param contryNumber 国番号
	 */
	public EventContry(Element element,Set<String> sameNameChek,int contryNumber) {
		super(element,sameNameChek);
		this.contryNumber = contryNumber;

		if (element == null) {
			pcFlag = false;
			this.gobi = "";
		} else {
			String priorityString = element.getAttribute("priority");
			if ((priorityString != null) && priorityString.equals("0")) {
				pcFlag = true;
			} else {
				pcFlag = false;
			}
			String gobi = element.getAttribute("gobi");
			if (gobi != null) {
				this.gobi = gobi;
			} else {
				this.gobi = "";
			}
		}

		//
		selectRate = new int[SelectionData.DATA.length];
		//
		//
		money = 100;
	}
	/** 
	 * ロケーションを接続する 
	 * @param target ロケーション
	 */
	public void addLocation(EventLocation target) {
		locationList.add(target);
	}
	/**
	 * ロケーションを削除する 
	 * @param target ロケーション
	 */
	public void removeLocation(EventLocation target) {
		locationList.remove(target);
	}
	/**
	 * ロケーションを取得する 
	 * @return ロケーション
	 */
	public Set<EventLocation> getLocationSet() {
		return this.locationList;
	}
	
	/**
	 * お金の取得
	 * @return お金
	 */
	public int getMoney() {
		return money;
	}
	/**
	 * お金の設定
	 * @param money お金
	 */
	public void setMoney(int money) {
		this.money = money;
	}
	/**
	 * 友好値の取得
	 * @return 友好値
	 */
	public int[] getLike() {
		return like;
	}
	/** 友好値の設定
	 * @param like 有効値
	 */
	public void setLike(int[] like) {
		this.like = like;
	}
	
	/**
	 * この国は生きているか？
	 * @return 生きているときtrue
	 */
	public boolean isAlive() {
		return 0 < getArivePersonList().size();
	}
	
	/**
	 * 国情報から存命中の情報を出す
	 * @return 存命中人情報
	 */
	public List<EventPerson> getArivePersonList() {
		return peronList.stream()
				.filter(e->e.isAlive())
				.filter(e->0 < e.getHp())
				.collect(Collectors.toList());
	}
	
	/**
	 * 国情報から人情報を出す 
	 * @return 人情報
	 */
	public List<EventPerson> getPersonList() {
		return peronList;
	}

	/**
	 * 語尾の取得 
	 * @return 語尾
	 */
	public String getGobi() {
		return gobi;
	}
	
	/**
	 * 国番号の取得 
	 * @return 国番号
	 */
	public int getContryNumber() {
		return this.contryNumber;
	}
	
	/**
	 * PCフラグの取得
	 * @return PCフラグ
	 */
	public boolean isPc() {
		return pcFlag;
	}
	/**
	 * PCフラグィの設定
	 * @param pcFlag PCフラグ
	 */
	public void setPc(boolean pcFlag) {
		this.pcFlag = pcFlag;
	}

	/** 国番号 */
	private final int contryNumber;
	
	/** 支配地域 */ 
	private final List<EventPerson> peronList = new ArrayList<EventPerson>();

	/** 支配地域 */
	private final Set<EventLocation> locationList = new TreeSet<EventLocation>();

	/** 国家予算 */
	private int money;
	
	/** 有効値 */
	private int[] like;
	
	/** 語尾 */
	private final String gobi;

	/** 選択枝を選ぶ確率 */
	public final int[] selectRate;
	
	/** PCかどうか？ */
	private boolean pcFlag;
}
