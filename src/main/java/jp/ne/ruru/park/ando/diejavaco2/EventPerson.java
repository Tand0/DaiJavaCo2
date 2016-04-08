package jp.ne.ruru.park.ando.diejavaco2;

import java.io.File;
import java.util.Random;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * 人データ
 * @author 安藤
 *
 */
public class EventPerson extends Event {
	/**
	 * イベントコンストラクタ
	 * @param element XMLのエレメント
	 * @param sameNameChek 同じ名前をチェックする用です
	 */
	public EventPerson(Element element,Set<String> sameNameChek) {
		super(element,sameNameChek);
		mp = 100;
		location = null;
		toLocation = null;
		
		if (element == null) {
			hp = -1;
			str = 0;
		} else {
			String priorityString = element.getAttribute("priority");
			if ((priorityString != null) && priorityString.equals("0")) {
				hp = 100;
			} else {
				hp = -1;
			}
			String img = element.getAttribute("img");
			if ((img != null) && (img.length() <= 0)) {
				this.iconName = null;
			} else {
				this.iconName = new File(img);
				if (!this.iconName.exists()) {
					this.iconName = null;
				}
			}
			String strString = element.getAttribute("str");
			if ((strString != null) && (!strString.equals(""))) {
				str = Integer.parseInt(strString);
			} else {
				str = ((new Random()).nextInt(5));
			}
		}
	}

	/**
	 * ロケーションの取得
	 * @return ロケーション
	 */
	public EventLocation getLocation() {
		return location;
	}

	/**
	 * この人は生きているか？
	 * @return 生きているときtrue
	 */
	public boolean isAlive() {
		return 0 < hp;
	}
	/**
	 * ロケーションの設定
	 * @param location ロケーション
	 */
	public void setLocation(EventLocation location) {
		this.location = location;
	}
	
	/**
	 * 移動先のロケーションの取得
	 * @return 移動先ロケーション
	 */
	public EventLocation getToLocation() {
		return toLocation;
	}

	/** 移動先ロケーションの設定
	 * @param toLocation 移動先ロケーション
	 */
	public void setToLocation(EventLocation toLocation) {
		this.toLocation = toLocation;
	}
	
	/**
	 * HPの取得
	 * @return HP
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * HPの設定
	 * @param hp HP
	 */
	public void setHp(int hp) {
		if (100 < hp) {
			hp = 100;
		}
		this.hp = hp;
	}

	/**
	 * MPの取得
	 * @return MP
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * MPの設定
	 * @param mp MP
	 */
	public void setMp(int mp) {
		if (100 < mp) {
			mp = 100;
		}
		this.mp = mp;
	}
	
	/**
	 * 所属国の設定
	 * @param contry 所属国
	 */
	public void setContry(EventContry contry) {
		this.contry = contry;
	}
	/**
	 * 所属国の取得
	 * @return 所属国
	 */
	public EventContry getContry() {
		return this.contry;
	}
	
	/**
	 * icon nameの設定
	 * @param iconName icon name
	 */
	public void setIconName(File iconName) {
		this.iconName = iconName;
	}
	
	/**
	 * icon nameの取得
	 * @return icon name
	 */
	public File getIconName() {
		return this.iconName;
	}
	
	/**
	 * 行動内容の取得
	 * @return 行動内容
	 */
	public SelectionData getSelection() {
		return this.selection;
	}
	
	/**
	 * 行動内容の設定
	 * @param selection 行動内容
	 */
	public void setSelection(SelectionData selection) {
		this.selection = selection;
	}
	
	/**
	 * STRの取得。高いほどつおい
	 * @return STR
	 */
	public int getStr() {
		return this.str;
	}
	
	/** 現在地*/
	private EventLocation location;
	
	/** 所属国 */
	private EventContry contry;
	
	/** HP */
	private int hp;
	
	/** MP */
	private int mp;
	
	/** STR */
	private final int str;
	
	/** アイコン */
	private File iconName;
	
	/** 行動内容 */
	private SelectionData selection;
	
	/** 移動先 */
	private EventLocation toLocation;
}

