package net.vsmudp.gui.vsmviewer;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_C;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.Net;
import net.vsmudp.engine.NetCommand;
import net.vsmudp.gui.AddressMaker;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.gui.Viewer;
import net.vsmudp.gui.msgdialog.MessageDialogEx;

@SuppressWarnings("serial")
public class ConnectAction extends AbstractAction implements PropertyName {
	
	private AddressMaker adrsMaker;
	private String frmt_sent;
	private String msg_error;
	private String initialAddress;
	private Icon webIcon;
	
	private Application app;
	
	public ConnectAction() {
		putValue(Action.NAME, "Connect");
		putValue(Action.SHORT_DESCRIPTION, "Connect to remote host");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(VK_C, ALT_DOWN_MASK, false));
		adrsMaker = AddressMaker.getInstance();
		frmt_sent = "Request sent to %s";
		msg_error = "Error";
		initialAddress = null; webIcon = null;
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		Net net = Net.getCurrent();
		InetAddress remote = null;
		MainViewer mainView = app.getMainViewer();
		
		if (net == null) {
			String msg = "Network initialization has been failed.\n"
					+ "Please see: " + app.VSM_HOME + app.LOG_FILE;
			showMessageDialog(mainView.getWindow(), msg, msg_error, ERROR_MESSAGE);
			return;
		}
		
		Window win = mainView.getWindow();
		if (webIcon == null) webIcon = new ImageIcon(app.getImage(IMG_INTERNET_32));
		if (initialAddress == null) initialAddress = net.getLocalAddress().getHostAddress();
		
		int res = adrsMaker.showDialog(win, webIcon, initialAddress);
		
		if (res == Viewer.OPTION_PASSED) {
			remote = adrsMaker.getAddress();
			
			try {
				initialAddress = adrsMaker.getAddressAsTyped();
				net.sendSignalMessage(remote, NetCommand.REQUEST_CONNECT);
				
				String status = String.format(frmt_sent, remote.getHostAddress());
				app.setStatusText(status);
			} catch (SocketException ex) {
				ex.printStackTrace(System.err);
				String details = "You are trying to connect with such "
						+ "a machine\nthat is not valid. Address %s is not valid";
				details = String.format(details, remote.getHostAddress());
				
				System.err.println(details);
				String msg = "Cannot send connect request.\nDetails";
				
				MessageDialogEx mg = new MessageDialogEx(win,
						msg_error, msg, details + "\n" + ex.getMessage(), MessageDialogEx.TYPE_ERROR);
				mg.run();
				
			} catch (UnknownHostException ex) {
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		adrsMaker.reset();
		
	}	// end actionPerformed
	
}