package net.vsmudp.gui.cassette;

import java.io.*;

import java.awt.*;
import javax.swing.*;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.record.*;
import net.vsmudp.gui.CassetteViewer;
import net.vsmudp.gui.util.GUIUtils;

public class SaveCassetteWorker implements Runnable, PropertyName {
	
	private Cassette tape;
	private File saveFile;
	
	private static String successMsg;
	private static String okDlgName;
	
	static {
		successMsg = "Writing successfull to%n%s";
		okDlgName = "Successfull";
	}
	
	SaveCassetteWorker(Cassette c, File f) {
		tape = c;
		saveFile = f;
	}
	
	public void run() {
		
		ResultGUI res= new ResultGUI();
		Printer pr = Application.getInstance().getPrinter();
		
		CassetteWriter writer = CassetteWriter.getWriter();
		
		try {
			writer.saveToFile(tape, saveFile);
			res.ok = true;
		} catch (IOException ex) {
			pr.printStackTrace(ex);
			res.ex = ex;
		}
		
		SwingUtilities.invokeLater(res);
	}
	
	private class ResultGUI implements Runnable {
		
		boolean ok;
		Exception ex;
		
		ResultGUI() {
			ok = false;
			ex = null;
		}
		
		public void run() {
			Window parent = GUIUtils.getActiveWindow();
			
			if (ok == true) ok(parent);
			else error(parent);
		}
		
		private void ok(Window parent) {
			
			Application app = Application.getInstance();
			Icon icn = new ImageIcon(app.getImage(IMG_FLOPPY_32));
			
			CassetteViewer cv = CassetteViewer.getInstance();
			if (cv.isShowing() == true) cv.setFileDetails(saveFile);
			
			String conf = String.format(successMsg, saveFile.getAbsolutePath());
			showMessageDialog(parent, conf, okDlgName, PLAIN_MESSAGE, icn);
		}
		
		private void error(Window parent) {
			CassetteWriter clt = CassetteWriter.getWriter();
			clt.showWriteError(parent, ex);
		}
	}

}
