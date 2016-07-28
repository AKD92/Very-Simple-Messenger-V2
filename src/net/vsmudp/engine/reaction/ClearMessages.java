package net.vsmudp.engine.reaction;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;

public class ClearMessages implements Runnable {
	
	private MainViewer main;
	private Application app;
	
	public ClearMessages() {
		main = null;
		app = Application.getInstance();
	}
	
	//@Override
	public final void run() {
		
		main = app.getMainViewer();
		if (main != null) main.clearAllMessages();
		
	}
}
