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
 * 選択用フレームの管理を担当します 
 * @author 安藤
 */
public class SelectFactory {
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public SelectFactory(State state) {
		this.state = state;
	}
	
	/**
	 * フレームの生成
	 * @return フレーム
	 */
	public JInternalFrame createJInternalFrame() {
		selectFrame = new JInternalFrame("選択してください",true, false, true, true);
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
		
		JButton updateButton = new JButton("反映");
		updateButton.addActionListener(e->{
			// テーブルを反映させる
			int[] selectRate = new int[this.state.getMyContry().selectRate.length];
			for (int j = 0 ; j < this.state.getMyContry().selectRate.length ; j++) {
				selectRate[j] = ((Integer)spinner[j].getValue()).intValue();
			}
			this.state.action(selectRate);
		});
		selectFrame.add(updateButton);
		//
		JButton endActionButton = new JButton("終了");
		endActionButton.addActionListener(e->{
			this.state.action(State.ACTION_TYPE.TURN_END);
		});
		selectFrame.add(endActionButton);

		return selectFrame;
	}
	/** テーブルをアップデートする */
	protected void updateTableValue() {
		for (int i = 0 ; i < this.state.getMyContry().selectRate.length ; i++) {
			this.spinner[i].setValue(this.state.getMyContry().selectRate[i]);
		}
	}
	/**
	 * フレームの表示
	 * @param enable 表示するか？
	 */
	public void showFrame(boolean enable) {
		selectFrame.setVisible(enable);
	}
	

	/** 状態 */
	private final State state;
	
	/** 選択用フレーム */
	private JInternalFrame selectFrame;
	

	/** スピナー */
	private JSpinner[] spinner;
	

}
