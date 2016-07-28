package net.vsmudp.engine.edt;

import java.awt.Component;
import javax.swing.Icon;
import net.vsmudp.gui.util.GUIUtils;
import static javax.swing.JOptionPane.*;

public class MessageDialog implements Runnable {
	
	private String title;
	private Object message;
	private int msgtype;
	private Icon icn;
	
	public MessageDialog(String title, Object msg, int msgtype, Icon icon) {
		this.title = title;
		message = msg;
		this.msgtype =msgtype;
		icn = icon;
	}
	
	public void setMessage(Object msg) {
		message = msg;
	}
	public void setMessageType(int type) {
		msgtype = type;
	}
	public void setTitle(String t) {
		title = t;
	}
	public void setIcon(Icon icon) {
		icn = icon;
	}
	public boolean isIconEmpty() {
		if (icn == null) return true;
		else return false;
	}
	
	public final void run() {
		Component parent = GUIUtils.getActiveWindow();
		showMessageDialog(parent, message, title, msgtype, icn);
	}
	
	
}
