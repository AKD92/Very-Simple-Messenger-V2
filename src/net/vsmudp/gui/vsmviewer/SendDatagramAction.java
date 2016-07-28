package net.vsmudp.gui.vsmviewer;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.engine.Message;
import net.vsmudp.engine.Net;

@SuppressWarnings("serial")
public class SendDatagramAction extends AbstractAction implements PropertyName{
	
	private String msg_notConnected;
	private Application app;
	
	public SendDatagramAction() {
		app = Application.getInstance();
		putValue(Action.NAME, "Send Message");
		putValue(Action.SHORT_DESCRIPTION, "Send text message to connected host");
		msg_notConnected = "You are not yet connected to any foreign host\nPlease make a connection first";
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		MainViewer mainView = app.getMainViewer();
		Window mainWin = mainView.getWindow();
		
		String text = mainView.getTextTyperBox().getText();
		Net net = Net.getCurrent();
		
		boolean lineOK = (net == null || net.isConnected());
		
		if (lineOK == false) {
			showMessageDialog(mainWin, msg_notConnected, "Sorry", ERROR_MESSAGE);
			
		} else {
			if (text.length() != 0) {
				try {
					
					mainView.getMessageList().clearSelection();
					net.sendDatagram(text);
					
					Message msg = new Message(text, Message.MESSAGE_SENT_BY_ME);
					mainView.addMessageToMessageList(msg);
					mainView.selectLastMessageInList();
					
					JTextField textField = mainView.getTextTyperBox();
					textField.selectAll();
					
				} catch (IOException ex) {
					String msg = "There is a problem in sending message to the\n"
							+ "remote user. May be your internet connection is lost.\n"
							+ "Problem: " + ex.getMessage();
					
					showMessageDialog(mainWin, msg, "Error", ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		}
	}
}
