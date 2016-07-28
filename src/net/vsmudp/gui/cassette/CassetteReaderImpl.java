package net.vsmudp.gui.cassette;

import java.io.*;

import net.vsmudp.engine.record.*;
import net.vsmudp.gui.msgdialog.*;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import net.vsmudp.Application;
import net.vsmudp.Printer;

import java.awt.*;

public class CassetteReaderImpl extends CassetteReader {
	
	private MessageDialogEx openError;
	
	private static final String versionErrorFormat;
	private static final String inputErrorFormat;
	private static final String wrongFileFormat;
	
	private File lastFile;
	
	static {
		wrongFileFormat = "File : %s%nIs not a valid VRC file. Probably you have tried to open a different formatted file with Cassette Viewer.";
		versionErrorFormat = "The file : %s%nIs created by higher versioned VSM. Yout current"
				+ " software cannot open this file. Currently supported VRC file version: %d, version number in selected file: %d\nPlease upgrade your VSM software.";
		inputErrorFormat = "Could not read cassette data, an I/O error occured%nfile: %s%nerror: %s%nProbably this file is damaged or corrupted.";
	}
	
	
	// CONSTRUCTOR--------
	
	CassetteReaderImpl() {
		lastFile = null;
		openError = null;
	}
	
	private class ErrorGUIBuilder implements Runnable {
		public void run() {
			openError = new MessageDialogEx(null, "Error", "Error while reading data\nDetails"
					, null, MessageDialogEx.TYPE_ERROR);
		}
	}
	
	// CONSTRUCTION COMPLETE---------
	
	public File getLastFileRead() {
		return lastFile;
	}
	
	public Cassette readFromFile(File f) throws IOException, CassetteIOException {
		Cassette tape = null;
		VRCDATA vcf = VRCDATA.getInstance();
		
		try {
			vcf.readFromFile(f);
			tape = Cassette.readFrom(vcf);
		} finally {
			VRCDATA.returnInstance(vcf);
		}
		
		return tape;
	}
	
	public Cassette readCassetteGUI(Component parent, File initSel) {
		
		Application app = Application.getInstance();
		
		JFileChooser filedlg = app.getCassetteFileChooser();
		Cassette tape = null;
		File f = null;
		
		filedlg.setDialogTitle("View cassette");
		if (initSel != null) {
			filedlg.ensureFileIsVisible(initSel);
			filedlg.setSelectedFile(initSel);
		}
		int res = filedlg.showOpenDialog(parent);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			
			Cursor cur = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			parent.setCursor(cur);
			
			f = filedlg.getSelectedFile();
			try {
				tape = readFromFile(f);
			} catch (Exception ex) {
				showInputError(parent, f, ex);
			}
			lastFile = f;
			parent.setCursor(null);
		}
		return tape;
	}
	
	public Cassette readCassetteGUI(Component parent) {
		return readCassetteGUI(parent, null);
	}
	
	public final boolean showInputError(Component parent, File inputFile, Exception ex) {
		String msg = null;
		Printer pr = Application.getInstance().getPrinter();
		
		if (openError == null) {
			Runnable run = new ErrorGUIBuilder();
			if (SwingUtilities.isEventDispatchThread()) {
				run.run();
			} else {
				return false;
			}
		}
		
		if (ex instanceof UnsupportedVersionException) {
			UnsupportedVersionException iex = (UnsupportedVersionException) ex;
			msg = String.format(versionErrorFormat, inputFile.getName(), iex.cVer, iex.fVer);
		} else if (ex instanceof WrongFormatException) {
			msg = String.format(wrongFileFormat, inputFile.getPath());
		} else {
			msg = String.format(inputErrorFormat, inputFile.getAbsolutePath(), ex.getMessage());
		}
		openError.setParent(parent);
		openError.setDetailsText(msg);
		openError.run();
		pr.println(msg);
		return true;
	}

}
