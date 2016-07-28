package net.vsmudp.gui.vsmviewer;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.vsmudp.Application;
import net.vsmudp.engine.record.Cassette;
import net.vsmudp.gui.CassetteViewer;
import net.vsmudp.gui.cassette.CassetteReader;

@SuppressWarnings("serial")
public class ViewExistingTapeAction extends AbstractAction {
	
	private CassetteReader reader;
	private Application app;
	
	public ViewExistingTapeAction() {
		app = Application.getInstance();
		reader = CassetteReader.getReader();
		super.putValue(Action.NAME, "View Existing Cassette");
		super.putValue(Action.SHORT_DESCRIPTION, "View Cassette that was saved in a file");
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		Window mainWin = app.getMainViewer().getWindow();
		Cassette tape = reader.readCassetteGUI(mainWin);
		
		if (tape != null) {
			CassetteViewer view = CassetteViewer.getInstance();
			view.setFileDetails(reader.getLastFileRead());
			view.setCassette(tape);
			view.showDialog((Frame)mainWin);
		}
	}
}
