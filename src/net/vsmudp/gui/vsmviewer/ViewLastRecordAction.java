package net.vsmudp.gui.vsmviewer;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.vsmudp.Application;
import net.vsmudp.engine.record.Cassette;
import net.vsmudp.engine.record.TapeRecorder;
import net.vsmudp.gui.CassetteViewer;

@SuppressWarnings("serial")
public class ViewLastRecordAction extends AbstractAction {
	
	private String msg_empty;
	private String msg_running;
	private Application app;
	
	public ViewLastRecordAction () {
		app = Application.getInstance();
		putValue(Action.NAME, "View Last Record");
		putValue(Action.SHORT_DESCRIPTION, "View Last Recorded Message (if you recorded any)");
		msg_running = "Cassette is in running state. Please\nstop recording this cassette and try again";
		msg_empty = "Cassette is empty. Please recorde a conversation and then view";
	}
	
	public void actionPerformed(ActionEvent evt) {
		Cassette cas = TapeRecorder.getRecorder().getCassette();
		CassetteViewer view = CassetteViewer.getInstance();
		
		Window mainWin = app.getMainViewer().getWindow();
		
		if (cas != null) {
			int state = cas.getState();
			if (state == Cassette.STATE_CLOSED) {
				view.setCassette(cas);
				view.showDialog((Frame)mainWin);
			} else if (state == Cassette.STATE_RUNNING){
				String msg = msg_running;
				showMessageDialog(mainWin,msg,"Oops",INFORMATION_MESSAGE);
			}
			else if (state == Cassette.STATE_FRESH) {
				String msg = msg_empty;
				showMessageDialog(mainWin, msg, "Empty Cassette", INFORMATION_MESSAGE);
			}
		}
		
	}
}
