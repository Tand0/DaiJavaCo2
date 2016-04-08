package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ListSelectionModel;

import jp.ne.ruru.park.ando.diejavaco2.EventContry;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * システム情報を表示する用のタブを管理します。
 * この画面でPC/NPCを選択できます。
 * @author 安藤
 *
 */
public class TableTabSystemContry extends TableTab {

	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public TableTabSystemContry(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getTabName()
	 */
	@Override
	public String getTabName() {
		return "System";
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getCnames()
	 */
	@Override
	public String[] getCnames() {
		final String[] cnames = {"国名","任意"};
		return  cnames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#crateTabNext()
	 */
	@Override
	public void crateTabNext() {
		getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTable().addMouseListener(mListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#updateValue()
	 */
	@Override
	public void updateValue() {
		while(0 < getModel().getRowCount()) {
			getModel().removeRow(0);
		}
		List<EventContry> contryList = getState().getData().getContryList();
		contryList.stream().forEach(contry->{
			Object[] obj = new Object[getCnames().length];
			obj[0] = contry.getTitle();
			obj[1] = contry.isPc() ? "PC" : "NPC";
			getModel().addRow(obj);
		});
	}
	
	/**
	 * クリックした後に国情報の選択を変更します
	 */
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
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			int index = getTable().getSelectedRow();
			if (index < 0) {
				return;
			}
			if (index == 0) {
				return; // １カ国は必ず選択できるようにする
			}
			EventContry contry = getState().getData().getContryList().get(index);
			contry.setPc(!contry.isPc());
			getModel().setValueAt(contry.isPc() ? "PC" : "NPC", index, 1);
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
}
