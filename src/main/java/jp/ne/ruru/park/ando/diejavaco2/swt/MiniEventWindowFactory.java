package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;


/**
 * �C�x���g�p�̃~�j�E�B���h�E�̊Ǘ���S�����܂�
 * @author ����
 *
 */
public class MiniEventWindowFactory {
	
	/**
	 * �R���X�g���N�^
	 * @param imageFactory �摜�H��
	 */
	public MiniEventWindowFactory(ImageFactory imageFactory) {
		this.imageFactory = imageFactory;
	}
	
	/**
	 * �t���[���̐���
	 * @return �t���[��
	 */
	public JInternalFrame createJInternalFrame() {
		miniEventFrame = new JInternalFrame("Event",true, false, true, true);
		miniEventFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		//
		miniEventFrame.setLayout(new BorderLayout());
		iconLabel = new JLabel();
		miniEventFrame.add(iconLabel,BorderLayout.CENTER);
		//
		return miniEventFrame;
	}
	
	/**
	 * �t���[���̕\��
	 * @param targetImage �~�j�C�x���g�̃t�@�C����
	 */
	public void showFrame(String targetImage) {
		if ((targetImage != null) && (!targetImage.equals(""))) {
			// �A�C�R���̕\��
			iconLabel.setIcon(imageFactory.getIcon(targetImage));
			miniEventFrame.setVisible(true);
		} else {
			miniEventFrame.setVisible(false);			
		}
	}	
	/** �摜�H�� */
	private final ImageFactory imageFactory;
	
	/** �摜�p�̃A�C�R�����x���ʒu */
	private JLabel iconLabel;
	
	/** �t���[�� */
	private JInternalFrame miniEventFrame;
}
