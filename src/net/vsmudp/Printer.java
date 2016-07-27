package net.vsmudp;

import java.util.Formatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class Printer implements Runnable {
	
	private String text;
	private StringBuilder buffer;
	private Formatter formatter;
	private int style;
	
	private Throwable exep;
	
	private Semaphore security;
	
	public static final int STYLE_PRINTF,
							STYLE_PRINTLN,
							STYLE_STACKTRACE;
	
	static {
		STYLE_PRINTF = 0;
		STYLE_PRINTLN = 1;
		STYLE_STACKTRACE = 2;
	}
	
	Printer() {
		exep = null;
		text = null;
		buffer = new StringBuilder(500);
		formatter = new Formatter(buffer);
		security = new Semaphore(1);
	}
	
	public void println(String str) {
		security.acquireUninterruptibly();
		text = str;
		style = STYLE_PRINTLN;
		runInBackground();
	}
	public void println(Object obj) {
		println(obj.toString());
	}
	
	public void printf(String frmt, Object... args) {
		security.acquireUninterruptibly();
		buffer.setLength(0);
		formatter.format(frmt, args);
		text = buffer.toString();
		style = STYLE_PRINTF;
		runInBackground();
	}
	
	public void printStackTrace(Throwable thr) {
		security.acquireUninterruptibly();
		exep = thr;
		style = STYLE_STACKTRACE;
		runInBackground();
	}
	
	public void run() {
		if (style == STYLE_PRINTLN) System.out.println(text);
		else if (style == STYLE_PRINTF) System.out.print(text);
		else if (style == STYLE_STACKTRACE) exep.printStackTrace(System.err);
		security.release();
	}
	
	private void runInBackground() {
		if (isThisBackgroundThread() == false) {
			ExecutorService exec = Application.getInstance().backgroundExecutor();
			if (isExecutorReady(exec) == true) exec.execute(this);
			else run();
		} else {
			run();
		}
	}
	
	private boolean isThisBackgroundThread() {
		Thread current = Thread.currentThread();
		String cur_name = current.getName();
		String format = VSMThreadFactory.STR_NAME_PREFIX;
		if (cur_name.startsWith(format)) return true;
		else return false;
	}
	
	private boolean isExecutorReady(ExecutorService exec) {
		boolean isShutDown = exec.isShutdown();
		boolean isTerminated = exec.isTerminated();
		return !(isShutDown | isTerminated);
	}
	
}
