package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import jp.ne.ruru.park.ando.diejavaco2.Message;
import jp.ne.ruru.park.ando.diejavaco2.State;
import jp.ne.ruru.park.ando.diejavaco2.Version;

/**
 * �e�[�u���f�[�^�̒u���ꏊ�B
 * State Thread��Swing�Ƃ̊Ԃŏ����̒�����s���܂��B
 * Swing���̃��C��������S�����܂��B
 * @author ����
 *
 */
public class FrameInvokMediator {
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public FrameInvokMediator(State state) {
		this.state = state;
		this.imageFactory = new ImageFactory();
		this.mainDesktopPanel = new MainDesktopPanel(state,imageFactory);
	}
	/**
	 * �t���[���̐��� �B
     *  ���̃��\�b�h��swing�Ƃ̒ʐM�p�ł��B
	 * @return �������ꂽ�t���[��
	 */
	public JInternalFrame createFrameToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				JFrame frame = new JFrame();
				frame.setBounds(10 , 10 , ImageFactory.WINDOW_X , ImageFactory.WINDOW_Y);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("��������΂�2(ver" + Version.VERSION + ")");
			    ImageIcon icon = imageFactory.getIcon(ImageFactory.TITLE_ICON);
			    frame.setIconImage(icon.getImage());
			    frame.setVisible(true);
			    //
			    frame.getContentPane().setLayout(new BorderLayout());
			    frame.getContentPane().add(this.mainDesktopPanel, BorderLayout.CENTER);
			    //
			    this.goButtonFactory = new GoButtonFactory(state,imageFactory);
			    JInternalFrame iFrame = goButtonFactory.createJInternalFrame();
			    this.mainDesktopPanel.add(iFrame);
			    //
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return null;
	}
    /**
     *  ���b�Z�[�W�̕\���B
     *  ���̃��\�b�h��swing�Ƃ̒ʐM�p�ł��B
     * @param type ���b�Z�[�W���
     * @param title �^�C�g��
     * @param message ���b�Z�[�W
     * @param miniEvent �~�j�C�x���g�̃t�@�C����
     * @param bigEvent �ł����C�x���g�̃t�@�C����
     */
    public void addMessageToSwing(Message.TYPE type,String title,String message,String miniEvent,String bigEvent) {
		try {
			SwingUtilities.invokeAndWait(()->{
				boolean bigEventFlag = (bigEvent != null) && (!bigEvent.equals(""));
				if (this.mapFactory != null) {
					this.mapFactory.showFrame(!bigEventFlag);
				}
				//
				if (messageFactory == null) {
					this.messageFactory = new MessageFactory();
					JInternalFrame frame = this.messageFactory.createJInternalFrame();
					Dimension d = mainDesktopPanel.getSize();
					frame.setBounds(5, (int)d.getHeight()*2/3,(int)d.getWidth()-10, (int)d.getHeight()/3-5);
					this.mainDesktopPanel.add(frame);
				}
				this.messageFactory.showFrame(type, title, message);
				//
				if (miniEventFactory == null) {
					this.miniEventFactory = new MiniEventWindowFactory(this.imageFactory);
					JInternalFrame frame = this.miniEventFactory.createJInternalFrame();
					frame.setBounds(
							128,
							64,
							440,
							290);
					this.mainDesktopPanel.add(frame);
				}
				if (bigEventFlag) {
					// �ł����C�x���g��������p�[�\���C�x���g�͏���
					this.showPersonIvent(false);
				}
				//
				this.miniEventFactory.showFrame(miniEvent);
				//
				this.mainDesktopPanel.showBigEvent(bigEvent);
				//
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    }
    /**
     * �����ĕ\���B
     * ���̃��\�b�h��swing�Ƃ̒ʐM�p�ł��B
     * @param bigEvent �ł����C�x���g���������Ă��邩�H
     * @param showTable �e�[�u������\�����邩�H
     * @param showSelect �I������\�����邩�H
     */
    public void showBaseToSwing(boolean bigEvent,boolean showTable,boolean showSelect) {
		try {
			SwingUtilities.invokeAndWait(()->{
				this.mainDesktopPanel.revalidate();
				this.mainDesktopPanel.repaint();
				if (!bigEvent) {
					if ((this.state.getMyContry() != null)
							&& (this.state.getMyContry().isAlive())) {
						this.showTableFrame(showTable);
						this.showSelectFrame(showSelect);
					} else {
						this.showTableFrame(false);
						this.showSelectFrame(false);
					}
					//
					// �l�C�x���g�̕\��
					this.showPersonIvent(true);
				} else {
					// �ł����C�x���g���s���Ă���(��ʂ͏���)
					this.showSelectFrame(false);
					this.showTableFrame(false);
					this.showSelectFrame(false);
					this.showPersonIvent(false);
				}
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
	}
	/** �e�[�u�����X�V���� */
	public void updateTableValueToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				updateTableValue();
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
	}
	/** �n�k�ϓ����N���� */
	public void changeMapToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				imageFactory.changeMap();
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
	}
	
	/**
	 * �}�b�v�\��
     *  ���̃��\�b�h��swing�Ƃ̒ʐM�p�ł��B
	 */
    public void showMapToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				if (this.mapFactory == null) {
					this.mapFactory = new MapFactory(state,imageFactory);
					JInternalFrame frame = this.mapFactory.createJInternalFrame();
					frame.setBounds(64,64,ImageFactory.WINDOW_MAP_X*2,ImageFactory.WINDOW_MAP_Y*2);
					this.mainDesktopPanel.add(frame);
				}
				this.mapFactory.showFrame(true);
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
    }
    /**
     * �e�[�u���\��
     * @param enable �\�����邩�H
     */
    protected void showTableFrame(boolean enable) {
		// �I���̕\��
		if (tableFactory == null) {
			tableFactory = new TableFactory(state);
			JInternalFrame frame = tableFactory.createJInternalFrame();
			frame.setBounds(
					this.mainDesktopPanel.getWidth()/3,
					5,
					this.mainDesktopPanel.getWidth()*2/3-64,
					this.mainDesktopPanel.getHeight()/3);
			this.mainDesktopPanel.add(frame);

		}
		tableFactory.showFrame(enable);
	}
	
    /** �I��\��
     * @param enable �\�����邩�H
     */
	protected void showSelectFrame(boolean enable) {
		// �I���̕\��
		if (selectFactory == null) {
			selectFactory = new SelectFactory(state);
			JInternalFrame frame = selectFactory.createJInternalFrame();
			frame.setBounds(5, 64+20, 128+32, this.mainDesktopPanel.getHeight()*4/9);
			this.mainDesktopPanel.add(frame);
		}
		selectFactory.showFrame(enable);
	}
	/**
	 * �l�C�x���g�\��
	 * @param enable �\�����邩�H
	 */
	protected void showPersonIvent(boolean enable) {
		if (pFactory == null) {
			pFactory = new PersonPanelFactory(this.state,this.imageFactory,(e)->updateTableValue());
			JInternalFrame frame = pFactory.createJInternalFrame();
			Dimension d = this.mainDesktopPanel.getSize();
			frame.setBounds(
					(int)d.getWidth()*2/3+5,
					(int)d.getHeight()/3,
					(int)d.getWidth()/3-10, 
					(int)d.getHeight()/3);
			this.mainDesktopPanel.add(frame);
		}
		pFactory.showFrame(enable);
	}
	/** �e�[�u�����A�b�v�f�[�g���� */
	protected void updateTableValue() {
		tableFactory.updateTableValue();
		selectFactory.updateTableValue();
	}
	
	/** ��� */
	private final State state;	

	/** �C���[�W�p�l�� */
	private final MainDesktopPanel mainDesktopPanel;
	
	/** �摜�H�� */
	private final ImageFactory imageFactory;
	
	/** ����́h���h�H�� */
	private GoButtonFactory goButtonFactory;
	
	/** �\�H�� */
	private TableFactory tableFactory;

	/** �I���H�� */
	private SelectFactory selectFactory;

	/** �}�b�v�H�� */
	private MapFactory mapFactory;

	/** �l�p�t���[���p */
	private PersonPanelFactory pFactory;

	/** �~�j�C�x���g�p */
	private MiniEventWindowFactory miniEventFactory;

	/** ���b�Z�[�W�p */
	private MessageFactory messageFactory;

}
