package jp.ne.ruru.park.ando.diejavaco2.swt;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.ne.ruru.park.ando.diejavaco2.State;


/**
 * �e�[�u���\���p�^�u�̊��N���X
 * @author ����
 */
public abstract class TableTab {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableTab(State state) {	
		this.state = state;
	}
	
	/**
	 * �e�[�u�����̎擾 
	 * @return �e�[�u���R���e�i
	 */
	protected JTable createJTable() {
		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		return table;
	}
	
	/**
	 * �X�N���[���R���e�i�̎擾�����T�u�N���X�ŃX�`�[������p
	 * @return swing�R���e�i
	 */
	protected JComponent createCompJTable() {
		return new JScrollPane(createJTable());
	}
	
	/**
	 * �^�u�̐���
	 * @param tabbedPane
	 */
	@SuppressWarnings("serial")
	public void createTab(JTabbedPane tabbedPane) {
		Object[] cnames = getCnames();
		model = new DefaultTableModel(cnames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JComponent scroll = createCompJTable();
		tabbedPane.add(getTabName(), scroll);
		//
		crateTabNext();
	}

	/**
	 * ��Ԓ�`�̎擾
	 * @return ��Ԓ�`
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * ���f���̎擾
	 * @return ���f��
	 */
	public DefaultTableModel getModel() {
		return model;
	}
	
	/**
	 * �e�[�u���̎擾
	 * @return �e�[�u���R���e�i
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * �^�u���̎擾
	 * @return �^�u��
	 */
	public abstract String getTabName();
	
	/**
	 * �J�������̎擾
	 * @return �J�������
	 */
	public abstract String[] getCnames();
	
	/**
	 * �^�u������̏����B
	 * �ǉ��Ń��X�i���𒣂�Ƃ��Ɏg���܂��B
	 */
	public abstract void crateTabNext();
	
	/**
	 * �l�̍X�V�v���B
	 * �V���Ɍl�̃^�[�����������閈�ɔ������܂��B
	 */
	public abstract void updateValue();

	/**
     * �\�����
     */
	private final State state;
	
	/** ���f�� */
	private DefaultTableModel model;
	
	/** �e�[�u�� */
	private JTable table;
}
