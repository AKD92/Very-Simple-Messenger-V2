package net.vsmudp.engine.reaction;

import java.net.*;

import net.vsmudp.Application;
import net.vsmudp.engine.*;

final class UserChattingWithOthersReaction extends AbstractReaction {
	
	private String FRMT_INFO, FRMT_STATUS;
	
	public UserChattingWithOthersReaction() {
		super(NetCommand.USER_CHATTING_WITH_OTHERS);
		FRMT_INFO = "%s is busy with others";
		FRMT_STATUS = "%s is already conected with others & busy";
	}
	
	public final void react(Net net, Signal signal, InetAddress remoteAddress, int remotePort) {
		
		String msg;
		String status;
		Application app = Application.getInstance();
		
		if (signal.isUserNameAvailable() == true) {
			msg = signal.remoteUserName();
		} else {
			msg = remoteAddress.getHostAddress();
		}
		
		msg = String.format(FRMT_INFO, msg);
		status = String.format(FRMT_STATUS, remoteAddress.getHostAddress());
		
		app.setInfoText(msg);
		app.setStatusText(status);
		
	}

}
