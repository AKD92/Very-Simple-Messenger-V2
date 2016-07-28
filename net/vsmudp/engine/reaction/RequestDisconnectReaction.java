package net.vsmudp.engine.reaction;

import net.vsmudp.Application;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.ConnectDisconnectSwap;
import net.vsmudp.engine.edt.PlaySound;

import java.net.*;
import javax.swing.SwingUtilities;

final class RequestDisconnectReaction extends AbstractReaction {
	
	private ConnectDisconnectSwap cds;
	
	private String FRMT_DISCONNECTED, FRMT_NAMED_DISCN;
	private String endString;
	
	public RequestDisconnectReaction() {
		super(NetCommand.REQUEST_DISCONNECT);
		cds = new ConnectDisconnectSwap();
		FRMT_DISCONNECTED = "Disconnected by foreign user (%s)";
		FRMT_NAMED_DISCN = "%s disconnected you";
		endString = "Received disconnect signal but the local machine is already disconnected";
	}
	
	public final void react(Net net, Signal signal, InetAddress remoteAddress, int remotePort) {
		
		Application app = Application.getInstance();
		if (net != null) {
			
			String status = null, infoText = null;
			setSound(PlaySound.AUDIO_CONNECTION_BROKEN);
			
			if (net.isConnected() == true) {
				
				net.disconnectFromHost(true); 	// disconnect from remote host
												// subsequently apply automaticReconnect
				status = String.format(FRMT_DISCONNECTED, remoteAddress.getHostAddress());
				
				if (signal.isUserNameAvailable() == true) {
					infoText = String.format(FRMT_NAMED_DISCN, signal.remoteUserName());
				}
				
				println(status);
				
				app.setStatusText(status);
				if (infoText != null) app.setInfoText(infoText);
				
				playReactionSound();
				
				cds.setConnectStatus(true);
				SwingUtilities.invokeLater( cds);
				
			} else  {
				status = endString;
				println(status);
			}
			
		} else {
			println("NET is not initialized (null)");
		}
	}

}
