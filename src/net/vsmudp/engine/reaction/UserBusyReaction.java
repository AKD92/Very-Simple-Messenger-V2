package net.vsmudp.engine.reaction;

import static javax.swing.JOptionPane.*;

import java.net.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.MessageDialog;
import net.vsmudp.engine.edt.PlaySound;

final class UserBusyReaction extends AbstractReaction implements PropertyName {
	
	private MessageDialog msgDlg;
	
	private String FRMT_MSG;
	private String FRMT_TITLE;
	private Icon icon;
	private Application app;
	
	private static final String USER;
	
	static {
		USER = "User";
	}
	
	public UserBusyReaction() {
		super(NetCommand.USER_BUSY);
		icon = null;
		msgDlg = new MessageDialog(null, null, WARNING_MESSAGE, icon);
		FRMT_MSG = "%s ( %s ) is BUSY at this moment%nDenied your connection request";
		FRMT_TITLE = "%s is BUSY";
		app = Application.getInstance();
	}
	
	public final void react(Net net, Signal signal, InetAddress remoteAddress, int remotePort) {
		
		if (icon == null) icon = new ImageIcon(app.getImage(IMG_REQUEST_BUSY_32));
		if (msgDlg.isIconEmpty() == true) msgDlg.setIcon(icon);
		
		String title = null, message = null;
		String hostAddress = remoteAddress.getHostAddress();
		setSound(PlaySound.AUDIO_USER_BUSY);
		
		if (signal.isUserNameAvailable() == true) {
			title = String.format(FRMT_TITLE, signal.remoteUserName());
			message = String.format(FRMT_MSG, signal.remoteUserName(), hostAddress);
		} else {
			title = String.format(FRMT_TITLE, USER);
			message = String.format(FRMT_MSG, USER, hostAddress);
		}
		
		msgDlg.setMessage(message);
		msgDlg.setTitle(title);
		
		playReactionSound();
		
		SwingUtilities.invokeLater(msgDlg);
	}

}
