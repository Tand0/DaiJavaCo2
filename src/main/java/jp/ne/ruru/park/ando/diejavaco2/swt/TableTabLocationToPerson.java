package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.EventPerson;
import jp.ne.ruru.park.ando.diejavaco2.State;


/**
 * �n�悩��l��I�����ĕ\������p�̃^�u���Ǘ����܂��B
 * @author ����
 *
 */
public class TableTabLocationToPerson extends TableTabPerson {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableTabLocationToPerson(State state) {
		super(state);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabPerson#getTabName()
	 */
	@Override
	public String getTabName() {
		return "�I��l";
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
		JButton button = new JButton("�I�����ꂽ�n��ɂ���l��\�����܂�");
		button.addActionListener((e)->{
			while(0 < this.getModel().getRowCount()) {
				this.getModel().removeRow(0);
			}
			List<EventLocation> locationList = this.getState().getTargetLocationList();
			if (locationList == null) {
				return;
			}
			this.getState().getData().getContryList().stream()
			.flatMap(contry->contry.getArivePersonList().stream())
			.filter(person->locationList.stream()
						.anyMatch(loc->loc == person.getLocation()))
			.forEach(person->addTable(person));
		});
		panel.add(button,BorderLayout.NORTH);
		panel.add(comp,BorderLayout.CENTER);
		return panel;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabPerson#updateValue()
	 */
	@Override
	public void updateValue() {
		EventPerson[] persons = new EventPerson[this.getModel().getRowCount()];
		for (int i = 0 ; i < persons.length ; i++) {
			persons[i] = (EventPerson)this.getModel().getValueAt(i, 0);
		}
		while(0 < this.getModel().getRowCount()) {
			this.getModel().removeRow(0);
		}
		Arrays.asList(persons).stream().forEach(location->addTable(location));
	}
}
