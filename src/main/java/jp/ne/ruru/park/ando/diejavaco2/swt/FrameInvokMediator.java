package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import jp.ne.ruru.park.ando.diejavaco2.Message;
import jp.ne.ruru.park.ando.diejavaco2.State;
import jp.ne.ruru.park.ando.diejavaco2.Version;

/**
 * テーブルデータの置き場所。
 * State ThreadとSwingとの間で処理の仲介を行います。
 * Swing側のメイン処理を担当します。
 * @author 安藤
 *
 */
public class FrameInvokMediator {
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 */
	public FrameInvokMediator(State state) {
		this.state = state;
		this.imageFactory = new ImageFactory();
		this.mainDesktopPanel = new MainDesktopPanel(state,imageFactory);
	}
	/**
	 * フレームの生成 。
     *  このメソッドはswingとの通信用です。
	 * @return 生成されたフレーム
	 */
	public JInternalFrame createFrameToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				JFrame frame = new JFrame();
				frame.setBounds(10 , 10 , ImageFactory.WINDOW_X , ImageFactory.WINDOW_Y);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("だいじゃばこ2(ver" + Version.VERSION + ")");
			    ImageIcon icon = imageFactory.getIcon(ImageFactory.TITLE_ICON);
			    frame.setIconImage(icon.getImage());
			    frame.setVisible(true);
			    //
			    frame.getContentPane().setLayout(new BorderLayout());
			    frame.getContentPane().add(this.mainDesktopPanel, BorderLayout.CENTER);
			    //
			    this.goButtonFactory = new GoButtonFactory(state,imageFactory);
			    JInternalFrame iFrame = goButtonFactory.createJInternalFrame();
			    this.mainDesktopPanel.add(iFrame);
			    //
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return null;
	}
    /**
     *  メッセージの表示。
     *  このメソッドはswingとの通信用です。
     * @param type メッセージ種別
     * @param title タイトル
     * @param message メッセージ
     * @param miniEvent ミニイベントのファイル名
     * @param bigEvent でかいイベントのファイル名
     */
    public void addMessageToSwing(Message.TYPE type,String title,String message,String miniEvent,String bigEvent) {
		try {
			SwingUtilities.invokeAndWait(()->{
				boolean bigEventFlag = (bigEvent != null) && (!bigEvent.equals(""));
				if (this.mapFactory != null) {
					this.mapFactory.showFrame(!bigEventFlag);
				}
				//
				if (messageFactory == null) {
					this.messageFactory = new MessageFactory();
					JInternalFrame frame = this.messageFactory.createJInternalFrame();
					Dimension d = mainDesktopPanel.getSize();
					frame.setBounds(5, (int)d.getHeight()*2/3,(int)d.getWidth()-10, (int)d.getHeight()/3-5);
					this.mainDesktopPanel.add(frame);
				}
				this.messageFactory.showFrame(type, title, message);
				//
				if (miniEventFactory == null) {
					this.miniEventFactory = new MiniEventWindowFactory(this.imageFactory);
					JInternalFrame frame = this.miniEventFactory.createJInternalFrame();
					frame.setBounds(
							128,
							64,
							440,
							290);
					this.mainDesktopPanel.add(frame);
				}
				if (bigEventFlag) {
					// でかいイベントが来たらパーソンイベントは消す
					this.showPersonIvent(false);
				}
				//
				this.miniEventFactory.showFrame(miniEvent);
				//
				this.mainDesktopPanel.showBigEvent(bigEvent);
				//
			});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    }
    /**
     * 強制再表示。
     * このメソッドはswingとの通信用です。
     * @param bigEvent でかいイベントが発生しているか？
     * @param showTable テーブル情報を表示するか？
     * @param showSelect 選択情報を表示するか？
     */
    public void showBaseToSwing(boolean bigEvent,boolean showTable,boolean showSelect) {
		try {
			SwingUtilities.invokeAndWait(()->{
				this.mainDesktopPanel.revalidate();
				this.mainDesktopPanel.repaint();
				if (!bigEvent) {
					if ((this.state.getMyContry() != null)
							&& (this.state.getMyContry().isAlive())) {
						this.showTableFrame(showTable);
						this.showSelectFrame(showSelect);
					} else {
						this.showTableFrame(false);
						this.showSelectFrame(false);
					}
					//
					// 個人イベントの表示
					this.showPersonIvent(true);
				} else {
					// でかいイベントが行われている(画面は消せ)
					this.showSelectFrame(false);
					this.showTableFrame(false);
					this.showSelectFrame(false);
					this.showPersonIvent(false);
				}
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
	}
	/** テーブルを更新する */
	public void updateTableValueToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				updateTableValue();
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
	}
	/** 地殻変動を起こす */
	public void changeMapToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				imageFactory.changeMap();
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
	}
	
	/**
	 * マップ表示
     *  このメソッドはswingとの通信用です。
	 */
    public void showMapToSwing() {
		try {
			SwingUtilities.invokeAndWait(()->{
				if (this.mapFactory == null) {
					this.mapFactory = new MapFactory(state,imageFactory);
					JInternalFrame frame = this.mapFactory.createJInternalFrame();
					frame.setBounds(64,64,ImageFactory.WINDOW_MAP_X*2,ImageFactory.WINDOW_MAP_Y*2);
					this.mainDesktopPanel.add(frame);
				}
				this.mapFactory.showFrame(true);
			});
			//
		} catch (InvocationTargetException|InterruptedException e) {
			//EMPTY
		}
    }
    /**
     * テーブル表示
     * @param enable 表示するか？
     */
    protected void showTableFrame(boolean enable) {
		// 選択の表示
		if (tableFactory == null) {
			tableFactory = new TableFactory(state);
			JInternalFrame frame = tableFactory.createJInternalFrame();
			frame.setBounds(
					this.mainDesktopPanel.getWidth()/3,
					5,
					this.mainDesktopPanel.getWidth()*2/3-64,
					this.mainDesktopPanel.getHeight()/3);
			this.mainDesktopPanel.add(frame);

		}
		tableFactory.showFrame(enable);
	}
	
    /** 選択表示
     * @param enable 表示するか？
     */
	protected void showSelectFrame(boolean enable) {
		// 選択の表示
		if (selectFactory == null) {
			selectFactory = new SelectFactory(state);
			JInternalFrame frame = selectFactory.createJInternalFrame();
			frame.setBounds(5, 64+20, 128+32, this.mainDesktopPanel.getHeight()*4/9);
			this.mainDesktopPanel.add(frame);
		}
		selectFactory.showFrame(enable);
	}
	/**
	 * 個人イベント表示
	 * @param enable 表示するか？
	 */
	protected void showPersonIvent(boolean enable) {
		if (pFactory == null) {
			pFactory = new PersonPanelFactory(this.state,this.imageFactory,(e)->updateTableValue());
			JInternalFrame frame = pFactory.createJInternalFrame();
			Dimension d = this.mainDesktopPanel.getSize();
			frame.setBounds(
					(int)d.getWidth()*2/3+5,
					(int)d.getHeight()/3,
					(int)d.getWidth()/3-10, 
					(int)d.getHeight()/3);
			this.mainDesktopPanel.add(frame);
		}
		pFactory.showFrame(enable);
	}
	/** テーブルをアップデートする */
	protected void updateTableValue() {
		tableFactory.updateTableValue();
		selectFactory.updateTableValue();
	}
	
	/** 状態 */
	private final State state;	

	/** イメージパネル */
	private final MainDesktopPanel mainDesktopPanel;
	
	/** 画像工場 */
	private final ImageFactory imageFactory;
	
	/** 左上の”＞”工場 */
	private GoButtonFactory goButtonFactory;
	
	/** 表工場 */
	private TableFactory tableFactory;

	/** 選択工場 */
	private SelectFactory selectFactory;

	/** マップ工場 */
	private MapFactory mapFactory;

	/** 個人用フレーム用 */
	private PersonPanelFactory pFactory;

	/** ミニイベント用 */
	private MiniEventWindowFactory miniEventFactory;

	/** メッセージ用 */
	private MessageFactory messageFactory;

}
