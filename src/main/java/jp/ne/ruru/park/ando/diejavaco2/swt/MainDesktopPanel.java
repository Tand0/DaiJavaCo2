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
 * デスクトップパネル上の画像処理を担当します
 * @author 安藤
 */
@SuppressWarnings("serial")
public class MainDesktopPanel extends JDesktopPane {
	
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 * @param imageFactory 画像工場
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
    		// なるみやを表示
    		BufferedImage image = imageFactory.getImage(ImageFactory.KEY_NARUMITA);
    		int plus = (timer%10);
    		plus = plus < 5 ? plus :5 -plus; 
    		g2D.drawImage(image,
    				(int)panelWidth*1/3 , 0, 
    				(int)(panelWidth*2/3) + plus,
    				(int)(panelWidth*2/3) + plus,
    				this);
    	} else {
    		// イベント画像を表示
    		BufferedImage image = imageFactory.getImage(bigEventFileName);
    		double sx = (panelWidth / image.getWidth());
    		double sy = (panelHeight / image.getWidth()); // 縦横比は一致
    		AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
    		g2D.drawImage(image, af, this);		
    	}
    	
    	
    	int speedUpFlag = this.state.getSpeedUpFlag();
    	if (speedUpFlag != 0) {
    		String text;
			if (speedUpFlag <= 1) {
				text = "xちょい速い";
			} else if (speedUpFlag <= 2) {
				text = "xてら速い";
			} else if (speedUpFlag <= 3) {
				text = "xすんげーーー速い";
			} else {
				text = "xおどろきの速さに";
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
					"左上の ＞ ボタンを押してください",
					"左上の ＞ ボタンをクリックしてね",
					"Please press \"＞\" button.",
					"左上の ＞ ボタンをクリックするじゃば",
			};
			Random rand = new Random();
    		g.drawString(text[rand.nextInt(text.length)],5+5+64,this.getHeight()*2/5);
    		g.setFont(fontOld);
    	}
    }
	
    /**
     * でかいイベントを発生させる
     * @param fileName でかいイベントの画像
     */
	public void showBigEvent(String fileName) {
		this.bigEventFileName = fileName;
		this.revalidate();
		this.repaint();
	}

	/** タイマー*/
	private int timer;

	/** 画像工場 */
	private final ImageFactory imageFactory;

	/** 表示状態 */
	private final State state;
	
	/** でかいイベント */
	private String bigEventFileName;
}
