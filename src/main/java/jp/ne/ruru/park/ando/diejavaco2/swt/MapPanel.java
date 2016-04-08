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

/** �}�b�v��̉摜������S�����܂� */
@SuppressWarnings("serial")
public class MapPanel extends JPanel {
	
	/**
	 *  �R���X�g���N�^
	 * @param state ��ԑJ��
	 * @param imageFactory �摜�H��
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
    	
    	// �w�i�̕\��
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
        
    		
        // ���̒��������߂�
        BasicStroke bStroke = new BasicStroke(3.0f);
        g2D.setStroke(bStroke);
        
        for (EventLocation location1 : state.getData().getLocationSet()) {
        	for (EventLocation location2 : location1.getLocationSet()) {
        		Color color = getLoctaionColor(location1,location2);
        		g.setColor(color);
        		//��������
        		g.drawLine(
        				location1.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X,
        				location1.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y,
        				location2.getX()*this.getWidth()/ImageFactory.WINDOW_MAP_X,
        				location2.getY()*this.getHeight()/ImageFactory.WINDOW_MAP_Y);
        	}
        }
        //
        // �n����l�p�ň͂�
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
        // ���̑�����ς���
        bStroke = new BasicStroke(2.0f);
        g2D.setStroke(bStroke);
        
        // �n��̏�ɕ������悹��
        for (EventLocation location : state.getData().getLocationSet()) {
        	StringBuffer buff = new StringBuffer();
        	Color color = Color.YELLOW;       
        	// ����
        	EventContry contry = state.getMyContry();
        	if (contry != null) {
        		int friend = contry.getArivePersonList().stream()
        				.filter(person->person.getLocation() == location)
        				.collect(Collectors.toList()).size();
        		// �G
        		int enemy = state.getData().getContryList().stream()
        				.filter(targetContry->targetContry !=contry)
        				.flatMap(targetContry->targetContry.getArivePersonList().stream())
        				.filter(person->person.getLocation() == location)
        				.collect(Collectors.toList()).size();
        		//
        		if (0 < friend + enemy) {
        			if ((0 < enemy) && (0 < friend)) {
        				color = Color.GREEN; // �퓬
        			} else if (0 < enemy) {
        				color = Color.RED; // �G
        			} else {
        				color = Color.BLUE; // ����
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
        			// tec��int�^�Ȃ̂œ�����char�ł͂����
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
        // �l�̃A�C�R�����ڂ���
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
    		//�h���b�O���̐�������
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
     * �e�L�X�g��`�悵�܂�
     * @param g The <code>Graphics</code> class is the abstract base class for
     *          all graphics contexts that allow an application to draw onto
     *          components that are realized on various devices, as well as
     *          onto off-screen images.
     * @param text �e�L�X�g 
     * @param x ��
     * @param y y
     * @param fore �t�H���O���E���h
     * @param back �o�b�N�O���E���h
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
     * �n��̃J���[�����߂܂�
     * @param location �n��
     * @return �J���[
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
     * �n��Ԃ̃J���[�����߂܂�
     * @param location1 �n��P
     * @param location2 �n��Q
     * @return �J���[
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
    
    /** �}�E�X�������ꂽ�Ƃ��̏��� */
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
				return; // ���͒��łȂ���΃K�[�h
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
	        		// ���ꂷ��
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
				return; // ���͒��łȂ���΃K�[�h
			}
	        //
        	List<EventLocation> eLocationList = new ArrayList<EventLocation>();
	        if ((dragStartDimension != null) && (dragEndDimension != null)) {
	        	// �h���b�O����Ă���ꍇ
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
	        	// �h���b�O����Ă��Ȃ��ꍇ
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
	        		location = null; // ���ꂷ��
	        	} else {
	        		eLocationList.add(location);
	        	}
	        }
	        //
	        // �}�b�v��̑I�����P�[�V������ς���
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
    
    /** �}�E�X���h���b�O���ꂽ�Ƃ��̏��� */
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
				return; // ���͒��łȂ���΃K�[�h
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
     * �l��񂪃��b�N���ꂽ�Ƃ��̏����B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventPerson(ActionEvent e) {
    	personFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * ����񂪃��b�N���ꂽ�Ƃ��̏����B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventContry(ActionEvent e) {
    	contryFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }

    /**
     * �n���񂪃��b�N���ꂽ�Ƃ��̏����B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventLocation(ActionEvent e) {
    	locationFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * �N���V�b�N��񂪃��b�N���ꂽ�Ƃ��̏����B
     * �N���V�b�N�ȉ摜���\������܂��B
     * ��������΂��P�̍��̉摜�ł��B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventClassic(ActionEvent e) {
    	classicFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * �Z�p��񂪃��b�N���ꂽ�Ƃ��̏����B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventTec(ActionEvent e) {
    	tecFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * �l����񂪃��b�N���ꂽ�Ƃ��̏����B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventMember(ActionEvent e) {
    	memberFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /**
     * �ߐڏ�񂪃��b�N���ꂽ�Ƃ��̏����B
     * �t���O��ύX��Arepaint���邱�ƂňȌ�`�ʂ���܂��B
     * @param e �C�x���g
     */
    public void eventNaibor(ActionEvent e) {
    	naibeorFlag = ((JCheckBox)e.getSource()).isSelected();
    	validate();
    	repaint();
    }
    
    /** �^�C�}�[ */
    private int timer;
    
	/** �摜�H�� */
	private final ImageFactory imageFactory;

	/** �\����� */
	private final State state;
	
	/** �l�� */
	private boolean personFlag = true;

	/** �l�� */
	private boolean memberFlag = false;

	/** ���� */
	private boolean contryFlag = false;

	/** �n�� */
	private boolean locationFlag = false;

	/** �Z�p�� */
	private boolean tecFlag = false;

	/** �ߐ� */
	private boolean naibeorFlag = false;

	/** �N���V�b�N */
	private boolean classicFlag = false;
	
	/** �N���b�N�J�n */
	private Dimension dragStartDimension;

	/** �h���b�O���̈ʒu */
	private Dimension dragEndDimension;
}
