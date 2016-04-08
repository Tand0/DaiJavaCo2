package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.EventPerson;
import jp.ne.ruru.park.ando.diejavaco2.SelectionData;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * �l����\������t���[���̊Ǘ���S�����܂� 
 * @author ����
 */
public class PersonPanelFactory {
	
	/**
	 * �R���X�g���N�^
	 * @param state ��Ԓ�`
	 * @param imageFactory �摜�H��
	 * @param updateListener �N���b�N���̒ǉ����X�i�B
	 *                       �e�[�u���̓��e���X�V�����܂��B
	 */
	public PersonPanelFactory(State state,ImageFactory imageFactory,ActionListener updateListener) {
		this.state = state;
		this.imageFactory = imageFactory;
		this.updateListener = updateListener;
	}
	
	/**
	 * �t���[���̐���
	 * @return �t���[��
	 */
	public JInternalFrame createJInternalFrame() {
		prsonFrame = new JInternalFrame("�l�����",true, true, true, true);
		prsonFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		prsonFrame.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		prsonFrame.add(topPanel,BorderLayout.NORTH);
		//
		personIconLabel = new JLabel();
		personIconLabel.setBorder(new LineBorder(Color.GRAY));
		personIconLabel.setOpaque(true);
		personIconLabel.setBackground(Color.WHITE);
		prsonFrame.add(personIconLabel,BorderLayout.WEST);
		//
		JPanel topCenterPanel = new JPanel();
		topCenterPanel.setBorder(new LineBorder(Color.GRAY));
		topCenterPanel.setLayout(new GridLayout(7,1));
		//
		title = new JLabel("title");
		title.setOpaque(true);
		title.setOpaque(true);
		title.setBackground(Color.BLACK);
		title.setForeground(Color.RED);
		topCenterPanel.add(title);
		//
		contry = new JLabel("contry");
		contry.setOpaque(true);
		contry.setBackground(Color.WHITE);
		topCenterPanel.add(contry);
		//
		money = new JLabel("money");
		money.setOpaque(true);
		money.setBackground(Color.WHITE);
		topCenterPanel.add(money);
		//
		location = new JLabel("location");
		topCenterPanel.add(location);
		//
		hpmp = new JLabel("HP/MP/STR");
		topCenterPanel.add(hpmp);
		//
		//
		cb = new JComboBox<SelectionData>(SelectionData.DATA);
		topCenterPanel.add(cb);
		//
		cbToLocation = new JComboBox<EventLocation>();
		topCenterPanel.add(cbToLocation);
		//
		prsonFrame.add(topCenterPanel,BorderLayout.CENTER);
		//
		return prsonFrame;
	}
	
	/**
	 * �t���[���̕\��
	 * @param enable �\�����邩�H
	 */
	public void showFrame(boolean enable) {
		// ���X�i�̍폜
		cb.removeActionListener(aListener);
		cbToLocation.removeActionListener(bListener);
		//
		EventPerson myPerson = state.getMyPerson();
		if ((myPerson == null) || (!enable)) {
			prsonFrame.setVisible(false);
			return;
		}
		File iconFile = imageFactory.getPesonImageFile(myPerson);
		personIconLabel.setIcon(imageFactory.getIcon(iconFile.toString()));
		//
		title.setText("���O:" + myPerson.getTitle());
		contry.setText("�����F" + myPerson.getContry().getTitle());
		money.setText("�����F" + myPerson.getContry().getMoney());
		if (myPerson.getLocation() != null) {
			location.setText("�Z���F" + myPerson.getLocation().getTitle() + "(�Z�p:"
					+ myPerson.getLocation().getTecnic() + ")");
		} else {
			location.setText("�Z���s��");
		}
		hpmp.setText("HP/MP/STR:" + myPerson.getHp() + "/" + myPerson.getMp() + "/" + myPerson.getStr());
		//
		if (myPerson.getSelection() == null) {
			cb.setVisible(false);
		} else {
			cb.setSelectedItem(myPerson.getSelection());
			if ((state.getNow() == State.NOW.SELECT_CONT_WAIT)
					&& (state.getMyContry() == myPerson.getContry())) {
				cb.setEnabled(true);
			} else {
				cb.setEnabled(false);
			}
			cb.setVisible(true);
			//
		}
		//
		cbToLocation.removeAllItems();
		if (myPerson.getLocation() != null) {
			// �ړ��s�p���Ɏ����̏ꏊ������Ă���
			cbToLocation.addItem(myPerson.getLocation());
		}
		if ((myPerson.getLocation() != null) && (myPerson.getLocation().getLocationSet() != null)) {
			myPerson.getLocation().getLocationSet().stream().forEachOrdered(loc->cbToLocation.addItem(loc));
		}
		if (myPerson.getToLocation() != null) {
			// �ړ��悪���݂���Ƃ��͂��̏ꏊ��I������
			cbToLocation.setSelectedItem(myPerson.getToLocation());
		} else {
			// �ړ��悪���݂��Ȃ��ꍇ�͎����̏ꏊ��I������
			cbToLocation.setSelectedItem(myPerson.getLocation());
		}
		if ((myPerson.getToLocation() == null)
			|| (!isRequiredDisplaActy(myPerson))) {
			cbToLocation.setVisible(false);
		} else {
			if ((state.getNow() == State.NOW.SELECT_CONT_WAIT)
				&& (state.getMyContry() == myPerson.getContry())) {
				cbToLocation.setEnabled(true);
			} else {
				cbToLocation.setEnabled(false);
			}
			cbToLocation.setVisible(true);
		}
		//
		prsonFrame.setVisible(true);

		//
		// ���X�i�̍ēo�^
		cb.addActionListener(aListener);
		cbToLocation.addActionListener(bListener);
	}
	
	/** �I����ύX���� */
	protected ActionListener aListener = new ActionListener() {
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if ((state == null) || (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return;
			}
			EventPerson person = state.getMyPerson();
			@SuppressWarnings("unchecked")
			Object obj = ((JComboBox<SelectionData>)e.getSource()).getSelectedItem();
			//
			// �V�����I����ݒ�
			person.setSelection((SelectionData)obj);
			//
			// �V�����I��p�Ƀ��P�[�V������ݒ�
			state.getData().invokeMovePersion(person);
			//
			if ((!isRequiredDisplaActy(person))
					|| (state.getMyContry() != person.getContry())) {
				// �\�����Ȃ�
				cbToLocation.setVisible(false);
			} else {
				cbToLocation.setVisible(true);
				//
				if (person.getToLocation() != null) {
					// �ړ��悪���݂���Ƃ��͂��̏ꏊ��I������
					cbToLocation.setSelectedItem(person.getToLocation());
				} else {
					// �ړ��悪���݂��Ȃ��ꍇ�͎����̏ꏊ��I������
					cbToLocation.setSelectedItem(person.getLocation());
				}
			}
			cbToLocation.validate();
			cbToLocation.repaint();
			//
			// �e�[�u�����������ď�Ԃ�ς���
			updateListener.actionPerformed(e);
		}
	};
	
	/**
	 * �ړ����Ă��ǂ��A�N�V�������H
	 * @param person �l�����
	 * @return �ړ����Ă��ǂ��Ȃ�true(false�Ȃ�tolocation��null���K�v)
	 */
	protected boolean isRequiredDisplaActy(EventPerson person) {
		return ((person.getLocation() != null)
				&& (person.getSelection() != null)
				&& (person.getSelection().move != 0)
				&& (person.getSelection().move != 5));
	}
	
	/**
	 * �ړ����ύX����
	 */
	protected ActionListener bListener = new ActionListener() {
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if ((state == null) || (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return;
			}
			EventPerson person = state.getMyPerson();
			//
			@SuppressWarnings("unchecked")
			EventLocation loc = (EventLocation)((JComboBox<EventLocation>)e.getSource()).getSelectedItem();
			if (loc != person.getLocation()) {
				person.setToLocation(loc);
			} else {
				person.setToLocation(null);
			}
			//
			// �e�[�u�����������ď�Ԃ�ς���
			updateListener.actionPerformed(e);
		}
	};

	/** ��� */
	private final State state;
	
	/** �摜�H�� */
	private final ImageFactory imageFactory;
	
	/** �e�[�u���X�V�p���X�i */
	private final ActionListener updateListener;

	/** �t���[�� */
	private JInternalFrame prsonFrame;
	
	/** �l�摜�̈ʒu */
	private JLabel personIconLabel;
	
	/** �^�C�g�� */
	private JLabel title;
	
	/** ������ */
	private JLabel contry;
	
	/** �������̂��� */
	private JLabel money;
	
	/** ���ݒn */
	private JLabel location;
	
	/** �l��� */
	private JLabel hpmp;
	
	/** �I����� */
	private JComboBox<SelectionData> cb;
	
	/** �ړ����� */
	private JComboBox<EventLocation> cbToLocation;
}
