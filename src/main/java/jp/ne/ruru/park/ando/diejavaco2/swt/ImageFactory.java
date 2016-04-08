package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import jp.ne.ruru.park.ando.diejavaco2.EventPerson;


/** 画像関連の管理を担当します
 * @author 安藤
 */
public class ImageFactory {
	
	/** 画面の幅 */
	public static final int WINDOW_X = 880;
	
	/** 画面の高さ */
	public static final int WINDOW_Y = 580;
	
	/** マップの幅 */
	public static final int WINDOW_MAP_X = WINDOW_X/4;
	
	/** マップの高さ */
	public static final int WINDOW_MAP_Y = WINDOW_Y/4;
	
	/** フレームの左上アイコン */
	public static final String TITLE_ICON = "./src/main/config/map/ee2e.gif";

	/** 左上の ＞ボタン */
	public static final String KEY_GT = "./src/main/config/map/gt.gif";
	
	/** 左上の ＞＞ボタン */
	public static final String KEY_GT2 = "./src/main/config/map/gt2.gif";

	/** 左上の ぐるぐるボタン */
	public static final String KEY_RANDOM = "./src/main/config/map/random.gif";
	
	/** 左上の 停止ボタン */
	public static final String KEY_STOP = "./src/main/config/map/stop.gif";
	
	/** 背景1 */
	public static final String KEY_TITLE1 = "./src/main/config/map/dai2.jpg";
	
	/** 背景2 */
	public static final String KEY_TITLE2 = "./src/main/config/map/dai3.jpg";

	/** クラシックマップ用の画像ファイル */
	public static final String KEY_CLASSIC = "./src/main/config/map/map2.jpg";

	/** 副団長の画像  */
	public static final String KEY_NARUMITA = "./src/main/config/imgnal/nal.png";

	/** MOB用個人アイコンの配置フォルダ */
	public static final String KEY_COMMON_DIR = "./src/main/config/img";

	/** ミニイベント／死んだ */
	public static final String EVENT_DEAD = "./src/main/config/map/dead.png";
	
	/** ミニイベント／攻撃 */
	public static final String EVENT_ATTACK = "./src/main/config/map/attack.png";

	/** ミニイベント／奪取 */
	public static final String EVENT_GET = "./src/main/config/map/get.png";

	/** ミニイベント／選挙 */
	public static final String EVENT_VOTE = "./src/main/config/map/vote.png";

	/** ミニイベント／復活 */
	public static final String EVENT_CREATE = "./src/main/config/map/create.png";

	/** コンストラクタ */
	public ImageFactory() {	
	}
	
    /**
     * イメージの取得
     * @param name 名前
     * @return イメージ
     */
    public BufferedImage getImage(String name) {
    	BufferedImage image = imageMap.get(name);
    	if (image == null) {
    		try {
				image = ImageIO.read(new File(name));
			} catch (IOException e) {
				e.printStackTrace();
			}
    		imageMap.put(name, image);
    	}
    	return image;
    }
    /**
     * アイコンの取得
     * @param name 名前
     * @return イメージ
     */
    public ImageIcon getIcon(String name) {
    	ImageIcon icon = iconMap.get(name);
    	if (icon == null) {
    		icon =  new ImageIcon(this.getImage(name));
    		iconMap.put(name, icon);
    	}
    	return icon;
    }
    /**
     *  人から画像を収集
     * @param person 人
     * @return 画像
     */
    public File getPesonImageFile(EventPerson person) {
		File target = person.getIconName();
		if (target == null) {
			Random rand = new Random();
			if (commonIconFiles == null) {
				commonIconFiles = (new File(KEY_COMMON_DIR)).listFiles();
			}

			target = commonIconFiles[rand.nextInt(commonIconFiles.length)];
			person.setIconName(target);
		}
		return target;

    }
    
    /**
     * マップイメージの取得 
     * @return マップイメージ
     */
    public BufferedImage getMapImage() {
    	if (map == null) {
    		map = new BufferedImage(WINDOW_MAP_X, WINDOW_MAP_Y, BufferedImage.TYPE_INT_RGB);
    	}
    	return map;
    }
    
    /** マップイメージの修正 */
    public void changeMap() {
    	if (map == null) {
    		getMapImage();
    	}
    	if (timer == 0) {
    		createMap();
    	} else {
    		createMoveMap();
    	}
    	timer++;
    }
    
	/** 初期マップの生成 */
	protected void createMap() {
		Random rand = new Random();
		for (int i = 0 ; i < map.getWidth() ; i++) {
			for (int j = 0 ; j < map.getHeight(); j++) {
				for (int m = 0 ; m < valueColor.length ; m++) {
					int ans = rand.nextInt(100);
					Color color = valueColor[m];
					map.setRGB(i, j, color.getRGB());
					if (ans < 40) {
						break;
					}
				}
			}
		}
		//
	}
	
	/** マップをそれっぽく変更 */
	protected void createMoveMap() {
		Random rand = new Random();
		for (int k = 0 ; k < 3 ; k ++) {
			for (int i = 0 ; i < map.getWidth() ; i++) {
				int oldColor = valueColor[0].getRGB();
				for (int j = 0 ; j < map.getHeight() ; j++) {
					int[] next = new int[4];
					if (i == 0) {
						next[1] = valueColor[0].getRGB();
					} else {
						next[1] = map.getRGB(i-1,j);
					}
					next[0] = oldColor;
					if (i == map.getWidth() - 1) {
						next[2] = valueColor[0].getRGB();
					} else {
						next[2] = map.getRGB(i+1,j);
					}
					if (j == map.getHeight() - 1) {
						next[3] = valueColor[0].getRGB();
					} else {
						next[3] = map.getRGB(i,j+1);
					}
					oldColor = map.getRGB(i,j);
					if ((next[0] == next[1])
							&& (next[1] == next[2])
							&& (next[2] == next[3])) {
						map.setRGB(i,j,next[0]);
					} else {
						map.setRGB(i,j,next[rand.nextInt(next.length)]);
					}
				}
			}
		}
	}

	/** カラー情報
	 * @see "http://crocro.com/html5/world_map/"
	 * @see "http://www.voidelement.com/randomap/"
	 */
	private static final Color[] valueColor = {
			new Color( 40,  97, 174),
			new Color(199, 219, 122),
			new Color( 99, 162, 216),
			new Color(168, 160, 152),
			new Color(123, 192, 102),
			new Color(123, 153,  69),
			new Color(208, 208, 208),
			new Color(255, 255, 255),
			new Color(102,  52,   1),
	};

    /** loop数 */
    private int timer = 0;

    /** マップイメージ */
    private BufferedImage map;
    
	/** 画像バッファー */
	private HashMap<String,BufferedImage> imageMap = new HashMap<String,BufferedImage>();

	/** 画像アイコン */
	private HashMap<String,ImageIcon> iconMap = new HashMap<String,ImageIcon>();
	
	/** 汎用画像ファイル配置 */
	private File[] commonIconFiles;

}
