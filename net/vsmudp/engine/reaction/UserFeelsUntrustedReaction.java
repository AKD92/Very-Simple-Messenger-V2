package net.vsmudp.engine.reaction;



import java.net.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import static javax.swing.JOptionPane.*;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.MessageDialog;
import net.vsmudp.engine.edt.PlaySound;

final class UserFeelsUntrustedReaction extends AbstractReaction implements PropertyName {
	
	private MessageDialog msgDlg;
	
	private String FRMT_MSG, FRMT_TITLE;
	private Icon icon;
	private Application app;
	
	private static final String USER;
	
	static {
		USER = "User";
	}
	
	public UserFeelsUntrustedReaction() {
		super(NetCommand.USER_FEELS_UNTRUSTED);
		icon = null;
		msgDlg = new MessageDialog(null, null, WARNING_MESSAGE, icon);
		FRMT_MSG = "You were recognized Untrusted by %s%nDenied the connection request";
		FRMT_TITLE = "%s feels UNTRUSTED";
		app = Application.getInstance();
	}
	
	public final void react(Net net, Signal signal, InetAddress remoteAddress, int remotePort) {
		
		String image = IMG_REQUEST_UNTRUSTED_32;
		if (icon == null) icon = new ImageIcon(app.getImage(image));
		if (msgDlg.isIconEmpty() == true) msgDlg.setIcon(icon);
		
		String msg = String.format(FRMT_MSG, remoteAddress.getHostAddress());
		setSound(PlaySound.AUDIO_USER_BUSY);
		String title = null;
		
		if (signal.isUserNameAvailable() == true) {
			title = String.format(FRMT_TITLE, signal.remoteUserName());
		} else {
			title = String.format(FRMT_TITLE, USER);
		}
		
		msgDlg.setMessage(msg);
		msgDlg.setTitle(title);
		
		playReactionSound();
		
		SwingUtilities.invokeLater(msgDlg);
	}

}
