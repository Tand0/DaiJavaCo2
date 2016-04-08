package jp.ne.ruru.park.ando.diejavaco2.swt;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * 自分の領地を表示する用のタブを管理します。
 * @author 安藤
 *
 */
public class TableTabMyLocation extends TableTabLocation {
	
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public TableTabMyLocation(State state) {
		super(state);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#getTabName()
	 */
	@Override
	public String getTabName() {
		return "自領地";
	}

	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#updateValue()
	 */
	@Override
	public void updateValue() {
		while(0 < getModel().getRowCount()) {
			getModel().removeRow(0);
		}
		if (getState().getMyContry() == null) {
			return ;
		}
		getState().getMyContry().getLocationSet().stream().forEach(location->this.addLocation(location));
	}
}
