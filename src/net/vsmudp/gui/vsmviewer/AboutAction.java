package net.vsmudp.gui.vsmviewer;

import static java.awt.event.KeyEvent.VK_F1;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.gui.TextViewer;

@SuppressWarnings("serial")
public class AboutAction extends AbstractAction {
	
	private TextViewer dlg;
	private String str_TextAddress, str_DlgTitle, str_Advertise;
	
	private Application app;
	
	public AboutAction() {
		str_DlgTitle = "About";
		putValue(Action.NAME, str_DlgTitle);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(VK_F1, 0));
		str_TextAddress = "gui/res/Readme.txt";
		dlg = TextViewer.getSharedInstance();
		str_Advertise = "About This Program";
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		URL url = Application.class.getResource(str_TextAddress);
		MainViewer mainView = app.getMainViewer();
		
		try {
			dlg.loadTextDataFrom(url);
			dlg.setAdvertisement(str_Advertise);
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
