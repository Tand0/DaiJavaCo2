package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * マップ用フレームの管理を担当します 
 * @author 安藤
 */
public class MapFactory {
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 * @param imageFactory 画像工場
	 */
	public MapFactory(State state,ImageFactory imageFactory) {
		this.state = state;
		this.imageFactory = imageFactory;
	}
	
	/**
	 * フレームの生成
	 * @return フレーム
	 */
	public JInternalFrame createJInternalFrame() {
		this.mapFrame = new JInternalFrame("マップ画面",true, true, true, true);
		this.mapFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		this.mapFrame.setMinimumSize(new Dimension(ImageFactory.WINDOW_MAP_X,ImageFactory.WINDOW_MAP_Y+64));
		this.mapPanel = new MapPanel(this.state,imageFactory);
		JPanel mapMainPanel = new JPanel();
		mapMainPanel.setLayout(new BorderLayout());
		mapMainPanel.add(mapPanel,BorderLayout.CENTER);
		JPanel mapBottomPanel = new JPanel();
		mapMainPanel.add(mapBottomPanel, BorderLayout.SOUTH);
		//
		JCheckBox box = new JCheckBox("人物");
		box.setSelected(true);
		box.addActionListener(e->mapPanel.eventPerson(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("人数");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventMember(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("国名");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventContry(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("地域");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventLocation(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("技術力");
		box.setSelected(false);
		box.addActionListener(e->mapPanel.eventTec(e));
		mapBottomPanel.add(box);
		//
		box = new JCheckBox("近接");
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
	 * フレームの表示
	 * @param enable 表示するか？
	 */
	public void showFrame(boolean enable) {
		if (enable) {
			this.mapPanel.revalidate();
			this.mapPanel.repaint();
		}
		this.mapFrame.setVisible(enable);
	}

	/** 状態 */
	private final State state;
	
	/** 画像工場 */
	private final ImageFactory imageFactory;
	
	/** イメージフレーム */
	private JInternalFrame mapFrame;

	/** イメージパネル */
	private MapPanel mapPanel;

}
