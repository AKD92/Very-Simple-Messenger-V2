package net.vsmudp.gui.vsmviewer;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_D;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.Net;
import net.vsmudp.engine.edt.ConnectDisconnectSwap;
import net.vsmudp.engine.edt.PlaySound;

@SuppressWarnings("serial")
public class DisconnectAction extends AbstractAction implements PropertyName {
	
	private ConnectDisconnectSwap cds;
	private PlaySound soundPlay;
	private Application app;
	
	public DisconnectAction() {
		setEnabled(false);
		
		putValue(Action.NAME, "Disconnect");
		putValue(Action.SHORT_DESCRIPTION, "Disconnect from remote host");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(VK_D, ALT_DOWN_MASK, false));
		
		cds = new ConnectDisconnectSwap(true);
		soundPlay = new PlaySound();
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		Net net = Net.getCurrent();
		soundPlay.setAudio(PlaySound.AUDIO_CONNECTION_BROKEN);
		
		if (net != null) {
			
			net.disconnectFromHost(true); // automatic Reconnect is on
			soundPlay.run();
			cds.run();
			
			String text = "Disconnected from remote host by user";
			app.setStatusText(text);
			app.setInfoText("Connection is broken");
			
		}
	}
}
