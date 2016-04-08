package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jp.ne.ruru.park.ando.diejavaco2.SelectionData;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �I��p�t���[���̊Ǘ���S�����܂� 
 * @author ����
 */
public class SelectFactory {
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public SelectFactory(State state) {
		this.state = state;
	}
	
	/**
	 * �t���[���̐���
	 * @return �t���[��
	 */
	public JInternalFrame createJInternalFrame() {
		selectFrame = new JInternalFrame("�I�����Ă�������",true, false, true, true);
		selectFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		selectFrame.setLayout(new GridLayout(11,2));
		int i = 0;
		spinner = new JSpinner[SelectionData.DATA.length];
		for (SelectionData data: SelectionData.DATA) {
			JLabel button = new JLabel(data.title);
			button.setToolTipText(data.title);
			selectFrame.add(button);
			SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 100, 1);
			spinner[i] = new JSpinner(model);
			selectFrame.add(spinner[i]);
			i++;
		}
		
		JButton updateButton = new JButton("���f");
		updateButton.addActionListener(e->{
			// �e�[�u���𔽉f������
			int[] selectRate = new int[this.state.getMyContry().selectRate.length];
			for (int j = 0 ; j < this.state.getMyContry().selectRate.length ; j++) {
				selectRate[j] = ((Integer)spinner[j].getValue()).intValue();
			}
			this.state.action(selectRate);
		});
		selectFrame.add(updateButton);
		//
		JButton endActionButton = new JButton("�I��");
		endActionButton.addActionListener(e->{
			this.state.action(State.ACTION_TYPE.TURN_END);
		});
		selectFrame.add(endActionButton);

		return selectFrame;
	}
	/** �e�[�u�����A�b�v�f�[�g���� */
	protected void updateTableValue() {
		for (int i = 0 ; i < this.state.getMyContry().selectRate.length ; i++) {
			this.spinner[i].setValue(this.state.getMyContry().selectRate[i]);
		}
	}
	/**
	 * �t���[���̕\��
	 * @param enable �\�����邩�H
	 */
	public void showFrame(boolean enable) {
		selectFrame.setVisible(enable);
	}
	

	/** ��� */
	private final State state;
	
	/** �I��p�t���[�� */
	private JInternalFrame selectFrame;
	

	/** �X�s�i�[ */
	private JSpinner[] spinner;
	

}
