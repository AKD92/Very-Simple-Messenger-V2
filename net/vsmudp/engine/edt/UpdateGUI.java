package net.vsmudp.engine.edt;

import javax.swing.JPopupMenu;

import net.vsmudp.Application;
import net.vsmudp.engine.Message;
import net.vsmudp.gui.MainViewer;

public class UpdateGUI implements Runnable {
	
	private Message msg;
	private PlaySound audio;
	private Application app;
	
	public UpdateGUI (Message mesg) {
		msg = mesg;
		audio = new PlaySound(PlaySound.AUDIO_MSG_RECEIVED);
		app = Application.getInstance();
	}
	
	public static UpdateGUI getNewInstance() {
		return new UpdateGUI(null);
	}
	public static UpdateGUI getNewInstance(Message mesg) {
		return new UpdateGUI(mesg);
	}
	
	public void setMessage(Message mesg) {
		this.msg = mesg;
	}
	
	public void run() {
		
		MainViewer main = app.getMainViewer();
		
		if (main != null && msg != null) {
			
			audio.run();
			
			JPopupMenu menu = main.getListPopupMenu();
			if (menu.isVisible() == true) {
				menu.setVisible(false);
			}
			
			main.addMessageToMessageList(msg);
			main.selectLastMessageInList();
		}
	}
}
