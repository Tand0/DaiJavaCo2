package jp.ne.ruru.park.ando.diejavaco2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * �C�x���g�p�f�[�^
 * @author ����
 *
 */
public class EventSinario extends Event {
	/**
	 * �C�x���g�R���X�g���N�^
	 * @param element XML�̃G�������g
	 * @param parentEvent �e�C�x���g
	 * @param sameNameChek �������O���`�F�b�N����p�ł�
	 */
	public EventSinario(Element element,Event parentEvent,Set<String> sameNameChek) {
		super(element,sameNameChek);
		this.parentEvent = parentEvent;
		this.img = element.getAttribute("img");
		//
		String priorityString = element.getAttribute("priority");
		if ((priorityString != null) && (!priorityString.equals(""))) {
			priority = Integer.parseInt(priorityString);
		} else {
			priority = Integer.MAX_VALUE;
		}
		//
		final NodeList nodes = element.getChildNodes();
		if (nodes == null) {
			return;
		}
		IntStream
		.range(0,nodes.getLength())
		.mapToObj(nodes::item)
		.filter(n->n instanceof Element)
		.map(n->(Element)n)
		.forEach(e->{
			switch (e.getTagName()) {
			case "if":{
				String name = e.getAttribute("name");
				String value = e.getAttribute("value");
				ifDataList.add(new IFData(name,value));
				break;
			}
			default:
			}
		});
	}
	
	/**
	 * �����f�[�^�̎擾
	 * @return �����f�[�^
	 */
	public List<IFData> getIfDataList() {
		return this.ifDataList;
	}
	
	/**
	 * �e�̃C�x���g�̎擾 
	 * @return �e�C�x���g
	 */
	public Event getParentEvent() {
		return this.parentEvent;
	}
	
	/**
	 * ���s�񐔂̎擾
	 * @return ���s��
	 */
	public int getCount() {
		return count;
	}
	
	/** ���s�񐔂̐ݒ�
	 * @param count ���s��
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * �C�x���g�C���[�W�̎擾
	 * @return �C���[�W
	 */
	public String getImage() {
		return this.img;
	}

	/**
	 * �v���C�I���e�B�̎擾
	 * @return �v���C�I���e�B
	 */
	public int getPriority() {
		return this.priority;
	}
	
	/** �����u���� */
	private final List<IFData> ifDataList = new ArrayList<IFData>();
	
	/** �e�C�x���g */
	private final Event parentEvent;

	/** �I���}��I�Ԋm�� */
	public final int priority;

	/** �V�i���I���s�� */
	private int count = 0;

	/** �C�x���g�摜�u���� */
	private final String img;
	
	/** �I���}��I�Ԋm�� */
	public int[] selectRate;
	
}
