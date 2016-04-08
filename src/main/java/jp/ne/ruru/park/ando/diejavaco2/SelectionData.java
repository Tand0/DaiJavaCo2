package jp.ne.ruru.park.ando.diejavaco2;

/** 選択肢情報 */
public class SelectionData {
	/** シグネチャー */
	public enum SG {
		/** ランダム */
		NT("NT"),
		
		/** 防衛 */
		HK("HK"),
		
		/** 戦う */
		FT("FT"),
		
		/** 逃げる */
		RA("RA"),
		
		/** 増える */
		AD("AD"),
		
		/** 搾取 */
		HM("HN"),
		
		/** 謝罪と賠償を要求する */
		SB("SB"),
		
		/** 友愛する */
		HN("HN"),
		
		/** 告げ口外交する */
		HG("HG"),
		
		/** 休憩する */
		SL("SL");
		/**
		 * コンストラクタ 
		 * @param name 名称を文字化したもの(XMLのIF文で使う)
		 */
		private SG(String name) {
			this.name = name;
		}
		/** 文字情報 */
		public final String name;
	}	
	
	/** 選択情報 */
	public static SelectionData[] DATA = {
			// とにかく移動する
			new SelectionData(SG.NT,"移動/ランダム",  1, 0,0,0,-20,  0,0,0,-20,   0,-20),

			// 自国内を移動する、敵が自国内にいたら優先する移動
			new SelectionData(SG.HK,"移動/ぼうえい",  2, 0,0,5,-10,  0,0,5,-10,  0,-20),

			// 地域のｘｘを現象させる,国を攻撃する
			// 友好度↓↓、敵の敵には↑
			new SelectionData(SG.FT,"移動/たたかう",  3,-1,0,0,-40,  -2,0,0,-40,  0,-20),

			// そこが自国でない場合移動する
			new SelectionData(SG.RA,"移動/にげる",   4, 0,0,0,-5,    0,0,0,-5,   0,-10),

			// 人を増やす、技術も増える、敵がいると技術は↑↑(せんそーじゃー)//旧名/こます/技術
			new SelectionData(SG.AD,"内政:ふえる",   5,-1,0,10,-10,  -1,0,12,-10,  0,-20),

			// 技術に応じたお金を得る↑↑、技術が↓ // 旧名(ひっこぬく)
			new SelectionData(SG.HN,"内政:搾取",    0,1,0,-1,15,  1,0,-1,20,   0,-20),

			//友好度が高いとき 友好度↓↓、お金↑↑、敵の敵には↑
			//友好度が低いとき友好度↓,お金ー、敵の敵には無反応(いつものことさ)
			new SelectionData(SG.SB,"外交:要求",    0, -10, 1,20,30,  -5, 0,-1,-1,  0,-10),
			
			// 友好度↑↑、敵の敵には↓、技術は↑、お金↑// 旧名(おだてる)
			new SelectionData(SG.HN,"外交:友愛",    0,  20,-5,15,10,  15,-1, 5, 5,  0,-10),
			
			// 友好度↓、敵の敵には↑
			new SelectionData(SG.HG,"外交:告げ口",  0,  -5, 1,10,20,  -10, 1,-1,-1,  0,-10),
			
			// 体力回復
			new SelectionData(SG.SL,"休憩",   0, 0,0,0,0,  0,0,0,0,  60,100)
	};
	
	/**
	 * シグネチャを選択クラスに変換する
	 * @param signature シグネチャ
	 * @return 選択クラス
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
	 * コンストラクタ
	 * @param signature シグネチャ
	 * @param title タイトル
	 * @param move 移動設定
	 * @param yuukouA 友好度が高いとき友好値
	 * @param tekiTekiYukouA 友好度が高いとき敵の敵の友好値
	 * @param tecA 友好度が高いとき技術増加量
	 * @param moneyA 友好度が高いとき資金増加量
	 * @param yuukouB 友好度が低いとき友好値
	 * @param tekiTekiYukouB 友好度が低いときき敵の敵の友好値
	 * @param tecB 友好度が低いとき技術増加量
	 * @param moneyB 友好度が低いとき資金増加量
	 * @param hp 消費HP
	 * @param mp 消費MP
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
	
	
	/** シグネチャ */
	public final SG signature;

	/** タイトル */
	public final String title;
	
	/** 移動種別 */
	public final int move;

	//
	//
	
	/** 友好値が高いとき友好値の増加量 */
	public final int yuukouA;
	
	/** 友好値が高いとき敵の敵の友好値の増加量 */
	public final int tekiTekiYukouA;
	
	/** 友好値が高いとき技術力の増加量 */
	public final int tecA;
	
	/** 友好値が高いとき資金の増加量 */
	public final int moneyA;
	
	//
	//
	
	/** 友好値が低いとき友好値の増加量 */
	public final int yuukouB;

	/** 友好値が低いとき敵の敵の友好値の増加量 */
	public final int tekiTekiYukouB;

	/** 友好値が低いときき技術力の増加量 */
	public final int tecB;

	/** 友好値が低いとき資金の増加量 */
	public final int moneyB;
	//
	
	/** HPの増加量 */
	public final int hp;
	
	/** MPの増加量 */
	public final int mp;
	//
}
