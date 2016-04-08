package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

/**
 * �����
 * @author ����
 *
 */
public class EventContry extends Event {
	/**
	 * �C�x���g�R���X�g���N�^
	 * @param element XML�̃G�������g
	 * @param sameNameChek �������O���`�F�b�N����p�ł�
	 * @param contryNumber ���ԍ�
	 */
	public EventContry(Element element,Set<String> sameNameChek,int contryNumber) {
		super(element,sameNameChek);
		this.contryNumber = contryNumber;

		if (element == null) {
			pcFlag = false;
			this.gobi = "";
		} else {
			String priorityString = element.getAttribute("priority");
			if ((priorityString != null) && priorityString.equals("0")) {
				pcFlag = true;
			} else {
				pcFlag = false;
			}
			String gobi = element.getAttribute("gobi");
			if (gobi != null) {
				this.gobi = gobi;
			} else {
				this.gobi = "";
			}
		}

		//
		selectRate = new int[SelectionData.DATA.length];
		//
		//
		money = 100;
	}
	/** 
	 * ���P�[�V������ڑ����� 
	 * @param target ���P�[�V����
	 */
	public void addLocation(EventLocation target) {
		locationList.add(target);
	}
	/**
	 * ���P�[�V�������폜���� 
	 * @param target ���P�[�V����
	 */
	public void removeLocation(EventLocation target) {
		locationList.remove(target);
	}
	/**
	 * ���P�[�V�������擾���� 
	 * @return ���P�[�V����
	 */
	public Set<EventLocation> getLocationSet() {
		return this.locationList;
	}
	
	/**
	 * �����̎擾
	 * @return ����
	 */
	public int getMoney() {
		return money;
	}
	/**
	 * �����̐ݒ�
	 * @param money ����
	 */
	public void setMoney(int money) {
		this.money = money;
	}
	/**
	 * �F�D�l�̎擾
	 * @return �F�D�l
	 */
	public int[] getLike() {
		return like;
	}
	/** �F�D�l�̐ݒ�
	 * @param like �L���l
	 */
	public void setLike(int[] like) {
		this.like = like;
	}
	
	/**
	 * ���̍��͐����Ă��邩�H
	 * @return �����Ă���Ƃ�true
	 */
	public boolean isAlive() {
		return 0 < getArivePersonList().size();
	}
	
	/**
	 * ����񂩂瑶�����̏����o��
	 * @return �������l���
	 */
	public List<EventPerson> getArivePersonList() {
		return peronList.stream()
				.filter(e->e.isAlive())
				.filter(e->0 < e.getHp())
				.collect(Collectors.toList());
	}
	
	/**
	 * ����񂩂�l�����o�� 
	 * @return �l���
	 */
	public List<EventPerson> getPersonList() {
		return peronList;
	}

	/**
	 * ����̎擾 
	 * @return ���
	 */
	public String getGobi() {
		return gobi;
	}
	
	/**
	 * ���ԍ��̎擾 
	 * @return ���ԍ�
	 */
	public int getContryNumber() {
		return this.contryNumber;
	}
	
	/**
	 * PC�t���O�̎擾
	 * @return PC�t���O
	 */
	public boolean isPc() {
		return pcFlag;
	}
	/**
	 * PC�t���O�B�̐ݒ�
	 * @param pcFlag PC�t���O
	 */
	public void setPc(boolean pcFlag) {
		this.pcFlag = pcFlag;
	}

	/** ���ԍ� */
	private final int contryNumber;
	
	/** �x�z�n�� */ 
	private final List<EventPerson> peronList = new ArrayList<EventPerson>();

	/** �x�z�n�� */
	private final Set<EventLocation> locationList = new TreeSet<EventLocation>();

	/** ���Ɨ\�Z */
	private int money;
	
	/** �L���l */
	private int[] like;
	
	/** ��� */
	private final String gobi;

	/** �I���}��I�Ԋm�� */
	public final int[] selectRate;
	
	/** PC���ǂ����H */
	private boolean pcFlag;
}
