package net.vsmudp.gui.vsmviewer;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.Net;
import net.vsmudp.gui.MainViewer;

public class MyAddressViewHandler implements ActionListener, PropertyName {
	
	private String str_MsgFormat, str_Title;
	private Icon dialogIcon;
	private Application app;
	
	public MyAddressViewHandler() {
		str_MsgFormat = "Your address is %s on port %d";
		dialogIcon = null;
		str_Title = "IP Address";
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		String msg = null;
		Net net = Net.getCurrent();
		MainViewer mainView = app.getMainViewer();
		
		if (net == null) {
			msg = "This program has been failed to make a socket in the local host";
		} else {
			
			if (dialogIcon == null) {
				Image img = app.getImage(IMG_BULB_32);
				if (img != null) dialogIcon = new ImageIcon(img);
			}
			
			String host = net.getLocalAddress().getHostAddress();
			int port = net.getLocalPort();
			msg = String.format(str_MsgFormat, host, port);
		}
		
		showMessageDialog(mainView.getWindow(), msg, str_Title, 0, dialogIcon);
	}
}
