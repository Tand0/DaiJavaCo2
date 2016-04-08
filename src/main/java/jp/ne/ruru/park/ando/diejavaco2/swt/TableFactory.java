package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;

import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * テーブル用フレームの管理を担当します 
 * @author 安藤
 */
public class TableFactory {
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public TableFactory(State state) {
		this.state = state;
	}
	
	/**
	 * フレームの生成
	 * @return フレーム
	 */
	public JInternalFrame createJInternalFrame() {
		this.tableFrame = new JInternalFrame("データテーブル",true, true, true, true);
		this.tableFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		this.tableFrame.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();
		//
		this.tabList = new ArrayList<TableTab>();
		//
		this.tabList.add(new TableTabContry(this.state));
		this.tabList.add(new TableTabPerson(this.state));
		this.tabList.add(new TableTabMyLocation(this.state));
		this.tabList.add(new TableTabLocationToLocation(this.state));
		this.tabList.add(new TableTabLocationToNear(this.state));
		this.tabList.add(new TableTabLocationToPerson(this.state));
		this.tabList.add(new TableTabLocation(this.state));
		this.tabList.add(new TableTabSystemContry(this.state));
		//
		this.tabList.stream().forEachOrdered(tab->tab.createTab(tabbedPane));
	    //
		this.tableFrame.add(tabbedPane,BorderLayout.CENTER);
		
		return tableFrame;
	}
	/** テーブルをアップデートする */
	protected void updateTableValue() {
		tabList.stream().forEachOrdered(e->{
			e.updateValue();
		});
	}
	/**
	 * フレームの表示
	 * @param enable 表示するか？
	 */
	public void showFrame(boolean enable) {
		tabList.stream().forEachOrdered(e->e.updateValue());
		this.tableFrame.setVisible(enable);
	}
	

	/** 状態 */
	private final State state;
	
	/** テーブルフレーム */
	private JInternalFrame tableFrame;
	
	/** テーブルタブのリスト */
	private List<TableTab> tabList;

}
