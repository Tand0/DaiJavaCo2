package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.EventContry;
import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.EventPerson;
import jp.ne.ruru.park.ando.diejavaco2.State;

/** マップ上の画像処理を担当します */
@SuppressWarnings("serial")
public class MapPanel extends JPanel {
	
	/**
	 *  コンストラクタ
	 * @param state 状態遷移
	 * @param imageFactory 画像工場
	 */
	public MapPanel(State state,ImageFactory imageFactory) {
		this.state = state;
		this.imageFactory = imageFactory;
		this.addMouseListener(mListener);
		this.addMouseMotionListener(mmListener);
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
    	super.paintComponent(g);
    	
    	// 背景の表示
    	BufferedImage image;
    	if (classicFlag) {
    		image = imageFactory.getImage(ImageFactory.KEY_CLASSIC);
    	} else {
    		image = imageFactory.getMapImage();
    	}
    	Graphics2D g2D = (Graphics2D) g;
        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double sx = (panelWidth / imageWidth);
        double sy = (panelHeight / imageHeight);
        AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
        g2D.drawImage(image, af, this);
        
    		
        // 線の長さを決める
        BasicStroke bStroke = new BasicStroke(3.0f);
        g2D.setStroke(bStroke);
        
        for (EventLocation location1 : state.getData().getLocationSet()) {
        	for (EventLocation location2 : location1.getLocationSet()) {
        		Color color = getLoctaionColor(location1,location2);
        		g.setColor(color);
        		//線を引く
        		g.drawLine(
        				location1.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X,
        				location1.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y,
        				location2.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X,
        				location2.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y);
        	}
        }
        //
        // 地域を四角で囲む
        for (EventLocation location : state.getData().getLocationSet()) {
        	Color color = getLoctaionColor(location);
        	g.setColor(color);
        	if (color == Color.YELLOW) {
        		g.drawRect(
        				location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X - (timer %3)-2,
        				location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y - (timer %3)-2,
        				(timer %3)*2+4,
        				(timer %3)*2+4);
        	} else if (color == Color.RED) {
        		g.drawRect(
        				location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X - (timer %3)-2,
        				location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y - (timer %3)-2,
        				(timer %3)*2+4,
        				(timer %3)*2+4);
        	} else {
        		g.drawRect(
        				location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X-2,
        				location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y-2,
        				4,
        				4);
    		}
        }
        //
        // 線の太さを変える
        bStroke = new BasicStroke(2.0f);
        g2D.setStroke(bStroke);
        
        // 地域の上に文字を乗せる
        for (EventLocation location : state.getData().getLocationSet()) {
        	StringBuffer buff = new StringBuffer();
        	Color color = Color.YELLOW;       
        	// 味方
        	EventContry contry = state.getMyContry();
        	if (contry != null) {
        		int friend = contry.getArivePersonList().stream()
        				.filter(person->person.getLocation() == location)
        				.collect(Collectors.toList()).size();
        		// 敵
        		int enemy = state.getData().getContryList().stream()
        				.filter(targetContry->targetContry !=contry)
        				.flatMap(targetContry->targetContry.getArivePersonList().stream())
        				.filter(person->person.getLocation() == location)
        				.collect(Collectors.toList()).size();
        		//
        		if (0 < friend + enemy) {
        			if ((0 < enemy) && (0 < friend)) {
        				color = Color.GREEN; // 戦闘
        			} else if (0 < enemy) {
        				color = Color.RED; // 敵
        			} else {
        				color = Color.BLUE; // 味方
        			}
        			g.setColor(color);
        			g.drawLine(
        					location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X - 10,
        					location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y - 10,
        					location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X + 10,
        					location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y + 10);
        			g.drawLine(
        					location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X + 10,
        					location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y - 10,
        					location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X - 10,
        					location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y + 10);

        			if (memberFlag && (state.getMyContry() != null)) {
        				buff.append(":" + friend);
        				buff.append("/" + enemy);
        			}
        		}
        	}
        	if ((state.getTargetLocationList() != null)
        			&& state.getTargetLocationList().contains(location)) {
        		if (contryFlag) {
        			List<EventContry> contryList = this.state.getData().getContryList().stream()
        					.filter(cont->cont.getLocationSet().contains(location)).collect(Collectors.toList());
        			if (!contryList.isEmpty()) {
            			buff.append(":");
            			buff.append(contryList.get(0).getTitle());
        			}
        		}
        		if (this.locationFlag) {
        			buff.append(":");
        			buff.append(location.getTitle());
        		}
        		if (this.tecFlag) {
        			// tecはint型なので入れるとcharではいるよ
        			buff.append(":" + location.getTecnic());
        		}
        		if (buff.toString().length() != 0) {
        			dorawText(g,"" + buff.toString(),
    					location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X -5,
    					location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y-5,
    					color,Color.WHITE);
        		}
        	}
        }
        //
        // 人のアイコンを載せる
    	if (personFlag) {
    		for (EventLocation location : state.getData().getLocationSet()) {
    			EventPerson person = state.getMyPerson();
        		if ((person != null) && (location == person.getLocation())) {
        			File target = imageFactory.getPesonImageFile(person);
        			BufferedImage icon = imageFactory.getImage(target.toString());
        			g2D.drawImage(icon,
        					location.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X-5 ,
        					location.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y+5,
        					this);
        		}
        	}
        }
        //
        if ((dragStartDimension != null) && (dragEndDimension != null)) {
    		g.setColor(Color.RED);
    		//ドラッグ中の線を引く
        	int startX = (int)dragStartDimension.getWidth();
        	int startY = (int)dragStartDimension.getHeight();
        	int endX = (int)dragEndDimension.getWidth();
        	int endY = (int)dragEndDimension.getHeight();
        	if (endX < startX) {
        		int work = endX;
        		endX = startX;
        		startX = work;
        	}
        	if (endY < startY) {
        		int work = endY;
        		endY = startY;
        		startY = work;
        	}
    		g.drawRect(
    				startX,
    				startY,
    				endX - startX,
    				endY - startY);
        }
        //
     }
    
    /**
     * テキストを描画します
     * @param g The <code>Graphics</code> class is the abstract base class for
     *          all graphics contexts that allow an application to draw onto
     *          components that are realized on various devices, as well as
     *          onto off-screen images.
     * @param text テキスト 
     * @param x ｘ
     * @param y y
     * @param fore フォワグラウンド
     * @param back バックグラウンド
     */
    protected void dorawText(Graphics g,String text,int x,int y,Color fore,Color back) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        // get the height of a line of text in this
        // font and render context
        int hgt = metrics.getHeight();
        // get the advance of my text in this font
        // and render context
        int adv = metrics.stringWidth(text);
        g.setColor(back);
        g.fillRoundRect(x-3, y - hgt , adv+6, hgt+3,5,10);
        g.setColor(fore);
        g.drawRoundRect(x-3, y - hgt , adv+6, hgt+3,5,10);
        g.setColor(Color.BLACK);
        g.drawString(text,x,y);
    }
    
    /**
     * 地域のカラーを決めます
     * @param location 地域
     * @return カラー
     */
    protected Color getLoctaionColor(EventLocation location) {
    	if (state == null) {
    		return Color.BLACK;
    	} else if ((state.getTargetLocationList() != null)
    			&& state.getTargetLocationList().contains(location)) {
    		return Color.YELLOW;
    	} else if ((state.getMyContry() != null)
				&& state.getMyContry().getLocationSet().contains(location)) {
    		return Color.RED;
    	} else if (state.getData().getContryList().stream().anyMatch(cont->cont.getLocationSet().contains(location))) {
    		return Color.BLUE;
    	} else {
    		return Color.BLACK;
    	}
    }
    
    /**
     * 地域間のカラーを決めます
     * @param location1 地域１
     * @param location2 地域２
     * @return カラー
     */
    protected Color getLoctaionColor(EventLocation location1,EventLocation location2) {
    	Color color1 = getLoctaionColor(location1);
    	Color color2 = getLoctaionColor(location2);
    	if (naibeorFlag) {
    		if ((color1 == Color.YELLOW) || (color2 == Color.YELLOW)) {
    			return Color.YELLOW;
    		} else if ((color1 == Color.RED) || (color2 == Color.RED)) {
    			return Color.RED;
    		} else if ((color1 == Color.BLUE) || (color2 == Color.BLUE)) {
    			return Color.BLUE;
    		} else {
    			return Color.BLACK;
    		}
    	} else {
    		if (((color1 == Color.YELLOW) && (color2 == Color.YELLOW))
    			|| ((color1 == Color.RED) && (color2 == Color.YELLOW))
    			|| ((color1 == Color.YELLOW) && (color2 == Color.RED))) {
    			return Color.GREEN;
    		} else if ((color1 == Color.RED) && (color2 == Color.RED)) {
    			return Color.RED;
    		} else if ((color1 == Color.BLUE) && (color2 == Color.BLUE)) {
    			return Color.BLUE;
    		} else {
    			return Color.BLACK;
    		}
    	}
    }
    
    /** マウスが押されたときの処理 */
    protected MouseListener mListener = new MouseListener() {

    	/*
    	 * (non-Javadoc)
    	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
    	 */
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if ((state == null)
					|| (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return; // 入力中でなければガード
			}
			if ((state.getTargetLocationList() != null)
					&& (state.getTargetLocationList().size() == 1)) {
	        	EventLocation nowLocation = state.getTargetLocationList().get(0);
	        	int locX = nowLocation.getX()*getWidth()/ImageFactory.WINDOW_MAP_X;
	        	int locY = nowLocation.getY()*getHeight()/ImageFactory.WINDOW_MAP_Y;
	        	int ans = (int)Math.sqrt(
	        				(locX - e.getX()) * (locX - e.getX())
	        				+  (locY - e.getY()) * (locY - e.getY()));
	        	if (20 < ans) {
	        		// 離れすぎ
	        		dragStartDimension = new Dimension(e.getX(),e.getY());
	        	} else {
	        		dragStartDimension = null;
	        	}
			} else {
				dragStartDimension = new Dimension(e.getX(),e.getY());				
			}
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if ((state == null)
					|| (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return; // 入力中でなければガード
			}
	        //
        	List<EventLocation> eLocationList = new ArrayList<EventLocation>();
	        if ((dragStartDimension != null) && (dragEndDimension != null)) {
	        	// ドラッグされている場合
	        	int startX = (int)dragStartDimension.getWidth();
	        	int startY = (int)dragStartDimension.getHeight();
	        	int endX = (int)dragEndDimension.getWidth();
	        	int endY = (int)dragEndDimension.getHeight();
	        	if (endX < startX) {
	        		int work = endX;
	        		endX = startX;
	        		startX = work;
	        	}
	        	if (endY < startY) {
	        		int work = endY;
	        		endY = startY;
	        		startY = work;
	        	}
	        	for (EventLocation nowLocation : state.getData().getLocationSet()) {
	        		int locX = nowLocation.getX()*getWidth()/ImageFactory.WINDOW_MAP_X;
	        		int locY = nowLocation.getY()*getHeight()/ImageFactory.WINDOW_MAP_Y;
	        		if ((startX <= locX)
	        				&& (locX <= endX)
	        				&& (startY <= locY)
	        				&& (locY <= endY)) {
	        			eLocationList.add(nowLocation);
	        		}
	        	}
	        } else {
	        	// ドラッグされていない場合
	        	int ans = Integer.MAX_VALUE;
	        	EventLocation location = null;
	        	for (EventLocation nowLocation : state.getData().getLocationSet()) {
	        		int locX = nowLocation.getX()*getWidth()/ImageFactory.WINDOW_MAP_X;
	        		int locY = nowLocation.getY()*getHeight()/ImageFactory.WINDOW_MAP_Y;
	        		int now = (int)Math.sqrt(
	        				(locX - e.getX()) * (locX - e.getX())
	        				+  (locY - e.getY()) * (locY - e.getY()));
	        		if (now < ans) {
	        			ans = now;
	        			location = nowLocation;
	        		}
	        	}
	        	if (20 < ans) {
	        		location = null; // 離れすぎ
	        	} else {
	        		eLocationList.add(location);
	        	}
	        }
	        //
	        // マップ上の選択ロケーションを変える
        	state.action(eLocationList, null);
			//
			//
			dragStartDimension = null;
			dragEndDimension = null;
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
		}
    };
    
    /** マウスがドラッグされたときの処理 */
    protected MouseMotionListener mmListener = new MouseMotionListener() {

    	/*
    	 * (non-Javadoc)
    	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
    	 */
		@Override
		public void mouseDragged(MouseEvent e) {
			//System.out.println("mouseDragged " + e.getX() + "," + e.getY());
			if ((state == null)
					|| (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return; // 入力中でなければガード
			}
			if ((dragStartDimension == null)
					&& (state.getTargetLocationList() != null)
					&& (state.getTargetLocationList().size() == 1)) {
				dragEndDimension = null;
				EventLocation location = state.getTargetLocationList().get(0);
				location.setX(e.getX()*ImageFactory.WINDOW_MAP_X/getWidth());
				location.setY(e.getY()*ImageFactory.WINDOW_MAP_Y/getHeight());
			} else {
				dragEndDimension = new Dimension(e.getX(),e.getY());
			}
			repaint();
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
		}
    };
    
    /**
     * 人情報がリックされたときの処理。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventPerson(ActionEvent e) {
    	personFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * 国情報がリックされたときの処理。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventContry(ActionEvent e) {
    	contryFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }

    /**
     * 地域情報がリックされたときの処理。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventLocation(ActionEvent e) {
    	locationFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * クラシック情報がリックされたときの処理。
     * クラシックな画像が表示されます。
     * だいじゃばこ１の頃の画像です。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventClassic(ActionEvent e) {
    	classicFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * 技術情報がリックされたときの処理。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventTec(ActionEvent e) {
    	tecFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * 人数情報がリックされたときの処理。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventMember(ActionEvent e) {
    	memberFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * 近接情報がリックされたときの処理。
     * フラグを変更後、repaintすることで以後描写されます。
     * @param e イベント
     */
    public void eventNaibor(ActionEvent e) {
    	naibeorFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /** タイマー */
    private int timer;
    
	/** 画像工場 */
	private final ImageFactory imageFactory;

	/** 表示状態 */
	private final State state;
	
	/** 人物 */
	private boolean personFlag = true;

	/** 人数 */
	private boolean memberFlag = false;

	/** 国名 */
	private boolean contryFlag = false;

	/** 地域 */
	private boolean locationFlag = false;

	/** 技術力 */
	private boolean tecFlag = false;

	/** 近接 */
	private boolean naibeorFlag = false;

	/** クラシック */
	private boolean classicFlag = false;
	
	/** クリック開始 */
	private Dimension dragStartDimension;

	/** ドラッグ中の位置 */
	private Dimension dragEndDimension;
}
