package net.vsmudp.engine.reaction;

import java.io.IOException;
import java.net.*;

import javax.swing.SwingUtilities;

import net.vsmudp.Application;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.CloseCriticalWindow;
import net.vsmudp.engine.edt.ConnectDisconnectSwap;
import net.vsmudp.engine.edt.PlaySound;

final class RequestAcceptedReaction extends AbstractReaction {
	
	private Runnable clearMessages;
	private Runnable setFocusOnText;
	private Runnable closeWindow;
	private ConnectDisconnectSwap cds;
	
	private String frmt_user, frmt_con, frmt_respond;
	
	public RequestAcceptedReaction() {
		super(NetCommand.REQUEST_ACCEPTED);
		clearMessages = new ClearMessages();
		setFocusOnText = new SetFocusOnTextBox();
		cds = new ConnectDisconnectSwap();
		closeWindow = new CloseCriticalWindow();
		frmt_user = "You and %s";
		frmt_con = "Connection implemented to %s on port %d";
		frmt_respond = "%s responded...";
	}
	
	private final boolean isThisHost(InetAddress address, Net net) {
		if (net.getLocalAddress().equals(address)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public final void react(Net net, Signal signal, InetAddress remoteAddress, int remotePort) {
		
		setSound(PlaySound.AUDIO_REQUEST_ACCEPTED);
		Application app = Application.getInstance();
		
		if (net != null) {
			
			// isThisHost() means that, I am trying to connect with myself....
			
			// if I have sent a connection request to someone & he responded
			// with AGRED_OPTION, and I am Free now (not aonnected with any person)
			// then..... I will be automatically connected with him......
			
			boolean isThisMachine = isThisHost(remoteAddress, net);
			boolean isNetConnected = net.isConnected();
			
			if (isNetConnected == false) {
				
				net.connectToHost(remoteAddress, remotePort, signal.remoteUserName());
				net.openReceiverThread();
				
				String big = String.format(frmt_user, remoteAddress.getHostAddress());
				if (signal.isUserNameAvailable() == true) {
					big = big.replace(remoteAddress.getHostAddress(), signal.remoteUserName());
				}
				
				playReactionSound();
				
				app.setInfoText(big);
				
				String status = String.format(frmt_con
						, remoteAddress.getHostAddress(), remotePort);
				
				println("Request accepted from " + remoteAddress.getHostAddress());
				
				cds.setConnectStatus(false);
				
				SwingUtilities.invokeLater( closeWindow);
				app.setStatusText(status);
				SwingUtilities.invokeLater( cds);
				SwingUtilities.invokeLater( clearMessages);
				SwingUtilities.invokeLater( setFocusOnText);
				
			}
			
			// if I have sent a connection request to someone & he did not
			// responded anything,, now I am busy with another person in chatting,
			// then...... This propgram automatically send a BUSY signal to
			// the person who responded through the AGREED_OPTION.......
			else if (isNetConnected == true) {
				
				if (isThisMachine == false) {
					String big = String.format(frmt_respond, remoteAddress.getHostAddress());
					
					if (signal.isUserNameAvailable()) {
						big = signal.remoteUserName() + " " + big;
					}
					
					app.setInfoText(big);
					
					try {
						net.sendSignalMessage(remoteAddress, NetCommand.USER_CHATTING_WITH_OTHERS);
					} catch (IOException e) {
					}
				
				} //else {
					// if isThisMachine == true, then no special things... only ignore this
				//}
			}
			
		}
	}

}
