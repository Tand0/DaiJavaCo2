package jp.ne.ruru.park.ando.diejavaco2;


/**
 * メッセージ用データ
 * @author 安藤
 */
public class Message {
	
	/** メッセージ種別 */
	public enum TYPE {

		/** オペレータのメッセージ */
		NAL,
		
		/** 自分のメッセージ */
		FRIEND,
		
		/** 敵のメッセージ */
		ENEMY
	}
	
	/**
	 * コンストラクタ
	 * @param type メッセージ種別
	 * @param message メッセージの内容
	 * @param miniEventFileName ミニメッセージイベント用
	 * @param bigEventFileName でかいイベント表示イベント用
	 */
	public Message(TYPE type,String message,String miniEventFileName,String bigEventFileName) {
		this.type = type;
		this.message = message;
		this.miniEventFileName = miniEventFileName;
		this.bigEventFileName = bigEventFileName;
	}
	
	/**
	 * メッセージ種別の取得 
	 * @return 種別
	 */
	public TYPE getType() {
		return type;
	}
	
	/**
	 * メッセージの取得
	 * @return メッセージ
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * ミニイベント画像の取得
	 * @return ミニイベント画像
	 */
	public String getMiniEventFileName() {
		return miniEventFileName;
	}
	
	/**
	 * でかいイベント画像の取得
	 * @return でかいイベント画像
	 */
	public String getBigEventFileName() {
		return bigEventFileName;
	}
	
	/** メッセージ種別 */
	private final TYPE type;
	
	/** メッセージ */
	private final String message;
	
	/** ミニメッセージ画像 */
	private final String miniEventFileName;
	
	/** でかいイベント画像 */
	private final String bigEventFileName;
}
