package jp.ne.ruru.park.ando.diejavaco2;

/** 条件データ */
public class IFData {
	/**
	 *コンストラクタ 
	 * @param name 条件名
	 * @param value 条件値
	 */
	public IFData(String name,String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * 条件名の取得
	 * @return 条件名
	 */
	public String getName() {
		if (name == null) {
			return "";
		}
		return name;
	}
	
	/**
	 * 条件値の取得
	 * @return 条件値
	 */
	public String getValue() {
		if (value == null) {
			return "";
		}
		return value;
	}
	
	/**
	 * int 化された条件値の取得
	 * @return 条件値
	 */
	public int getValueInt() {
		if ((value == null) || value.equals("")) {
			return 0;
		}
		int ans;
		try {
			ans = Integer.parseInt(value);
		} catch(NumberFormatException e) {
			ans = 0;
		}
		return ans;
	}
	
	/** 条件名 */
	public final String name;
	
	/** 条件値 */
	public final String value;
}
