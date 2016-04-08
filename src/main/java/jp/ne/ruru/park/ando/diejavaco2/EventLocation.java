package jp.ne.ruru.park.ando.diejavaco2;

import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Element;
/**
 * 地域情報
 * @author 安藤
 *
 */
public class EventLocation extends Event {
	/**
	 * イベントコンストラクタ
	 * @param element XMLのエレメント
	 * @param sameNameChek 同じ名前をチェックする用です
	 */
	public EventLocation(Element element,Set<String> sameNameChek) {
		super(element,sameNameChek);
	}
	
	/**
	 * getter x
	 * @return x
	 */
	public int getX() {
		return x;
	}
	/**
	 * setter x
	 * @param x x
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * getter y
	 * @return y
	 */
	public int getY() {
		return y;
	}
	/**
	 * setter y
	 * @param y y
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * 地域情報のリスト
	 * @return 地域情報
	 */
	public Set<EventLocation> getLocationSet() {
		return locationList;
	}
	/**
	 * ロケーションの距離を返す 
	 * @param target 相手ロケーション
	 * @return 距離
	 */
	public int getRange(EventLocation target) {
		return (int)Math.sqrt(
				   (this.getX() - target.getX()) * (this.getX() - target.getX())
				+  (this.getY() - target.getY()) * (this.getY() - target.getY()));
	}
	/**
	 * ロケーションを接続する 
	 * @param target 接続先ロケーション
	 */
	public void addLocation(EventLocation target) {
		if (target == this) {
			return;
		}
		locationList.add(target);
	}

	/**
	 * 技術力の所得
	 * @return 技術力
	 */
	public int getTecnic() {
		return tecnic;
	}

	/**
	 * 技術力の設定
	 * @param tecnic 技術力
	 */
	public void setTecnic(int tecnic) {
		this.tecnic = tecnic;
		if (this.tecnic < 0) {
			this.tecnic = 0;
		} else if (100 < this.tecnic) {
			this.tecnic = 100;
		}
	}
	
	/** x */
	protected int x;
	
	/** y */
	protected int y;
	
	/** ロケーション情報 */
	protected final Set<EventLocation> locationList = new TreeSet<EventLocation>();

	/** 技術力 */
	protected int tecnic;
}

