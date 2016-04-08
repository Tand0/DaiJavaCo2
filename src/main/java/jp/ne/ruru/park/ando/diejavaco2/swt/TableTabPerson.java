package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ListSelectionModel;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.EventPerson;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * 自分の味方を表示する用のタブを管理します。
 * @author 安藤
 *
 */
public class TableTabPerson extends TableTab {
	
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public TableTabPerson(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getTabName()
	 */
	@Override
	public String getTabName() {
		return "味方";
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getCnames()
	 */
	@Override
	public String[] getCnames() {
		final String[] cnames = {"人物","位置","行動","移動先","HP","MP","STR","識別"};
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
		this.getState().getMyContry().getArivePersonList().stream()
		.forEachOrdered(person-> addTable(person));
	}
	
	/**
	 * 人一人分の情報を追加します
	 * @param person 人
	 */
	protected void addTable(EventPerson person) {
		String location;
		if (person.getLocation() == null) {
			location = "住所不定";
		} else {
			location = person.getLocation().toString();
		}
		String selectString;
		if (person.getSelection() == null) {
			selectString = "未選択";
		} else {
			selectString = person.getSelection().title;
		}
		String toLocation;
		if ((person.getToLocation() == null)
				|| (person.getLocation() == person.getToLocation())) {
			toLocation = "---";
		} else {
			toLocation = person.getToLocation().toString();
		}
		Integer hp = new Integer(person.getHp());
		Integer mp = new Integer(person.getMp());
		Integer str = new Integer(person.getStr());
		String friend = person.getContry().getTitle();
		Object[] target = {person,location,selectString,toLocation,hp,mp,str,friend};
		getModel().addRow(target);
	}
	
	/** クリックした人の地域を選択してマップに反映させます */
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
		public void mouseReleased(MouseEvent me) {
			int index = getTable().getSelectedRow();
			if (index < 0) {
				return;
			}
			String personName = getTable().getValueAt(index,0).toString();
			List<EventPerson> personList =
					getState().getData().getContryList().stream()
					.flatMap(e->e.getArivePersonList().stream())
					.filter(e->e.getTitle().equals(personName))
					.collect(Collectors.toList());
			//
			if (!personList.isEmpty()) {
				EventPerson person = personList.get(0);
				List<EventLocation> targetLocationList = personList.stream()
					.flatMap(e->Arrays.asList(e.getLocation()).stream())
					.collect(Collectors.toList());
				getState().action(targetLocationList,person);
			}
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
