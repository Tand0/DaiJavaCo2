package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import jp.ne.ruru.park.ando.diejavaco2.EventLocation;
import jp.ne.ruru.park.ando.diejavaco2.EventPerson;
import jp.ne.ruru.park.ando.diejavaco2.SelectionData;
import jp.ne.ruru.park.ando.diejavaco2.State;

/**
 * 人情報を表示するフレームの管理を担当します 
 * @author 安藤
 */
public class PersonPanelFactory {
	
	/**
	 * コンストラクタ
	 * @param state 状態定義
	 * @param imageFactory 画像工場
	 * @param updateListener クリック時の追加リスナ。
	 *                       テーブルの内容を更新させます。
	 */
	public PersonPanelFactory(State state,ImageFactory imageFactory,ActionListener updateListener) {
		this.state = state;
		this.imageFactory = imageFactory;
		this.updateListener = updateListener;
	}
	
	/**
	 * フレームの生成
	 * @return フレーム
	 */
	public JInternalFrame createJInternalFrame() {
		prsonFrame = new JInternalFrame("人物画面",true, true, true, true);
		prsonFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		prsonFrame.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		prsonFrame.add(topPanel,BorderLayout.NORTH);
		//
		personIconLabel = new JLabel();
		personIconLabel.setBorder(new LineBorder(Color.GRAY));
		personIconLabel.setOpaque(true);
		personIconLabel.setBackground(Color.WHITE);
		prsonFrame.add(personIconLabel,BorderLayout.WEST);
		//
		JPanel topCenterPanel = new JPanel();
		topCenterPanel.setBorder(new LineBorder(Color.GRAY));
		topCenterPanel.setLayout(new GridLayout(7,1));
		//
		title = new JLabel("title");
		title.setOpaque(true);
		title.setOpaque(true);
		title.setBackground(Color.BLACK);
		title.setForeground(Color.RED);
		topCenterPanel.add(title);
		//
		contry = new JLabel("contry");
		contry.setOpaque(true);
		contry.setBackground(Color.WHITE);
		topCenterPanel.add(contry);
		//
		money = new JLabel("money");
		money.setOpaque(true);
		money.setBackground(Color.WHITE);
		topCenterPanel.add(money);
		//
		location = new JLabel("location");
		topCenterPanel.add(location);
		//
		hpmp = new JLabel("HP/MP/STR");
		topCenterPanel.add(hpmp);
		//
		//
		cb = new JComboBox<SelectionData>(SelectionData.DATA);
		topCenterPanel.add(cb);
		//
		cbToLocation = new JComboBox<EventLocation>();
		topCenterPanel.add(cbToLocation);
		//
		prsonFrame.add(topCenterPanel,BorderLayout.CENTER);
		//
		return prsonFrame;
	}
	
	/**
	 * フレームの表示
	 * @param enable 表示するか？
	 */
	public void showFrame(boolean enable) {
		// リスナの削除
		cb.removeActionListener(aListener);
		cbToLocation.removeActionListener(bListener);
		//
		EventPerson myPerson = state.getMyPerson();
		if ((myPerson == null) || (!enable)) {
			prsonFrame.setVisible(false);
			return;
		}
		File iconFile = imageFactory.getPesonImageFile(myPerson);
		personIconLabel.setIcon(imageFactory.getIcon(iconFile.toString()));
		//
		title.setText("名前:" + myPerson.getTitle());
		contry.setText("所属：" + myPerson.getContry().getTitle());
		money.setText("お金：" + myPerson.getContry().getMoney());
		if (myPerson.getLocation() != null) {
			location.setText("住所：" + myPerson.getLocation().getTitle() + "(技術:"
					+ myPerson.getLocation().getTecnic() + ")");
		} else {
			location.setText("住所不定");
		}
		hpmp.setText("HP/MP/STR:" + myPerson.getHp() + "/" + myPerson.getMp() + "/" + myPerson.getStr());
		//
		if (myPerson.getSelection() == null) {
			cb.setVisible(false);
		} else {
			cb.setSelectedItem(myPerson.getSelection());
			if ((state.getNow() == State.NOW.SELECT_CONT_WAIT)
					&& (state.getMyContry() == myPerson.getContry())) {
				cb.setEnabled(true);
			} else {
				cb.setEnabled(false);
			}
			cb.setVisible(true);
			//
		}
		//
		cbToLocation.removeAllItems();
		if (myPerson.getLocation() != null) {
			// 移動不可用時に自分の場所も入れておく
			cbToLocation.addItem(myPerson.getLocation());
		}
		if ((myPerson.getLocation() != null) && (myPerson.getLocation().getLocationSet() != null)) {
			myPerson.getLocation().getLocationSet().stream().forEachOrdered(loc->cbToLocation.addItem(loc));
		}
		if (myPerson.getToLocation() != null) {
			// 移動先が存在するときはその場所を選択する
			cbToLocation.setSelectedItem(myPerson.getToLocation());
		} else {
			// 移動先が存在しない場合は自分の場所を選択する
			cbToLocation.setSelectedItem(myPerson.getLocation());
		}
		if ((myPerson.getToLocation() == null)
			|| (!isRequiredDisplaActy(myPerson))) {
			cbToLocation.setVisible(false);
		} else {
			if ((state.getNow() == State.NOW.SELECT_CONT_WAIT)
				&& (state.getMyContry() == myPerson.getContry())) {
				cbToLocation.setEnabled(true);
			} else {
				cbToLocation.setEnabled(false);
			}
			cbToLocation.setVisible(true);
		}
		//
		prsonFrame.setVisible(true);

		//
		// リスナの再登録
		cb.addActionListener(aListener);
		cbToLocation.addActionListener(bListener);
	}
	
	/** 選択を変更した */
	protected ActionListener aListener = new ActionListener() {
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if ((state == null) || (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return;
			}
			EventPerson person = state.getMyPerson();
			@SuppressWarnings("unchecked")
			Object obj = ((JComboBox<SelectionData>)e.getSource()).getSelectedItem();
			//
			// 新しい選択を設定
			person.setSelection((SelectionData)obj);
			//
			// 新しい選択用にロケーションを設定
			state.getData().invokeMovePersion(person);
			//
			if ((!isRequiredDisplaActy(person))
					|| (state.getMyContry() != person.getContry())) {
				// 表示しない
				cbToLocation.setVisible(false);
			} else {
				cbToLocation.setVisible(true);
				//
				if (person.getToLocation() != null) {
					// 移動先が存在するときはその場所を選択する
					cbToLocation.setSelectedItem(person.getToLocation());
				} else {
					// 移動先が存在しない場合は自分の場所を選択する
					cbToLocation.setSelectedItem(person.getLocation());
				}
			}
			cbToLocation.validate();
			cbToLocation.repaint();
			//
			// テーブルを検索して状態を変える
			updateListener.actionPerformed(e);
		}
	};
	
	/**
	 * 移動しても良いアクションか？
	 * @param person 人物情報
	 * @return 移動しても良いならtrue(falseならtolocationをnull化必要)
	 */
	protected boolean isRequiredDisplaActy(EventPerson person) {
		return ((person.getLocation() != null)
				&& (person.getSelection() != null)
				&& (person.getSelection().move != 0)
				&& (person.getSelection().move != 5));
	}
	
	/**
	 * 移動先を変更した
	 */
	protected ActionListener bListener = new ActionListener() {
		
		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if ((state == null) || (state.getNow() != State.NOW.SELECT_CONT_WAIT)) {
				return;
			}
			EventPerson person = state.getMyPerson();
			//
			@SuppressWarnings("unchecked")
			EventLocation loc = (EventLocation)((JComboBox<EventLocation>)e.getSource()).getSelectedItem();
			if (loc != person.getLocation()) {
				person.setToLocation(loc);
			} else {
				person.setToLocation(null);
			}
			//
			// テーブルを検索して状態を変える
			updateListener.actionPerformed(e);
		}
	};

	/** 状態 */
	private final State state;
	
	/** 画像工場 */
	private final ImageFactory imageFactory;
	
	/** テーブル更新用リスナ */
	private final ActionListener updateListener;

	/** フレーム */
	private JInternalFrame prsonFrame;
	
	/** 人画像の位置 */
	private JLabel personIconLabel;
	
	/** タイトル */
	private JLabel title;
	
	/** 所属国 */
	private JLabel contry;
	
	/** 所属国のお金 */
	private JLabel money;
	
	/** 現在地 */
	private JLabel location;
	
	/** 個人情報 */
	private JLabel hpmp;
	
	/** 選択情報 */
	private JComboBox<SelectionData> cb;
	
	/** 移動先情報 */
	private JComboBox<EventLocation> cbToLocation;
}
