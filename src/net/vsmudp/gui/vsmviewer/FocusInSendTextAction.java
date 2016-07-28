package net.vsmudp.gui.vsmviewer;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;

@SuppressWarnings("serial")
public class FocusInSendTextAction extends AbstractAction {
	
	private Application app;
	
	public FocusInSendTextAction () {
		putValue (Action.NAME, "Select All Texts");
		app = Application.getInstance();
	}
	
	public final void actionPerformed(ActionEvent evt) {
		MainViewer mainView = app.getMainViewer();
		JTextField txt = mainView.getTextTyperBox();
		txt.requestFocusInWindow();
	}
}
