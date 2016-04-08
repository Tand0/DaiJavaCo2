package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �}�b�v�p�t���[���̊Ǘ���S�����܂� 
 * @author ����
 */
public class MapFactory {
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 * @param imageFactory �摜�H��
	 */
	public MapFactory(State state,ImageFactory imageFactory) {
		this.state = state;
		this.imageFactory = imageFactory;
	}
	
	/**
	 * �t���[���̐���
	 * @return �t���[��
	 */
	public JInternalFrame createJInternalFrame() {
		this.mapFrame = new JInternalFrame("�}�b�v���",true, true, true, true);
		this.mapFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		this.mapFrame.setMinimumSize(new Dimension(ImageFactory.WINDOW_MAP_X,ImageFactory.WINDOW_MAP_Y+64));
		this.mapPanel = new MapPanel(this.state,imageFactory);
		JPanel mapMainPanel = new JPanel();
		mapMainPanel.setLayout(new BorderLayout());
		mapMainPanel.add(mapPanel,BorderLayout.CENTER);
		JPanel mapBottomPanel = new JPanel();
		mapMainPanel.add(mapBottomPanel, BorderLayout.SOUTH);
		//
		JCheckBox box = new JCheckBox("�l��");
		box.setSelected(true);
		box.addActionListener(e->mapPanel.eventPerson(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("�l��");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventMember(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("����");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventContry(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("�n��");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventLocation(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("�Z�p��");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventTec(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("�ߐ�");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventNaibor(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("Classic");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventClassic(e));
		//
		mapBottomPanel.add(box);
		this.mapFrame.setLayout(new BorderLayout());
		this.mapFrame.add(mapMainPanel,BorderLayout.CENTER);

		return mapFrame;
	}
	/**
	 * �t���[���̕\��
	 * @param enable �\�����邩�H
	 */
	public void showFrame(boolean enable) {
		if (enable) {
			this.mapPanel.revalidate();
			this.mapPanel.repaint();
		}
		this.mapFrame.setVisible(enable);
	}

	/** ��� */
	private final State state;
	
	/** �摜�H�� */
	private final ImageFactory imageFactory;
	
	/** �C���[�W�t���[�� */
	private JInternalFrame mapFrame;

	/** �C���[�W�p�l�� */
	private MapPanel mapPanel;

}
