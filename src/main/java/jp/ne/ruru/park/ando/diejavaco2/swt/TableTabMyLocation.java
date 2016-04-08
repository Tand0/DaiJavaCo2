package jp.ne.ruru.park.ando.diejavaco2.swt;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �����̗̒n��\������p�̃^�u���Ǘ����܂��B
 * @author ����
 *
 */
public class TableTabMyLocation extends TableTabLocation {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
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
		return "���̒n";
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
