package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �אڒn���\������p�̃^�u���Ǘ����܂��B
 * @author ����
 *
 */
public class TableTabLocationToNear extends TableTabLocation {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableTabLocationToNear(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#getTabName()
	 */
	@Override
	public String getTabName() {
		return "���Ӓn";
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
		JButton button = new JButton("�I�����ꂽ�n��̎��Ӓn���\�����܂�");
		button.addActionListener((e)->{
			while(0 < this.getModel().getRowCount()) {
				this.getModel().removeRow(0);
			}
			if (getState().getTargetLocationList() != null) {
				Set<EventLocation> locationSet =
					getState().getTargetLocationList().stream()
					.flatMap(location->location.getLocationSet().stream())
					.collect(Collectors.toSet());
				List<EventLocation> locationList = new ArrayList<EventLocation>(locationSet);
				locationList.removeAll(getState().getTargetLocationList());
				locationList.stream().forEach(location->addLocation(location));
				//
				//�}�b�v�ɔ��f
				getState().action(locationList,null);
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
