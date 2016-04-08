package jp.ne.ruru.park.ando.diejavaco2;

/** �I������� */
public class SelectionData {
	/** �V�O�l�`���[ */
	public enum SG {
		/** �����_�� */
		NT("NT"),
		
		/** �h�q */
		HK("HK"),
		
		/** �키 */
		FT("FT"),
		
		/** ������ */
		RA("RA"),
		
		/** ������ */
		AD("AD"),
		
		/** ��� */
		HM("HN"),
		
		/** �Ӎ߂Ɣ�����v������ */
		SB("SB"),
		
		/** �F������ */
		HN("HN"),
		
		/** �������O������ */
		HG("HG"),
		
		/** �x�e���� */
		SL("SL");
		/**
		 * �R���X�g���N�^ 
		 * @param name ���̂𕶎�����������(XML��IF���Ŏg��)
		 */
		private SG(String name) {
			this.name = name;
		}
		/** ������� */
		public final String name;
	}	
	
	/** �I����� */
	public static SelectionData[] DATA = {
			// �Ƃɂ����ړ�����
			new SelectionData(SG.NT,"�ړ�/�����_��",  1, 0,0,0,-20,  0,0,0,-20,   0,-20),

			// ���������ړ�����A�G���������ɂ�����D�悷��ړ�
			new SelectionData(SG.HK,"�ړ�/�ڂ�����",  2, 0,0,5,-10,  0,0,5,-10,  0,-20),

			// �n��̂��������ۂ�����,�����U������
			// �F�D�x�����A�G�̓G�ɂ́�
			new SelectionData(SG.FT,"�ړ�/��������",  3,-1,0,0,-40,  -2,0,0,-40,  0,-20),

			// �����������łȂ��ꍇ�ړ�����
			new SelectionData(SG.RA,"�ړ�/�ɂ���",   4, 0,0,0,-5,    0,0,0,-5,   0,-10),

			// �l�𑝂₷�A�Z�p��������A�G������ƋZ�p�́���(���񂻁[����[)//����/���܂�/�Z�p
			new SelectionData(SG.AD,"����:�ӂ���",   5,-1,0,10,-10,  -1,0,12,-10,  0,-20),

			// �Z�p�ɉ����������𓾂遪���A�Z�p���� // ����(�Ђ����ʂ�)
			new SelectionData(SG.HN,"����:���",    0,1,0,-1,15,  1,0,-1,20,   0,-20),

			//�F�D�x�������Ƃ� �F�D�x�����A���������A�G�̓G�ɂ́�
			//�F�D�x���Ⴂ�Ƃ��F�D�x��,�����[�A�G�̓G�ɂ͖�����(�����̂��Ƃ�)
			new SelectionData(SG.SB,"�O��:�v��",    0, -10, 1,20,30,  -5, 0,-1,-1,  0,-10),
			
			// �F�D�x�����A�G�̓G�ɂ́��A�Z�p�́��A������// ����(�����Ă�)
			new SelectionData(SG.HN,"�O��:�F��",    0,  20,-5,15,10,  15,-1, 5, 5,  0,-10),
			
			// �F�D�x���A�G�̓G�ɂ́�
			new SelectionData(SG.HG,"�O��:������",  0,  -5, 1,10,20,  -10, 1,-1,-1,  0,-10),
			
			// �̗͉�
			new SelectionData(SG.SL,"�x�e",   0, 0,0,0,0,  0,0,0,0,  60,100)
	};
	
	/**
	 * �V�O�l�`����I���N���X�ɕϊ�����
	 * @param signature �V�O�l�`��
	 * @return �I���N���X
	 */
	public static SelectionData getSignatureToSelectionData(SG signature) {
		for (SelectionData sd : DATA) {
			if (sd.signature == signature) {
				return sd;
			}
		}
		return DATA[DATA.length - 1];
	}
	
	/**
	 * �R���X�g���N�^
	 * @param signature �V�O�l�`��
	 * @param title �^�C�g��
	 * @param move �ړ��ݒ�
	 * @param yuukouA �F�D�x�������Ƃ��F�D�l
	 * @param tekiTekiYukouA �F�D�x�������Ƃ��G�̓G�̗F�D�l
	 * @param tecA �F�D�x�������Ƃ��Z�p������
	 * @param moneyA �F�D�x�������Ƃ�����������
	 * @param yuukouB �F�D�x���Ⴂ�Ƃ��F�D�l
	 * @param tekiTekiYukouB �F�D�x���Ⴂ�Ƃ����G�̓G�̗F�D�l
	 * @param tecB �F�D�x���Ⴂ�Ƃ��Z�p������
	 * @param moneyB �F�D�x���Ⴂ�Ƃ�����������
	 * @param hp ����HP
	 * @param mp ����MP
	 */
	protected SelectionData(
			SG signature,
			String title,
			int move,
			//
			int yuukouA,
			int tekiTekiYukouA,
			int tecA,
			int moneyA,
			//
			int yuukouB,
			int tekiTekiYukouB,
			int tecB,
			int moneyB,
			 //
			 int hp,
			 int mp
			) {
		this.signature = signature;
		this.title = title;
		this.move = move;
		//
		this.yuukouA = yuukouA;
		this.tekiTekiYukouA = tekiTekiYukouA;
		this.tecA = tecA;
		this.moneyA = moneyA;
		//
		this.yuukouB = yuukouB;
		this.tekiTekiYukouB = tekiTekiYukouB;
		this.tecB = tecB;
		this.moneyB = moneyB;
		 //
		this.hp = hp;
		this.mp =  mp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.title + "(MP:" + mp + ")";
	}
	
	
	/** �V�O�l�`�� */
	public final SG signature;

	/** �^�C�g�� */
	public final String title;
	
	/** �ړ���� */
	public final int move;

	//
	//
	
	/** �F�D�l�������Ƃ��F�D�l�̑����� */
	public final int yuukouA;
	
	/** �F�D�l�������Ƃ��G�̓G�̗F�D�l�̑����� */
	public final int tekiTekiYukouA;
	
	/** �F�D�l�������Ƃ��Z�p�͂̑����� */
	public final int tecA;
	
	/** �F�D�l�������Ƃ������̑����� */
	public final int moneyA;
	
	//
	//
	
	/** �F�D�l���Ⴂ�Ƃ��F�D�l�̑����� */
	public final int yuukouB;

	/** �F�D�l���Ⴂ�Ƃ��G�̓G�̗F�D�l�̑����� */
	public final int tekiTekiYukouB;

	/** �F�D�l���Ⴂ�Ƃ����Z�p�͂̑����� */
	public final int tecB;

	/** �F�D�l���Ⴂ�Ƃ������̑����� */
	public final int moneyB;
	//
	
	/** HP�̑����� */
	public final int hp;
	
	/** MP�̑����� */
	public final int mp;
	//
}
