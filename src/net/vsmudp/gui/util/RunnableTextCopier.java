package net.vsmudp.gui.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.vsmudp.Application;
import net.vsmudp.Printer;

import java.util.concurrent.Executor;

public class RunnableTextCopier implements Runnable {
	
	private String text;
	
	public RunnableTextCopier(String txt) {
		setText(txt);
	}
	
	public void setText(String txt) {
		text = txt;
	}
	
	public void run() {
		StringSelection sk = new StringSelection(text);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Clipboard cb = tk.getSystemClipboard();
		try {
			cb.setContents(sk, null);
		} catch (IllegalStateException ex) {
			Printer pr = Application.getInstance().getPrinter();
			pr.println("Clipboard is unavailable at this moment");
		}
	}
	
	public void runInBackground() {
		Executor exec = Application.getInstance().backgroundExecutor();
		exec.execute(this);
	}
}
