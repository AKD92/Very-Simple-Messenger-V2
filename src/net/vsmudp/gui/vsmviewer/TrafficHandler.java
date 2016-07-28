package net.vsmudp.gui.vsmviewer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import net.vsmudp.Application;
import net.vsmudp.engine.TrafficStatistics;
import net.vsmudp.gui.MainViewer;

public class TrafficHandler implements ActionListener {
	
	Dimension minSize;
	Dimension size;
	String title;
	
	private Application app;
	
	public TrafficHandler() {
		minSize = new Dimension(200, 300);
		size = new Dimension();
		size.width = minSize.width + 80;
		size.height = minSize.height + 50;
		title = "Traffic Statistics";
		app = Application.getInstance();
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		MainViewer mainView = app.getMainViewer();
		
		JDialog jd = new JDialog((JFrame)mainView.getWindow(), true);
		jd.setTitle(title);
		jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		TrafficStatistics ts = new TrafficStatistics();
		ts.createPreviewPane(jd);
		jd.setContentPane(ts.getPreviewPane());
		jd.pack();
		
		jd.setSize(size);
		jd.setMinimumSize(minSize);
		
		jd.setLocationRelativeTo(mainView.getWindow());
		jd.setVisible(true);
	}
}
