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


/** �摜�֘A�̊Ǘ���S�����܂�
 * @author ����
 */
public class ImageFactory {
	
	/** ��ʂ̕� */
	public static final int WINDOW_X = 880;
	
	/** ��ʂ̍��� */
	public static final int WINDOW_Y = 580;
	
	/** �}�b�v�̕� */
	public static final int WINDOW_MAP_X = WINDOW_X/4;
	
	/** �}�b�v�̍��� */
	public static final int WINDOW_MAP_Y = WINDOW_Y/4;
	
	/** �t���[���̍���A�C�R�� */
	public static final String TITLE_ICON = "./src/main/config/map/ee2e.gif";

	/** ����� ���{�^�� */
	public static final String KEY_GT = "./src/main/config/map/gt.gif";
	
	/** ����� �����{�^�� */
	public static final String KEY_GT2 = "./src/main/config/map/gt2.gif";

	/** ����� ���邮��{�^�� */
	public static final String KEY_RANDOM = "./src/main/config/map/random.gif";
	
	/** ����� ��~�{�^�� */
	public static final String KEY_STOP = "./src/main/config/map/stop.gif";
	
	/** �w�i1 */
	public static final String KEY_TITLE1 = "./src/main/config/map/dai2.jpg";
	
	/** �w�i2 */
	public static final String KEY_TITLE2 = "./src/main/config/map/dai3.jpg";

	/** �N���V�b�N�}�b�v�p�̉摜�t�@�C�� */
	public static final String KEY_CLASSIC = "./src/main/config/map/map2.jpg";

	/** ���c���̉摜  */
	public static final String KEY_NARUMITA = "./src/main/config/imgnal/nal.png";

	/** MOB�p�l�A�C�R���̔z�u�t�H���_ */
	public static final String KEY_COMMON_DIR = "./src/main/config/img";

	/** �~�j�C�x���g�^���� */
	public static final String EVENT_DEAD = "./src/main/config/map/dead.png";
	
	/** �~�j�C�x���g�^�U�� */
	public static final String EVENT_ATTACK = "./src/main/config/map/attack.png";

	/** �~�j�C�x���g�^�D�� */
	public static final String EVENT_GET = "./src/main/config/map/get.png";

	/** �~�j�C�x���g�^�I�� */
	public static final String EVENT_VOTE = "./src/main/config/map/vote.png";

	/** �~�j�C�x���g�^���� */
	public static final String EVENT_CREATE = "./src/main/config/map/create.png";

	/** �R���X�g���N�^ */
	public ImageFactory() {	
	}
	
    /**
     * �C���[�W�̎擾
     * @param name ���O
     * @return �C���[�W
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
     * �A�C�R���̎擾
     * @param name ���O
     * @return �C���[�W
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
     *  �l����摜�����W
     * @param person �l
     * @return �摜
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
     * �}�b�v�C���[�W�̎擾 
     * @return �}�b�v�C���[�W
     */
    public BufferedImage getMapImage() {
    	if (map == null) {
    		map = new BufferedImage(WINDOW_MAP_X, WINDOW_MAP_Y, BufferedImage.TYPE_INT_RGB);
    	}
    	return map;
    }
    
    /** �}�b�v�C���[�W�̏C�� */
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
    
	/** �����}�b�v�̐��� */
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
	
	/** �}�b�v��������ۂ��ύX */
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

	/** �J���[���
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

    /** loop�� */
    private int timer = 0;

    /** �}�b�v�C���[�W */
    private BufferedImage map;
    
	/** �摜�o�b�t�@�[ */
	private HashMap<String,BufferedImage> imageMap = new HashMap<String,BufferedImage>();

	/** �摜�A�C�R�� */
	private HashMap<String,ImageIcon> iconMap = new HashMap<String,ImageIcon>();
	
	/** �ėp�摜�t�@�C���z�u */
	private File[] commonIconFiles;

}
