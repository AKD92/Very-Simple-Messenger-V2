package net.vsmudp.engine.reaction;

import javax.swing.JTextField;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;

class SetFocusOnTextBox implements Runnable {
	
	private MainViewer main;
	private JTextField txtSend;
	private Application app;
	
	public SetFocusOnTextBox() {
		main = null;
		txtSend = null;
		app = Application.getInstance();
	}
	
	public void run() {
		
		main = app.getMainViewer();
		
		if (main != null) {
			txtSend = main.getTextTyperBox();
			txtSend.setText(null);
			txtSend.requestFocusInWindow();
		}
	}
}
