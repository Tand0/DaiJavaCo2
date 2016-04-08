package jp.ne.ruru.park.ando.diejavaco2;


/**
 * ���b�Z�[�W�p�f�[�^
 * @author ����
 */
public class Message {
	
	/** ���b�Z�[�W��� */
	public enum TYPE {

		/** �I�y���[�^�̃��b�Z�[�W */
		NAL,
		
		/** �����̃��b�Z�[�W */
		FRIEND,
		
		/** �G�̃��b�Z�[�W */
		ENEMY
	}
	
	/**
	 * �R���X�g���N�^
	 * @param type ���b�Z�[�W���
	 * @param message ���b�Z�[�W�̓��e
	 * @param miniEventFileName �~�j���b�Z�[�W�C�x���g�p
	 * @param bigEventFileName �ł����C�x���g�\���C�x���g�p
	 */
	public Message(TYPE type,String message,String miniEventFileName,String bigEventFileName) {
		this.type = type;
		this.message = message;
		this.miniEventFileName = miniEventFileName;
		this.bigEventFileName = bigEventFileName;
	}
	
	/**
	 * ���b�Z�[�W��ʂ̎擾 
	 * @return ���
	 */
	public TYPE getType() {
		return type;
	}
	
	/**
	 * ���b�Z�[�W�̎擾
	 * @return ���b�Z�[�W
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * �~�j�C�x���g�摜�̎擾
	 * @return �~�j�C�x���g�摜
	 */
	public String getMiniEventFileName() {
		return miniEventFileName;
	}
	
	/**
	 * �ł����C�x���g�摜�̎擾
	 * @return �ł����C�x���g�摜
	 */
	public String getBigEventFileName() {
		return bigEventFileName;
	}
	
	/** ���b�Z�[�W��� */
	private final TYPE type;
	
	/** ���b�Z�[�W */
	private final String message;
	
	/** �~�j���b�Z�[�W�摜 */
	private final String miniEventFileName;
	
	/** �ł����C�x���g�摜 */
	private final String bigEventFileName;
}
