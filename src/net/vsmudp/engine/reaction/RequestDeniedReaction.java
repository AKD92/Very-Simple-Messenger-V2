package net.vsmudp.engine.reaction;

import java.net.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.MessageDialog;
import net.vsmudp.engine.edt.PlaySound;
import static javax.swing.JOptionPane.*;

final class RequestDeniedReaction extends AbstractReaction implements PropertyName {
	
	private MessageDialog msgDlg;
	private Icon icon;
	
	private String FRMT_REQ_DENIED, FRMT_CONN_DENIED;
	private Application app;
	
	private static final String USER;
	
	static {
		USER = "the user";
	}
	
	public RequestDeniedReaction() {
		super(NetCommand.REQUEST_DENIED);
		icon = null;
		msgDlg = new MessageDialog("Request denied", null, ERROR_MESSAGE, icon);
		FRMT_REQ_DENIED = "Request was denied by %s";
		FRMT_CONN_DENIED = "Connection request is denied by %s ( %s )";
		app = Application.getInstance();
	}
	
	@Override
	public final void react(Net net, Signal signal, InetAddress remoteAddress,
			int remotePort) {
		
		if (icon == null) icon = new ImageIcon(app.getImage(IMG_REQUEST_BUSY_32));
		if (msgDlg.isIconEmpty() == true) msgDlg.setIcon(icon);
		
		Application app = Application.getInstance();
		
		setSound(PlaySound.AUDIO_REQUEST_DENIED);
		
		String str_address = remoteAddress.getHostAddress();
		String status = null, msg = null;
		
		if (signal.isUserNameAvailable() == true) {
			status = String.format(FRMT_REQ_DENIED, signal.remoteUserName(), str_address);
			msg = String.format(FRMT_CONN_DENIED, signal.remoteUserName(), str_address);
		} else {
			status = String.format(FRMT_REQ_DENIED, USER, str_address);
			msg = String.format(FRMT_CONN_DENIED, USER, str_address);
		}
		
		playReactionSound();
		app.setStatusText(status);
		
		msgDlg.setMessage(msg);
		
		println(status);
		SwingUtilities.invokeLater(msgDlg);

	}

}
