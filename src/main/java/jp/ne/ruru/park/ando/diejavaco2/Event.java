package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * �C�x���g�p�f�[�^
 * @author ����
 */
public class Event implements Comparable<Event> {
	/**
	 * �C�x���g�R���X�g���N�^
	 * @param element XML�̃G�������g
	 * @param sameNameChek �������O���`�F�b�N����p�ł�
	 */
	public Event(Element element,Set<String> sameNameChek) {
		if (element == null) {
			title = "none";
		} else {
			title = element.getAttribute("title");
		}
		while (true) {
			if (!sameNameChek.contains(title)) {
				sameNameChek.add(title);
				break;
			}
			System.out.println("Same title found! titile=" + title);
			title = title + "*";
		}
	}
	/**
	 * �^�C�g���̎擾
	 * @return �^�C�g��
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * ���b�Z�[�W�̒ǉ�
	 * @param message �ǉ����郁�b�Z�[�W
	 */
	public void addMessage(String message) {
		messageList.add(message);
	}
	/**
	 * ���b�Z�[�W�̎擾
	 * @return ���b�Z�[�W
	 */
	public List<String> getMessage() {
		return messageList;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Event o) {
		if (o == this) {
			return 0;
		}
		return o.getTitle().compareTo(this.getTitle());
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if ((title == null) && title.equals("")) {
			return super.toString();
		}
		return title;
	}
	/** �^�C�g�� */
	private String title;

	/** ���b�Z�[�W */
	private final List<String> messageList = new ArrayList<String>();

}
