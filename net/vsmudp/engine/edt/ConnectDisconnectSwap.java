package net.vsmudp.engine.edt;

import net.vsmudp.Application;
import net.vsmudp.gui.*;

import javax.swing.Action;

public class ConnectDisconnectSwap implements Runnable {
	
	private boolean statusConnect;
	private boolean statusDisconnect;
	private boolean readyToUse;
	
	private MainViewer main;
	private Action actionConnect, actionDiscon;
	private Application app;
	
	private static String errorMsg;
	
	static {
		errorMsg = "Connect & Disconnect value cannot be same";
	}
	
	public ConnectDisconnectSwap() {
		statusConnect = statusDisconnect = readyToUse = false;
		app = Application.getInstance();
	}
	public ConnectDisconnectSwap(boolean connect) {
		this();
		setConnectStatus(connect);
	}
	
	public void setConnectStatus(boolean connect) {
		statusConnect = connect;
		statusDisconnect = !connect;
		if (readyToUse == false) readyToUse = true;
	}
	
	public void setDisconnectStatus(boolean disconnect) {
		setConnectStatus(!disconnect);
	}
	
	public final void run() {
		main = app.getMainViewer();
		if (readyToUse == false) throw new UnsupportedOperationException(errorMsg);
		
		if (main != null) {
			
			actionConnect = main.getConnectAction();
			actionDiscon = main.getDisconnectAction();
			
			actionConnect.setEnabled(statusConnect);
			actionDiscon.setEnabled(statusDisconnect);
			
		}
	}

}
