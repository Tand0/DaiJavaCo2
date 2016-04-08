package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �e�[�u���p�t���[���̊Ǘ���S�����܂� 
 * @author ����
 */
public class TableFactory {
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableFactory(State state) {
		this.state = state;
	}
	
	/**
	 * �t���[���̐���
	 * @return �t���[��
	 */
	public JInternalFrame createJInternalFrame() {
		this.tableFrame = new JInternalFrame("�f�[�^�e�[�u��",true, true, true, true);
		this.tableFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		this.tableFrame.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();
		//
		this.tabList = new ArrayList<TableTab>();
		//
		this.tabList.add(new TableTabContry(this.state));
		this.tabList.add(new TableTabPerson(this.state));
		this.tabList.add(new TableTabMyLocation(this.state));
		this.tabList.add(new TableTabLocationToLocation(this.state));
		this.tabList.add(new TableTabLocationToNear(this.state));
		this.tabList.add(new TableTabLocationToPerson(this.state));
		this.tabList.add(new TableTabLocation(this.state));
		this.tabList.add(new TableTabSystemContry(this.state));
		//
		this.tabList.stream().forEachOrdered(tab->tab.createTab(tabbedPane));
	    //
		this.tableFrame.add(tabbedPane,BorderLayout.CENTER);
		
		return tableFrame;
	}
	/** �e�[�u�����A�b�v�f�[�g���� */
	protected void updateTableValue() {
		tabList.stream().forEachOrdered(e->{
			e.updateValue();
		});
	}
	/**
	 * �t���[���̕\��
	 * @param enable �\�����邩�H
	 */
	public void showFrame(boolean enable) {
		tabList.stream().forEachOrdered(e->e.updateValue());
		this.tableFrame.setVisible(enable);
	}
	

	/** ��� */
	private final State state;
	
	/** �e�[�u���t���[�� */
	private JInternalFrame tableFrame;
	
	/** �e�[�u���^�u�̃��X�g */
	private List<TableTab> tabList;

}
