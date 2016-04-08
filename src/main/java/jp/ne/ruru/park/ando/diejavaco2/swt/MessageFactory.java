package jp.ne.ruru.park.ando.diejavaco2.swt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import jp.ne.ruru.park.ando.diejavaco2.Message;

/**
 * ���b�Z�[�W�E�B���h�E��S�����܂��B
 * @author ����
 *
 */
public class MessageFactory {
	
	/** �R���X�g���N�^ */
	public MessageFactory() {
		this.textBuffer = new ArrayList<ColorMessage>();
	}
	
	/**
	 * �t���[���̐���
	 * @return �t���[��
	 */
	public JInternalFrame createJInternalFrame() {
		mainFrame = new JInternalFrame("Event",true, false, true, true);
		mainFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		//
		textPane = new JTextPane();
		JScrollPane scroll = new JScrollPane(textPane);
		//
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(scroll,BorderLayout.CENTER);
		//
		nalAttribute = new SimpleAttributeSet();
		StyleConstants.setFontSize(nalAttribute,20);
		StyleConstants.setForeground(nalAttribute,Color.BLACK);
		nalAttribute.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
		StyleConstants.setFontFamily(nalAttribute,Font.SANS_SERIF);
		//
		enemyAttribute = new SimpleAttributeSet();
		StyleConstants.setFontSize(enemyAttribute,18);
		StyleConstants.setForeground(enemyAttribute, new Color(0, 0, 64+32));
		enemyAttribute.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
		StyleConstants.setFontFamily(enemyAttribute,Font.SANS_SERIF);
		//
		friendAttribute = new SimpleAttributeSet();
		StyleConstants.setFontSize(friendAttribute,18);
		StyleConstants.setForeground(friendAttribute,new Color(64+32, 0, 0));
		friendAttribute.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
		StyleConstants.setFontFamily(friendAttribute,Font.SANS_SERIF);
		//
		return mainFrame;
	}
	
	/**
	 * �t���[���̕\��
	 * @param type ���b�Z�[�W���
	 * @param title �^�C�g��
	 * @param message ���b�Z�[�W
	 */
	public void showFrame(Message.TYPE type,String title,String message) {
		Document doc = textPane.getDocument();
		if (doc == null) {
			return;
		}
		if (0 < doc.getLength()) {
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				// �ȍ~�̏����ɈӖ��Ȃ�
				return;
			}
		}
		SimpleAttributeSet atr;
		if (type == Message.TYPE.NAL) {
			atr = nalAttribute;
		} else if (type == Message.TYPE.ENEMY) {
			atr = enemyAttribute;
		} else {
			atr = friendAttribute;
		}
		this.textBuffer.add(new ColorMessage(atr,message));
		if (50 < this.textBuffer.size()) {
			this.textBuffer.remove(0);
		}
		this.textBuffer.stream().forEachOrdered(buff-> {
			try {
				doc.insertString(doc.getLength(), buff.message, buff.atr);
				doc.insertString(doc.getLength(), "\r\n", buff.atr);
			} catch (BadLocationException e) {
			}
		});
		//�X�N���[���΍�
		//JScrollPane scroll = (JScrollPane) this.textArea.getParent().getParent();
		//JScrollBar vBar = scroll.getVerticalScrollBar();
		//int vBarMax = vBar.getMaximum();
		//vBar.setValue(vBarMax);
		//JScrollBar hBar = scroll.getHorizontalScrollBar();
		//int hBarMax = hBar.getMaximum();
		//hBar.setValue(hBarMax);
	    //
		this.mainFrame.setTitle(title);
		this.mainFrame.setVisible(true);
	}
	
	/** �F�t���N���X��� */
	private class ColorMessage {
		/**
		 * �R���X�g���N�^
		 * @param atr �F���
		 * @param message ���b�Z�[�W
		 */
		public ColorMessage(SimpleAttributeSet atr,String message) {
			this.atr = atr;
			this.message = message;
		}
		/** �F��� */
		public final SimpleAttributeSet atr;

		/** ���b�Z�[�W */
		public final String message;
	}

	/** �摜�H�� */
	private JInternalFrame mainFrame;
	
	/** �e�L�X�g�G���A��� */
	private JTextPane textPane;

	/** ���݂̃��b�Z�[�W */
	private final List<ColorMessage> textBuffer;
	
	/** �i���[�V�����̐F��� */
	private SimpleAttributeSet nalAttribute;
	
	/** �G�̐F��� */
	private SimpleAttributeSet enemyAttribute;
	
	/** �����̐F��� */
	private SimpleAttributeSet friendAttribute;
}
