package jp.ne.ruru.park.ando.diejavaco2;

import java.io.File;
import java.util.Random;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * �l�f�[�^
 * @author ����
 *
 */
public class EventPerson extends Event {
	/**
	 * �C�x���g�R���X�g���N�^
	 * @param element XML�̃G�������g
	 * @param sameNameChek �������O���`�F�b�N����p�ł�
	 */
	public EventPerson(Element element,Set<String> sameNameChek) {
		super(element,sameNameChek);
		mp = 100;
		location = null;
		toLocation = null;
		
		if (element == null) {
			hp = -1;
			str = 0;
		} else {
			String priorityString = element.getAttribute("priority");
			if ((priorityString != null) && priorityString.equals("0")) {
				hp = 100;
			} else {
				hp = -1;
			}
			String img = element.getAttribute("img");
			if ((img != null) && (img.length() <= 0)) {
				this.iconName = null;
			} else {
				this.iconName = new File(img);
				if (!this.iconName.exists()) {
					this.iconName = null;
				}
			}
			String strString = element.getAttribute("str");
			if ((strString != null) && (!strString.equals(""))) {
				str = Integer.parseInt(strString);
			} else {
				str = ((new Random()).nextInt(5));
			}
		}
	}

	/**
	 * ���P�[�V�����̎擾
	 * @return ���P�[�V����
	 */
	public EventLocation getLocation() {
		return location;
	}

	/**
	 * ���̐l�͐����Ă��邩�H
	 * @return �����Ă���Ƃ�true
	 */
	public boolean isAlive() {
		return 0 < hp;
	}
	/**
	 * ���P�[�V�����̐ݒ�
	 * @param location ���P�[�V����
	 */
	public void setLocation(EventLocation location) {
		this.location = location;
	}
	
	/**
	 * �ړ���̃��P�[�V�����̎擾
	 * @return �ړ��惍�P�[�V����
	 */
	public EventLocation getToLocation() {
		return toLocation;
	}

	/** �ړ��惍�P�[�V�����̐ݒ�
	 * @param toLocation �ړ��惍�P�[�V����
	 */
	public void setToLocation(EventLocation toLocation) {
		this.toLocation = toLocation;
	}
	
	/**
	 * HP�̎擾
	 * @return HP
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * HP�̐ݒ�
	 * @param hp HP
	 */
	public void setHp(int hp) {
		if (100 < hp) {
			hp = 100;
		}
		this.hp = hp;
	}

	/**
	 * MP�̎擾
	 * @return MP
	 */
	public int getMp() {
		return mp;
	}

	/**
	 * MP�̐ݒ�
	 * @param mp MP
	 */
	public void setMp(int mp) {
		if (100 < mp) {
			mp = 100;
		}
		this.mp = mp;
	}
	
	/**
	 * �������̐ݒ�
	 * @param contry ������
	 */
	public void setContry(EventContry contry) {
		this.contry = contry;
	}
	/**
	 * �������̎擾
	 * @return ������
	 */
	public EventContry getContry() {
		return this.contry;
	}
	
	/**
	 * icon name�̐ݒ�
	 * @param iconName icon name
	 */
	public void setIconName(File iconName) {
		this.iconName = iconName;
	}
	
	/**
	 * icon name�̎擾
	 * @return icon name
	 */
	public File getIconName() {
		return this.iconName;
	}
	
	/**
	 * �s�����e�̎擾
	 * @return �s�����e
	 */
	public SelectionData getSelection() {
		return this.selection;
	}
	
	/**
	 * �s�����e�̐ݒ�
	 * @param selection �s�����e
	 */
	public void setSelection(SelectionData selection) {
		this.selection = selection;
	}
	
	/**
	 * STR�̎擾�B�����قǂ���
	 * @return STR
	 */
	public int getStr() {
		return this.str;
	}
	
	/** ���ݒn*/
	private EventLocation location;
	
	/** ������ */
	private EventContry contry;
	
	/** HP */
	private int hp;
	
	/** MP */
	private int mp;
	
	/** STR */
	private final int str;
	
	/** �A�C�R�� */
	private File iconName;
	
	/** �s�����e */
	private SelectionData selection;
	
	/** �ړ��� */
	private EventLocation toLocation;
}

