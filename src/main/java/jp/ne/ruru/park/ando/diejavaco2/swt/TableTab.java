package jp.ne.ruru.park.ando.diejavaco2.swt;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jp.ne.ruru.park.ando.diejavaco2.State;


/**
 * テーブル表示用タブの基底クラス
 * @author 安藤
 */
public abstract class TableTab {
	
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public TableTab(State state) {	
		this.state = state;
	}
	
	/**
	 * テーブル情報の取得 
	 * @return テーブルコンテナ
	 */
	protected JTable createJTable() {
		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		return table;
	}
	
	/**
	 * スクロールコンテナの取得→をサブクラスでスチールする用
	 * @return swingコンテナ
	 */
	protected JComponent createCompJTable() {
		return new JScrollPane(createJTable());
	}
	
	/**
	 * タブの生成
	 * @param tabbedPane
	 */
	@SuppressWarnings("serial")
	public void createTab(JTabbedPane tabbedPane) {
		Object[] cnames = getCnames();
		model = new DefaultTableModel(cnames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JComponent scroll = createCompJTable();
		tabbedPane.add(getTabName(), scroll);
		//
		crateTabNext();
	}

	/**
	 * 状態定義の取得
	 * @return 状態定義
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * モデルの取得
	 * @return モデル
	 */
	public DefaultTableModel getModel() {
		return model;
	}
	
	/**
	 * テーブルの取得
	 * @return テーブルコンテナ
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * タブ名の取得
	 * @return タブ名
	 */
	public abstract String getTabName();
	
	/**
	 * カラム情報の取得
	 * @return カラム情報
	 */
	public abstract String[] getCnames();
	
	/**
	 * タブ生成後の処理。
	 * 追加でリスナ等を張るときに使います。
	 */
	public abstract void crateTabNext();
	
	/**
	 * 値の更新要求。
	 * 新たに個人のターンが発生する毎に発生します。
	 */
	public abstract void updateValue();

	/**
     * 表示状態
     */
	private final State state;
	
	/** モデル */
	private DefaultTableModel model;
	
	/** テーブル */
	private JTable table;
}
