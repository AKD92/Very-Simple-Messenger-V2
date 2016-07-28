package net.vsmudp.gui.cassette;

import java.awt.*;
import java.io.*;
import java.util.concurrent.*;
import net.vsmudp.gui.msgdialog.*;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.engine.record.Cassette;
import net.vsmudp.engine.record.VRCDATA;

public class CassetteWriterImpl extends CassetteWriter {
	
	private MessageDialogEx saveError;
	private File lastFile;
	
	// CONSTRUCTOR--------------
	
	CassetteWriterImpl() {
		saveError = null;
		lastFile = null;
	}
	
	private class ErrorGUIBuilder implements Runnable {
		public void run() {
			saveError = new MessageDialogEx(null, "Error", "Error while writing data\nDetails"
					, null, MessageDialogEx.TYPE_ERROR);
		}
	}
	
	// CONSTRUCTION COMPLETE----------
	
	public File getLastFileWritten() {
		return lastFile;
	}
	
	public void saveToFile(Cassette tape, File f) throws IOException {
		VRCDATA vf = VRCDATA.getInstance();
		tape.writeTo(vf);
		
		try {
			vf.writeToFile(f);
		} finally {
			VRCDATA.returnInstance(vf);
		}
	}
	
	public void saveCassetteGUI(Component parent, Cassette tape, File initSel) {
		
		Application app = Application.getInstance();
		JFileChooser filedlg = app.getCassetteFileChooser();
		filedlg.setDialogTitle("Save to file");
		
		if (initSel != null) {
			filedlg.ensureFileIsVisible(initSel);
			filedlg.setSelectedFile(initSel);
		}
		
		int res = filedlg.showSaveDialog(parent);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			
			File f = filedlg.getSelectedFile();
			FileFilter currentFilter = filedlg.getFileFilter();
			FileFilter allFilter = filedlg.getAcceptAllFileFilter();
			
			if (currentFilter != allFilter) {
				f = ((ExtensionFileFilter)currentFilter).getFilePathWithExtension(f);
			}
			
			SaveCassetteWorker sw = new SaveCassetteWorker(tape, f);
			Executor exec = app.backgroundExecutor();
			exec.execute(sw);
			
		}
	}
	
	public void saveCassetteGUI(Component parent, Cassette tape) {
		saveCassetteGUI(parent, tape, null);
	}
	
	public boolean showWriteError(Component parent, Exception ex) {
		
		Printer pr = Application.getInstance().getPrinter();
		
		if (saveError == null) {
			Runnable run = new ErrorGUIBuilder();
			if (SwingUtilities.isEventDispatchThread()) {
				run.run();
			} else {
				return false;
			}
		}
		
		saveError.setParent(parent);
		saveError.setDetailsText(ex.getMessage());
		saveError.run();
		pr.println(ex);
		return true;
	}
}
