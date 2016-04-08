package jp.ne.ruru.park.ando.diejavaco2;

/** �����f�[�^ */
public class IFData {
	/**
	 *�R���X�g���N�^ 
	 * @param name ������
	 * @param value �����l
	 */
	public IFData(String name,String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * �������̎擾
	 * @return ������
	 */
	public String getName() {
		if (name == null) {
			return "";
		}
		return name;
	}
	
	/**
	 * �����l�̎擾
	 * @return �����l
	 */
	public String getValue() {
		if (value == null) {
			return "";
		}
		return value;
	}
	
	/**
	 * int �����ꂽ�����l�̎擾
	 * @return �����l
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
	
	/** ������ */
	public final String name;
	
	/** �����l */
	public final String value;
}
