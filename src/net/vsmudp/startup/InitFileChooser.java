package net.vsmudp.startup;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.vsmudp.Application;
import net.vsmudp.gui.cassette.CassetteFileFilter;

public class InitFileChooser implements Runnable {
	
	public void run() {
		initCassetteFileChooser();
	}
	
	private void initCassetteFileChooser() {
		JFileChooser filedlg = new JFileChooser();
		filedlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
		filedlg.setMultiSelectionEnabled(false);
		filedlg.setAutoscrolls(true);
		filedlg.setAcceptAllFileFilterUsed(true);
		
		FileFilter filter = CassetteFileFilter.getSharedInstance();
		filedlg.addChoosableFileFilter(filter);
		filedlg.setFileFilter(filter);
		Application.getInstance().setCassetteFileChooser(filedlg);
	}
}
