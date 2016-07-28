package net.vsmudp.gui.vsmviewer;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;

import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.record.Cassette;
import net.vsmudp.engine.record.TapeRecorder;
import net.vsmudp.gui.cassette.CassetteWriter;

public class LoadNewCassetteListener implements ActionListener, PropertyName {
	
	private Icon dialogIcon;
	private String title, msg_loaded, msg_loaded2, msg_clear, msg_save;
	
	private Application app;
	
	public LoadNewCassetteListener() {
		dialogIcon = null;
		title = "Cassette state";
		msg_loaded = "A new cassette has been loaded in the recorder.";
		msg_loaded2 = "New cassette has been loaded in the recorder";
		msg_clear = "A fresh cassette is already in the recorder.\nThis cassette is eligible for recording.";
		msg_save = "There is a cassette which is recorded. Save cassette to file?";
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		TapeRecorder recorder = TapeRecorder.getRecorder();
		String msg = null;
		int type = 0;
		
		Window mainWin = app.getMainViewer().getWindow();
		
		if (dialogIcon == null) {
			Image img = app.getImage(IMG_FLOPPY_2_32);
			if (img != null) dialogIcon = new ImageIcon(img);
		}
		
		Cassette tape = recorder.getCassette();
		if (tape == null) {
			msg = msg_loaded;
			type = INFORMATION_MESSAGE;
			recorder.loadANewCassette();
		} else {
			int state = tape.getState();
			if (state == Cassette.STATE_FRESH) {
				msg = msg_clear;
				type = INFORMATION_MESSAGE;
			} else {
				askForSave(tape, mainWin);
				msg = msg_loaded2;
				type = INFORMATION_MESSAGE;
				recorder.loadANewCassette();
			}
		}
		
		showMessageDialog(mainWin, msg, title, type, dialogIcon);
	}
	
	private void askForSave(Cassette tape, Window parent) {
		String msg = msg_save;
		String[] buttons = new String[] {"Save now", "Discard"};
		Image img = app.getImage(IMG_FLOPPY_32);
		Icon icon = null;
		if (img != null) icon = new ImageIcon(img);
		
		int res = showOptionDialog(parent, msg, "Save", 0, PLAIN_MESSAGE,
				icon, buttons, buttons[0]);
		if (res == 0) {
			CassetteWriter ctr = CassetteWriter.getWriter();
			ctr.saveCassetteGUI(parent, tape);
		}
	}
}
