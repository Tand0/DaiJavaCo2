package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * 左上の”＞”の管理を担当します 
 * @author 安藤
 */
public class GoButtonFactory {
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 * @param imageFactory 画像工場
	 */
	public GoButtonFactory(State state,ImageFactory imageFactory) {
		this.state = state;
		this.imageFactory = imageFactory;
	}
	
	/**
	 * フレームの生成
	 * @return フレーム
	 */
	public JInternalFrame createJInternalFrame() {
	    JInternalFrame iFrame = new JInternalFrame("",true, false, false, false);
	    iFrame.setLayout(new GridLayout(1,3));
	    JButton button = new JButton("");
	    button.setToolTipText("next");
	    button.setIcon(imageFactory.getIcon(ImageFactory.KEY_GT));
	    button.setSize(64,64);
	    iFrame.add(button);
	    button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.action(State.ACTION_TYPE.IGNRE);
			}
	    });
	    button = new JButton("");
	    button.setIcon(imageFactory.getIcon(ImageFactory.KEY_GT2));
	    button.setToolTipText("skip");
	    button.setSize(64,64);
	    iFrame.add(button);
	    button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.action(State.ACTION_TYPE.SPEED_UP);
			}
	    });
		iconFlag = true;
		startStopButton = new JButton();
		startStopButton.setToolTipText("non stop");
		startStopButton.setIcon(imageFactory.getIcon(ImageFactory.KEY_RANDOM));
		startStopButton.setSize(64,64);
	    iFrame.add(startStopButton);
		startStopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (iconFlag) {
					startStopButton.setToolTipText("stop");
					startStopButton.setIcon(imageFactory.getIcon(ImageFactory.KEY_STOP));
					state.action(State.ACTION_TYPE.EXTRNAL_TURNEND);
				} else {
					startStopButton.setToolTipText("auto");
					startStopButton.setIcon(imageFactory.getIcon(ImageFactory.KEY_RANDOM));
					state.action(State.ACTION_TYPE.EXTRNAL_TURNSTART);
				}
				iconFlag = ! iconFlag;
			}
	    });
	    iFrame.setSize(64,64*3);
	    iFrame.setBounds(5, 5, 64, 64);
	    iFrame.setVisible(true);

		return iFrame;
	}

	/** 状態 */
	private final State state;
	
	/** 画像工場 */
	private final ImageFactory imageFactory;

	/** 開始停止用ボタン */
	private JButton startStopButton;
	
	/** iconFlag */
	private boolean iconFlag;

}
