package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �I��n���\������p�̃^�u���Ǘ����܂��B
 * @author ����
 *
 */
public class TableTabLocationToLocation extends TableTabLocation {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableTabLocationToLocation(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#getTabName()
	 */
	@Override
	public String getTabName() {
		return "�I��n";
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#createCompJTable()
	 */
	@Override
	protected JComponent createCompJTable() {
		JComponent comp = super.createCompJTable();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JButton button = new JButton("�I�����ꂽ�n���\�����܂�");
		button.addActionListener((e)->{
			while(0 < this.getModel().getRowCount()) {
				this.getModel().removeRow(0);
			}
			if (getState().getTargetLocationList() != null) {
				getState().getTargetLocationList().stream()
				.forEach(location->addLocation(location));
			}
		});
		panel.add(button,BorderLayout.NORTH);
		panel.add(comp,BorderLayout.CENTER);
		return panel;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#updateValue()
	 */
	@Override
	public void updateValue() {
		EventLocation[] locations = new EventLocation[this.getModel().getRowCount()];
		for (int i = 0 ; i < locations.length ; i++) {
			locations[i] = (EventLocation)this.getModel().getValueAt(i, 0);
		}
		while(0 < this.getModel().getRowCount()) {
			this.getModel().removeRow(0);
		}
		Arrays.asList(locations).stream().forEach(location->addLocation(location));
	}
}
