package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JDesktopPane;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �f�X�N�g�b�v�p�l����̉摜������S�����܂�
 * @author ����
 */
@SuppressWarnings("serial")
public class MainDesktopPanel extends JDesktopPane {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 * @param imageFactory �摜�H��
	 */
	public MainDesktopPanel(State state,ImageFactory imageFactory) {	
		this.state = state;
		this.imageFactory = imageFactory;
		this.timer = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
    @Override
    public void paintComponent(Graphics g) {
    	timer++;
    	if (Integer.MAX_VALUE - 10 < timer) {
    		timer = 0;
    	}
    	if (state == null) {
    		return;
    	}
    	Graphics2D g2D = (Graphics2D) g;
    	
        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();
    	switch (state.getNow()) {
    	default:
    		BufferedImage image = imageFactory.getImage(ImageFactory.KEY_TITLE1);
    		double imageWidth = image.getWidth();
    		double imageHeight = image.getHeight();
    		double sx = (panelWidth / imageWidth);
    		double sy = (panelHeight / imageHeight);
    		AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
    		g2D.drawImage(image, af, this);
    		break;
    	case MOVE_PRE:
    	case MOVE_PERSOPN_PRE:
    	case MOVE_PERSON:
    	case MOVE_PERSON_POST:
    	case MOVE_POST:
    		image = imageFactory.getImage(ImageFactory.KEY_TITLE2);
    		imageWidth = image.getWidth();
    		imageHeight = image.getHeight();
    		sx = (panelWidth / imageWidth);
    		sy = (panelHeight / imageHeight);
    		af = AffineTransform.getScaleInstance(sx, sy);
    		g2D.drawImage(image, af, this);
    		break;
    	case MAP_MOVE:
    	    image = imageFactory.getMapImage();
    	    imageWidth = image.getWidth();
    		imageHeight = image.getHeight();
    		sx = (panelWidth / imageWidth);
    		sy = (panelHeight / imageHeight);
    		af = AffineTransform.getScaleInstance(sx, sy);
    		g2D.drawImage(image, af, this);
    		break;
    	}
    	
		g2D.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if ((state.getNow() == State.NOW.START_PRE)
				|| (state.getNow() == State.NOW.START)
				|| (state.getNow() == State.NOW.START_POST)) {
			//EMPTY
		} else if ((bigEventFileName == null) || bigEventFileName.equals("")) {
    		// �Ȃ�݂��\��
    		BufferedImage image = imageFactory.getImage(ImageFactory.KEY_NARUMITA);
    		int plus = (timer%10);
    		plus = plus < 5 ? plus :5 -plus; 
    		g2D.drawImage(image,
    				(int)panelWidth*1/3 , 0, 
    				(int)(panelWidth*2/3) + plus,
    				(int)(panelWidth*2/3) + plus,
    				this);
    	} else {
    		// �C�x���g�摜��\��
    		BufferedImage image = imageFactory.getImage(bigEventFileName);
    		double sx = (panelWidth / image.getWidth());
    		double sy = (panelHeight / image.getWidth()); // �c����͈�v
    		AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
    		g2D.drawImage(image, af, this);		
    	}
    	
    	
    	int speedUpFlag = this.state.getSpeedUpFlag();
    	if (speedUpFlag != 0) {
    		String text;
			if (speedUpFlag <= 1) {
				text = "x���傢����";
			} else if (speedUpFlag <= 2) {
				text = "x�Ă瑬��";
			} else if (speedUpFlag <= 3) {
				text = "x���񂰁[�[�[����";
			} else {
				text = "x���ǂ낫�̑�����";
			}
    		g.setColor(Color.RED);
			Font fontOld = g.getFont();
			Font font = new Font(Font.SANS_SERIF, Font.BOLD,30);
			g.setFont(font);
    		g.drawString(text,5+5+64,5+32);
    		g.setFont(fontOld);
    	}
    	
    	if (state.getNow() == State.NOW.START) {
    		g.setColor(Color.BLACK);
			Font fontOld = g.getFont();
			Font font = new Font(Font.SANS_SERIF, Font.BOLD,30);
			g.setFont(font);
			String[] text = {
					"����� �� �{�^���������Ă�������",
					"����� �� �{�^�����N���b�N���Ă�",
					"Please press \"��\" button.",
					"����� �� �{�^�����N���b�N���邶���",
			};
			Random rand = new Random();
    		g.drawString(text[rand.nextInt(text.length)],5+5+64,this.getHeight()*2/5);
    		g.setFont(fontOld);
    	}
    }
	
    /**
     * �ł����C�x���g�𔭐�������
     * @param fileName �ł����C�x���g�̉摜
     */
	public void showBigEvent(String fileName) {
		this.bigEventFileName = fileName;
		this.revalidate();
		this.repaint();
	}

	/** �^�C�}�[*/
	private int timer;

	/** �摜�H�� */
	private final ImageFactory imageFactory;

	/** �\����� */
	private final State state;
	
	/** �ł����C�x���g */
	private String bigEventFileName;
}
