package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.ne.ruru.park.ando.diejavaco2.EventContry;
import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �S�n��p�̃^�u���Ǘ����܂��B
 * @author ����
 *
 */
public class TableTabLocation extends TableTab {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableTabLocation(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getTabName()
	 */
	@Override
	public String getTabName() {
		return "�S�n��";
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getCnames()
	 */
	@Override
	public String[] getCnames() {
		final String[] cnames = {"�n�於","�x�z��","�Z�p��","�����l��","�G�l��","x","y"};
		return  cnames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#crateTabNext()
	 */
	@Override
	public void crateTabNext() {
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
		getState().getData().getLocationSet().stream()
		.forEachOrdered(location-> {
			addLocation(location);
		});
	}
	
	/**
	 * �P���̒n�����ǉ����܂�
	 * @param location �����
	 */
	protected void addLocation(EventLocation location) {
		//
		// �x�z������������
		String targetConttry = "�Ȃ�";
		for (EventContry contry : this.getState().getData().getContryList()) {
			if (contry.getLocationSet().stream()
					.anyMatch(loc-> loc== location)) {
				targetConttry = contry.getTitle();
				break;
			}
		}
		int tec = location.getTecnic();
		int frend = 0;
		int enemy = 0;
		if (getState().getMyContry() != null) {
			//
			EventContry contry = getState().getMyContry();
			frend = contry.getArivePersonList().stream()
					.filter(person->person.getLocation() == location)
				.collect(Collectors.toList()).size();
			//
			enemy = getState().getData().getContryList().stream()
					.filter(targetContry->targetContry !=contry)
					.flatMap(targetContry->targetContry.getArivePersonList().stream())
					.filter(person->person.getLocation() == location)
					.collect(Collectors.toList()).size();
		}
		String x = String.format("%04d",location.getX());
		String y = String.format("%04d",location.getY());
		Object[] target = {location,targetConttry,tec,frend,enemy,x,y};
		getModel().addRow(target);

	}
	
	/** �I�������n���I�����}�b�v�ɔ��f���܂� */
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
			int[] indexArray = getTable().getSelectedRows();
			if (indexArray == null) {
				return;
			}
			List<EventLocation> targetLocationList = new ArrayList<EventLocation>();		
			IntStream.range(0, indexArray.length)
					.map(i->indexArray[i])
					.mapToObj(index->(String)getTable().getValueAt(index,0).toString())
					.collect(Collectors.toList())
					.forEach(locationName->{
						getState().getData().getLocationSet().stream()
						.filter(location->location.getTitle().equals(locationName))
						.forEach(location->targetLocationList.add(location));
					});
			getState().action(targetLocationList,null);
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
