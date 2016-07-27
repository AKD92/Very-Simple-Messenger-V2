package net.vsmudp;

import javax.swing.JLabel;
import java.util.Queue;
import java.util.LinkedList;
import net.vsmudp.gui.MainViewer;

class RunnableTextSetter implements Runnable {
	
	private String text;
	private int which;
	
	static final int LOC_STATUS_TEXT, LOC_INFO_TEXT;
	
	private static Queue <RunnableTextSetter> pool;
	
	static {
		pool = new LinkedList <RunnableTextSetter> ();
		LOC_STATUS_TEXT = 2;
		LOC_INFO_TEXT = 3;
	}
	
	private RunnableTextSetter() {
		text = null;
	}
	
	void setTextData(String txt, int where) {
		text = txt;
		which = where;
	}
	
	public void run() {
		
		MainViewer main = Application.getInstance().getMainViewer();
		if (main == null) return;
		
		if (which == LOC_STATUS_TEXT) {
			JLabel statusBar = main.getStatusBarLabel();
			statusBar.setText(text);
		} else if (which == LOC_INFO_TEXT) {
			JLabel infoBar = main.getInfoBoxLabel();
			infoBar.setText(text);
		}
		returnInstance(this);
	}
	
	void runInCurrentThread() {
		run();
	}
	
	synchronized static RunnableTextSetter getInstance() {
		RunnableTextSetter res = null;
		res = pool.poll();
		if (res == null) res = new RunnableTextSetter();
		return res;
	}
	
	private synchronized static boolean returnInstance(RunnableTextSetter obj) {
		if (obj == null) return false;
		return pool.offer(obj);
	}

}
