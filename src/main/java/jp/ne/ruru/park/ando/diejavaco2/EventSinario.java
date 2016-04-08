package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * イベント用データ
 * @author 安藤
 *
 */
public class EventSinario extends Event {
	/**
	 * イベントコンストラクタ
	 * @param element XMLのエレメント
	 * @param parentEvent 親イベント
	 * @param sameNameChek 同じ名前をチェックする用です
	 */
	public EventSinario(Element element,Event parentEvent,Set<String> sameNameChek) {
		super(element,sameNameChek);
		this.parentEvent = parentEvent;
		this.img = element.getAttribute("img");
		//
		String priorityString = element.getAttribute("priority");
		if ((priorityString != null) && (!priorityString.equals(""))) {
			priority = Integer.parseInt(priorityString);
		} else {
			priority = Integer.MAX_VALUE;
		}
		//
		final NodeList nodes = element.getChildNodes();
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
			case "if":{
				String name = e.getAttribute("name");
				String value = e.getAttribute("value");
				ifDataList.add(new IFData(name,value));
				break;
			}
			default:
			}
		});
	}
	
	/**
	 * 条件データの取得
	 * @return 条件データ
	 */
	public List<IFData> getIfDataList() {
		return this.ifDataList;
	}
	
	/**
	 * 親のイベントの取得 
	 * @return 親イベント
	 */
	public Event getParentEvent() {
		return this.parentEvent;
	}
	
	/**
	 * 実行回数の取得
	 * @return 実行回数
	 */
	public int getCount() {
		return count;
	}
	
	/** 実行回数の設定
	 * @param count 実行回数
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * イベントイメージの取得
	 * @return イメージ
	 */
	public String getImage() {
		return this.img;
	}

	/**
	 * プライオリティの取得
	 * @return プライオリティ
	 */
	public int getPriority() {
		return this.priority;
	}
	
	/** 条件置き場 */
	private final List<IFData> ifDataList = new ArrayList<IFData>();
	
	/** 親イベント */
	private final Event parentEvent;

	/** 選択枝を選ぶ確率 */
	public final int priority;

	/** シナリオ実行回数 */
	private int count = 0;

	/** イベント画像置き場 */
	private final String img;
	
	/** 選択枝を選ぶ確率 */
	public int[] selectRate;
	
}
