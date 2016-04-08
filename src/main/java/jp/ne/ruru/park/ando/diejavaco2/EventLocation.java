package jp.ne.ruru.park.ando.diejavaco2;

import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Element;
/**
 * �n����
 * @author ����
 *
 */
public class EventLocation extends Event {
	/**
	 * �C�x���g�R���X�g���N�^
	 * @param element XML�̃G�������g
	 * @param sameNameChek �������O���`�F�b�N����p�ł�
	 */
	public EventLocation(Element element,Set<String> sameNameChek) {
		super(element,sameNameChek);
	}
	
	/**
	 * getter x
	 * @return x
	 */
	public int getX() {
		return x;
	}
	/**
	 * setter x
	 * @param x x
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * getter y
	 * @return y
	 */
	public int getY() {
		return y;
	}
	/**
	 * setter y
	 * @param y y
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * �n����̃��X�g
	 * @return �n����
	 */
	public Set<EventLocation> getLocationSet() {
		return locationList;
	}
	/**
	 * ���P�[�V�����̋�����Ԃ� 
	 * @param target ���胍�P�[�V����
	 * @return ����
	 */
	public int getRange(EventLocation target) {
		return (int)Math.sqrt(
				   (this.getX() - target.getX()) * (this.getX() - target.getX())
				+  (this.getY() - target.getY()) * (this.getY() - target.getY()));
	}
	/**
	 * ���P�[�V������ڑ����� 
	 * @param target �ڑ��惍�P�[�V����
	 */
	public void addLocation(EventLocation target) {
		if (target == this) {
			return;
		}
		locationList.add(target);
	}

	/**
	 * �Z�p�͂̏���
	 * @return �Z�p��
	 */
	public int getTecnic() {
		return tecnic;
	}

	/**
	 * �Z�p�͂̐ݒ�
	 * @param tecnic �Z�p��
	 */
	public void setTecnic(int tecnic) {
		this.tecnic = tecnic;
		if (this.tecnic < 0) {
			this.tecnic = 0;
		} else if (100 < this.tecnic) {
			this.tecnic = 100;
		}
	}
	
	/** x */
	protected int x;
	
	/** y */
	protected int y;
	
	/** ���P�[�V������� */
	protected final Set<EventLocation> locationList = new TreeSet<EventLocation>();

	/** �Z�p�� */
	protected int tecnic;
}

