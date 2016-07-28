package net.vsmudp.gui.component;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.*;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.*;

@SuppressWarnings("serial")
public abstract class MsgPanel extends JPanel {
	
	protected Message msg = null;
	protected JLabel lblMsgType;
	protected JTextArea txtMessage;
	
	private Application app;
	
	protected static Border border1, border2, borderComp;
	
	public static final String received, sent, unknown;
	private static final String DEFAULT_MSG, TYPE_STR;
	private static final Dimension DIM_TYPEAREA;
	
	
	static {
		border1 = new EmptyBorder(0, 10, 0, 2);
		border2 = null;
		borderComp = null;
		DIM_TYPEAREA = new Dimension(50, 100);
		
		received = "RE";
		sent = "ME";
		unknown = "UNK";
		DEFAULT_MSG = "Text Message Goes Here";
		TYPE_STR = "Type";
	}
	
	
	public MsgPanel() {
		
		app = Application.getInstance();
		setLayout(new BorderLayout(15, 5));
		setBorder(border1);
		
		Font font_Msg = app.getFont(PropertyName.FONT_MSG_VIEW_EDITOR);
		Font msg_Type = app.getFont(PropertyName.FONT_MSG_TYPE_RENDERER);
		
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setPreferredSize(DIM_TYPEAREA);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBorder( BorderFactory.createEmptyBorder(7, 0, 7, 0));
		
		add(panel, BorderLayout.LINE_START);
		
		lblMsgType = new JLabel(TYPE_STR);
		lblMsgType.setOpaque(true);
		lblMsgType.setHorizontalAlignment(SwingConstants.CENTER);
		lblMsgType.setVerticalAlignment(SwingConstants.CENTER);
		lblMsgType.setForeground(Color.white);
		
		lblMsgType.setFont(msg_Type);
		panel.add(lblMsgType);
		
		JPanel textBoxHolder = new JPanel();
		textBoxHolder.setOpaque(false);
		add(textBoxHolder, BorderLayout.CENTER);
		
		textBoxHolder.setLayout(new GridBagLayout());
		
		txtMessage = new JTextArea();
		txtMessage.setOpaque(false);
		txtMessage.setEditable(false);
		txtMessage.setLineWrap(true);
		txtMessage.setWrapStyleWord(true);
		
		txtMessage.setFont(font_Msg);
		txtMessage.setText(DEFAULT_MSG);
		
		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = 0;
		cons.gridy = 0;
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weightx = 1;
		cons.anchor = GridBagConstraints.CENTER;
		
		textBoxHolder.add(txtMessage, cons);

	}
	public MsgPanel (Message msg) {
		this();
		setMessage(msg);
	}
	
	public final void setMessage(Message msg) {
		this.msg = msg;
		
		String typeString = null;
		
		if (msg.getMessageType() == Message.MESSAGE_SENT_BY_ME) {
			
			typeString = sent;
			lblMsgType.setBackground(app.getApplicationTheme().getLocalMachineColor());
			
		} else if (msg.getMessageType() == Message.MESSAGE_RECEIVED) {
			
			typeString = received;
			lblMsgType.setBackground(app.getApplicationTheme().getRemoteMachineColor());
			
		} else {
			typeString = unknown;		// this line never executes, just for security reason
			lblMsgType.setBackground(Color.CYAN);
		}							// that, program never encounters NullPointerException
		
		lblMsgType.setText(typeString);
		txtMessage.setText(msg.getMessageBody());
	}
	
	public final void setColorAsSelected(Color selColor) {
		//txtMessage.setForeground(Color.white);
		setBackground(selColor);
	}
	public final void setColorAsNotSelected() {
		//txtMessage.setForeground(Color.black);
		setBackground(null);
	}
	
	public final void setMessageFontPLAIN() {
		Font font = txtMessage.getFont();
		if (font.isPlain() == false) {
			font = font.deriveFont(Font.PLAIN);
			applyFontChanged(font);
		}
	}
	public final void setMessageFontBOLD() {
		Font font = txtMessage.getFont();
		if (font.isBold() == false) {
			font = font.deriveFont(Font.BOLD);
			applyFontChanged(font);
		}
	}
	
	public final void setMessageFontSize(float size) {
		Font font = txtMessage.getFont();
		float current = font.getSize2D();
		
		if (current != size) {
			font = font.deriveFont(size);
			applyFontChanged(font);
		}
	}
	
	public final float getMessageFontSize() {
		return txtMessage.getFont().getSize2D();
	}
	
	public final void applyFontChanged(Font f) {
		txtMessage.setFont(f);
		app.setFont(PropertyName.FONT_MSG_VIEW_EDITOR, f);
	}
	
	public final void attachBorder(Color col) {
		if (border2 == null) {
			border2 = new LineBorder(col, 1);
			borderComp = new CompoundBorder(border2, border1);
		}
		setBorder(borderComp);
	}
	public final void removeBorder() {
		setBorder(border1);
	}
	
	public final void decideFontWillBoldOrPlain(boolean what) {
		if (what == true) {
			setMessageFontBOLD();
		} else {
			setMessageFontPLAIN();
		}
	}
	
}