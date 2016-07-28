package net.vsmudp.gui.msgdialog;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;

@SuppressWarnings("serial")
public class MessagePanel extends JPanel {
	
	protected JLabel[] lblMessage;
	protected JTextArea txtDetails;
	
	protected static Insets margin =null;
	
	public MessagePanel(String msg, String details) {
		
		setLayout(new BorderLayout(5,5));
		
		JPanel pan = createParsedMessagePanel(msg);
		add(pan, BorderLayout.PAGE_START);
		
		if (margin == null) margin = new Insets(5,7,5,7);
		
		txtDetails = new JTextArea(details);
		txtDetails.setEditable(false);
		txtDetails.setLineWrap(true);
		txtDetails.setWrapStyleWord(true);
		txtDetails.setMargin(margin);
		
		JScrollPane scr = new JScrollPane(txtDetails);
		add(scr, BorderLayout.CENTER);
	}
	
	private JPanel createParsedMessagePanel(String message) {
		String[] msgs = message.split("\n");
		lblMessage = new JLabel[msgs.length];
		
		JPanel pan = new JPanel(new GridLayout(0,1));
		
		for (int i=0; i < msgs.length; i++) {
			lblMessage[i] = new JLabel(msgs[i]);
			pan.add(lblMessage[i]);
		}
		
		return pan;
	}
	
	public MessagePanel() {
		this("", "");
	}
	
	public void setDetailsText(String text) {
		if (text != null) txtDetails.setText(text);
	}

}
