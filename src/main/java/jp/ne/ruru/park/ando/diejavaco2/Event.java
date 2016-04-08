package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * イベント用データ
 * @author 安藤
 */
public class Event implements Comparable<Event> {
	/**
	 * イベントコンストラクタ
	 * @param element XMLのエレメント
	 * @param sameNameChek 同じ名前をチェックする用です
	 */
	public Event(Element element,Set<String> sameNameChek) {
		if (element == null) {
			title = "none";
		} else {
			title = element.getAttribute("title");
		}
		while (true) {
			if (!sameNameChek.contains(title)) {
				sameNameChek.add(title);
				break;
			}
			System.out.println("Same title found! titile=" + title);
			title = title + "*";
		}
	}
	/**
	 * タイトルの取得
	 * @return タイトル
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * メッセージの追加
	 * @param message 追加するメッセージ
	 */
	public void addMessage(String message) {
		messageList.add(message);
	}
	/**
	 * メッセージの取得
	 * @return メッセージ
	 */
	public List<String> getMessage() {
		return messageList;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Event o) {
		if (o == this) {
			return 0;
		}
		return o.getTitle().compareTo(this.getTitle());
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if ((title == null) && title.equals("")) {
			return super.toString();
		}
		return title;
	}
	/** タイトル */
	private String title;

	/** メッセージ */
	private final List<String> messageList = new ArrayList<String>();

}
