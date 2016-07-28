package net.vsmudp.gui.vsmviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;

import net.vsmudp.Application;
import net.vsmudp.gui.MainViewer;

public class DateTimeViewHandler implements ActionListener {
	
	private Date now;
	private long current;
	private String pattern;
	private SimpleDateFormat frm;
	private Application app;
	
	public DateTimeViewHandler() {
		pattern = "hh : mm : ss a";
		frm = new SimpleDateFormat(pattern);
		now = new Date();
		app = Application.getInstance();
	}
	
	private final String getFormattedTime() {
		return frm.format(now);
	}
	
	public final void actionPerformed(ActionEvent evt) {
		current = System.currentTimeMillis();
		now.setTime(current);
		
		MainViewer mainView = app.getMainViewer();
		JLabel lbl = mainView.getTimeViewLabel();
		lbl.setText(getFormattedTime());
	}
}