package net.vsmudp.gui.vsmviewer;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.gui.TextViewer;

@SuppressWarnings("serial")
public class KeyboardShortcutsAction extends AbstractAction {
	
	private TextViewer dlg;
	private String str_TextAddress, str_DlgTitle;
	private Application app;
	
	public KeyboardShortcutsAction () {
		str_DlgTitle = "Keyboard Shortcuts";
		putValue(Action.NAME, str_DlgTitle);
		str_TextAddress = "gui/res/keyboard.txt";
		dlg = TextViewer.getSharedInstance();
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		URL url = Application.class.getResource(str_TextAddress);
		MainViewer mainView = app.getMainViewer();
		
		try {
			dlg.loadTextDataFrom(url);
			dlg.setAdvertisement(str_DlgTitle);
			if (dlg.isShowing() == false)
				dlg.showDialog(mainView.getWindow(), str_DlgTitle);
			else
				dlg.setTitle(str_DlgTitle);
		} catch (Exception ex) {
			showMessageDialog(mainView.getWindow(), ex.getMessage());
			ex.printStackTrace();
		}
		
	}
}