package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import jp.ne.ruru.park.ando.diejavaco2.EventContry;
import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * ���Ə��p�̃^�u���Ǘ����܂��B
 * @author ����
 *
 */
public class TableTabContry extends TableTab {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 */
	public TableTabContry(State state) {
		super(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getTabName()
	 */
	@Override
	public String getTabName() {
		return "���Ə��";
	}
	
	/*
	 * (non-Javadoc)
	 * @see jp.ne.ruru.park.ando.diejavaco2.swt.TableTab#getCnames()
	 */
	@Override
	public String[] getCnames() {
		final String[] cnames = {"Num","����","�x�z�n�搔","�`���Ґ�","���Ɨ\�Z","�F�D�x","�F�D�x","�F�D�x","�F�D�x","�F�D�x"};
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
		//
		List<EventContry> contryList = getState().getData().getContryList();
		contryList.stream().forEach(contry->{
			Object[] obj = new Object[getCnames().length];
			obj[0] = new Integer(contry.getContryNumber());
			obj[1] = contry.getTitle();
			obj[2] = contry.getLocationSet().size();
			obj[3] = contry.getPersonList().stream()
					.filter(n->n.isAlive()).count();
			obj[4] = contry.getMoney();
			for (int i = 5 ; i < obj.length ; i++) {
				if (i - 5 < contry.getLike().length) {
					if (i - 5 == contry.getContryNumber()) {
						obj[i] = "--";
					} else {
						obj[i] = contry.getLike()[i -5];
					}
				}
			}
			getModel().addRow(obj);
		});
	}
	
	/**�e�[�u���N���b�N�ɂ�荑�����n���I�����܂� */
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
			int[] indexArray = getTable().getSelectedRows();
			if (indexArray == null) {
				return;
			}
			List<EventLocation> targetLocationList = new ArrayList<EventLocation>();		
			IntStream.range(0, indexArray.length)
					.map(i->indexArray[i])
					.mapToObj(index->(Integer)getTable().getValueAt(index,0))
					.forEach(number->{
						targetLocationList.addAll(
								getState().getData().getContryList().get(number.intValue()).getLocationSet());
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
