package net.vsmudp;

import java.util.concurrent.ThreadFactory;

public class VSMThreadFactory implements ThreadFactory {
	
	private static int threadCount;
	private static final String nameFormat;
	public static final String STR_NAME_PREFIX;
	
	static {
		STR_NAME_PREFIX = "Executor Background Worker Thread";
		nameFormat = STR_NAME_PREFIX + " #%d";
		threadCount = 0;
	}
	
	public final Thread newThread(Runnable r) {
		threadCount++;
		String name = String.format(nameFormat, threadCount);
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		Thread t = new Thread(r, name);
		int priority = Math.min(Thread.NORM_PRIORITY, tg.getMaxPriority());
		t.setPriority(priority);
		return t;
	}

}
