package net.vsmudp.engine.reaction;

import javax.swing.SwingUtilities;
import java.net.*;

import net.vsmudp.Application;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.AskForConnection;
import net.vsmudp.engine.edt.ConnectDisconnectSwap;
import net.vsmudp.engine.edt.PlaySound;
import net.vsmudp.engine.edt.CloseCriticalWindow;

final class RequestConnectReaction extends AbstractReaction {
	
	private Runnable clearMessages;
	private Runnable setFocusOnText;
	private Runnable closeWindow;
	private ConnectDisconnectSwap cds;
	
	private static String FRMT_INFO, FRMT_CONNECTED;
	
	static {
		FRMT_INFO = "You and %s";
		FRMT_CONNECTED = "Connection implemented to %s on port %d";
	}
	
	public RequestConnectReaction() {
		super(NetCommand.REQUEST_CONNECT);
		clearMessages = new ClearMessages();
		setFocusOnText = new SetFocusOnTextBox();
		cds = new ConnectDisconnectSwap();
		closeWindow = new CloseCriticalWindow();
	}
	
	public final void react(Net net, Signal signal, InetAddress remoteAddress, int remotePort) {
		
		setSound(PlaySound.AUDIO_REQUEST_ACCEPTED);
		Application app = Application.getInstance();
		
		try {
		
		println("Got connection request from " + remoteAddress.getHostAddress());
		
		AskForConnection ask = new AskForConnection(remoteAddress, signal);
		
		SwingUtilities.invokeAndWait(ask);
		int result = ask.getResult();
		
		if (result == AskForConnection.AGREED_OPTION) {
			
			SwingUtilities.invokeLater(closeWindow);
			net.sendSignalMessage(remoteAddress, NetCommand.REQUEST_ACCEPTED);
			
			// if the user is already chatting with someone,
			// that is, if the user is connected to anybody,
			// then previous connection will be broken & new
			// connection will be established.
			
			if (net.isConnected() == true) net.disconnectFromHost(true);
			
			net.connectToHost(remoteAddress, remotePort, signal.remoteUserName());
			net.openReceiverThread();
			
			String infoText = FRMT_INFO;
			String postfix = null;
			
			if (signal.isUserNameAvailable() == true) {
				postfix = signal.remoteUserName();
			} else postfix = remoteAddress.getHostAddress();
			
			infoText = String.format(infoText, postfix);
			
			String status = String.format(FRMT_CONNECTED, remoteAddress.getHostAddress(), remotePort);
			
			playReactionSound();
			
			app.setStatusText(status);
			app.setInfoText(infoText);
			cds.setConnectStatus(false);
			
			SwingUtilities.invokeLater(cds);
			SwingUtilities.invokeLater(clearMessages);
			SwingUtilities.invokeLater(setFocusOnText);
			
		} else if (result == AskForConnection.DENIED_OPTION
				|| result == AskForConnection.CLOSED_OPTION) {
			net.sendSignalMessage(remoteAddress, NetCommand.REQUEST_DENIED);
			
		} else if (result == AskForConnection.BUSY_OPTION) {
			net.sendSignalMessage(remoteAddress, NetCommand.USER_BUSY);
			
		} else if (result == AskForConnection.UNTRUSTED_OPTION){
			net.sendSignalMessage(remoteAddress, NetCommand.USER_FEELS_UNTRUSTED);
		}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
