package net.vsmudp.engine.edt;

import static javax.swing.JOptionPane.*;

import java.awt.Window;
import java.net.*;
import java.util.Formatter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.Signal;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.gui.util.GUIUtils;

public class AskForConnection implements Runnable, PropertyName {
	
	private InetAddress remoteAddress;
	private int port;
	private int result;
	private String user;
	private Icon icon;
	private PlaySound soundPlay;
	private Application app;
	
	public static final int
			AGREED_OPTION = 0,
			DENIED_OPTION = 1,
			UNTRUSTED_OPTION = 2,
			BUSY_OPTION = 3,
			CLOSED_OPTION = JOptionPane.CLOSED_OPTION;
	
	private static final StringBuilder BUFFER;
	private static final Formatter FRMTR;
	private static final String FRMT_INCOMING, FRMT_USER, TXT_AGREE;
	private static String[] BUTTONS;
	private static final String DIALOG_TITLE;
	
	static {
		BUFFER = new StringBuilder(300);
		FRMTR = new Formatter(BUFFER);
		FRMT_INCOMING = "An user in the address %s on port %d wants to chat with you.%n";
		FRMT_USER = "User name : %s%n";
		TXT_AGREE = "Do you agree to chat?";
		BUTTONS = new String[] {"Accept", "Deny", "Untrusted", "Busy Now"};
		DIALOG_TITLE = "New Request";
	}
	
	public AskForConnection(InetAddress address, int port, String user) {
		app = Application.getInstance();
		remoteAddress = address;
		this.port = port;
		this.user = user;
		icon = null;
		soundPlay = new PlaySound();
	}
	
	public AskForConnection(InetAddress address, Signal signal) {
		this(address, signal.getRemotePort(), signal.remoteUserName());
	}
	
	public int getResult() {
		return result;
	}
	
	public void setUserName(String name) {
		user = name;
	}
	
	public void run() {
		
		if (icon == null) icon = new ImageIcon(app.getImage(IMG_REQUEST_CONNECT_32));
		
		MainViewer mainWin = app.getMainViewer();
		soundPlay.setAudio(PlaySound.AUDIO_ALERT_NEW_REQUEST);
		String str_address = remoteAddress.getHostAddress();
		
		FRMTR.format(FRMT_INCOMING, str_address, port);
		
		if (user != null) {
			FRMTR.format(FRMT_USER, user);
		}
		
		BUFFER.append(TXT_AGREE);
		
		String msg = BUFFER.toString();
		BUFFER.setLength(0);
		
		soundPlay.run();
		
		JPopupMenu menu = mainWin.getListPopupMenu();
		if (menu.isVisible() == true) {
			menu.setVisible(false);
		}
		
		Window parent = GUIUtils.getActiveWindow();
		result = showOptionDialog(parent, msg, DIALOG_TITLE,
				YES_NO_OPTION, INFORMATION_MESSAGE, icon, BUTTONS, BUTTONS[0]);
		
	}
	
}
