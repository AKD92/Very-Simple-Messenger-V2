package net.vsmudp.gui.vsmviewer;

import static java.awt.event.KeyEvent.VK_F2;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.gui.OptionsViewer;

@SuppressWarnings("serial")
public class ShowOptionsAction extends AbstractAction {
	
	private OptionsViewer optView;
	private Application app;
	
	public ShowOptionsAction() {
		app = Application.getInstance();
		putValue(Action.NAME, "Options");
		putValue(Action.SHORT_DESCRIPTION, "Show options dialog");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(VK_F2, 0));
		
		optView = OptionsViewer.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		MainViewer mainView = app.getMainViewer();
		Window parent = mainView.getWindow();
		optView.showDialog(parent, OptionsViewer.TAB_GENERAL);
	}
	
}
