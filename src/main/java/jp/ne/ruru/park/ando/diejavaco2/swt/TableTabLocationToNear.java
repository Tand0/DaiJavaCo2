package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * 隣接地域を表示する用のタブを管理します。
 * @author 安藤
 *
 */
public class TableTabLocationToNear extends TableTabLocation {
	
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public TableTabLocationToNear(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#getTabName()
	 */
	@Override
	public String getTabName() {
		return "周辺地";
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#createCompJTable()
	 */
	@Override
	protected JComponent createCompJTable() {
		JComponent comp = super.createCompJTable();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JButton button = new JButton("選択された地域の周辺地域を表示します");
		button.addActionListener((e)->{
			while(0 < this.getModel().getRowCount()) {
				this.getModel().removeRow(0);
			}
			if (getState().getTargetLocationList() != null) {
				Set<EventLocation> locationSet =
					getState().getTargetLocationList().stream()
					.flatMap(location->location.getLocationSet().stream())
					.collect(Collectors.toSet());
				List<EventLocation> locationList = new ArrayList<EventLocation>(locationSet);
				locationList.removeAll(getState().getTargetLocationList());
				locationList.stream().forEach(location->addLocation(location));
				//
				//マップに反映
				getState().action(locationList,null);
			}
		});
		panel.add(button,BorderLayout.NORTH);
		panel.add(comp,BorderLayout.CENTER);
		return panel;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTabLocation#updateValue()
	 */
	@Override
	public void updateValue() {
		EventLocation[] locations = new EventLocation[this.getModel().getRowCount()];
		for (int i = 0 ; i < locations.length ; i++) {
			locations[i] = (EventLocation)this.getModel().getValueAt(i, 0);
		}
		while(0 < this.getModel().getRowCount()) {
			this.getModel().removeRow(0);
		}
		Arrays.asList(locations).stream().forEach(location->addLocation(location));
	}
}
