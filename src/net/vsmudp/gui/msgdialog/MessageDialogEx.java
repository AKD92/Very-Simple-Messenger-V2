package net.vsmudp.gui.msgdialog;

import static javax.swing.JOptionPane.*;

import java.awt.Component;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class MessageDialogEx extends MessagePanel implements Runnable {
	
	private Component parent;
	private String title;
	private int type;
	
	public static final int TYPE_PLAIN = PLAIN_MESSAGE;
	public static final int TYPE_ERROR = ERROR_MESSAGE;
	public static final int TYPE_INFORMATION = INFORMATION_MESSAGE;
	public static final int TYPE_WARNING = WARNING_MESSAGE;
	
	public MessageDialogEx(Component parent, String tit, String msg, String details, int ty) {
		super(msg, details);
		this.parent = parent;
		title = tit;
		//setMinimumSize(new Dimension(200,150));
		setPreferredSize(new Dimension(400,150));
	}
	
	public final void run() {
		showMessageDialog(parent, this, title, type);
	}
	
	public final void setParent(Component parent) {
		this.parent = parent;
	}

}
