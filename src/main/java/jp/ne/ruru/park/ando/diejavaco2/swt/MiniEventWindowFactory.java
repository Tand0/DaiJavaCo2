package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;


/**
 * イベント用のミニウィンドウの管理を担当します
 * @author 安藤
 *
 */
public class MiniEventWindowFactory {
	
	/**
	 * コンストラクタ
	 * @param imageFactory 画像工場
	 */
	public MiniEventWindowFactory(ImageFactory imageFactory) {
		this.imageFactory = imageFactory;
	}
	
	/**
	 * フレームの生成
	 * @return フレーム
	 */
	public JInternalFrame createJInternalFrame() {
		miniEventFrame = new JInternalFrame("Event",true, false, true, true);
		miniEventFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		//
		miniEventFrame.setLayout(new BorderLayout());
		iconLabel = new JLabel();
		miniEventFrame.add(iconLabel,BorderLayout.CENTER);
		//
		return miniEventFrame;
	}
	
	/**
	 * フレームの表示
	 * @param targetImage ミニイベントのファイル名
	 */
	public void showFrame(String targetImage) {
		if ((targetImage != null) && (!targetImage.equals(""))) {
			// アイコンの表示
			iconLabel.setIcon(imageFactory.getIcon(targetImage));
			miniEventFrame.setVisible(true);
		} else {
			miniEventFrame.setVisible(false);			
		}
	}	
	/** 画像工場 */
	private final ImageFactory imageFactory;
	
	/** 画像用のアイコンラベル位置 */
	private JLabel iconLabel;
	
	/** フレーム */
	private JInternalFrame miniEventFrame;
}
