package net.vsmudp;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.*;

public class ThreadExceptionHandler implements UncaughtExceptionHandler {
	
	private String errorFormat;
	private String headerFormat;
	private String dash;
	private List <String> tNames;
	
	private Application app;
	
	public ThreadExceptionHandler() {
		app = Application.getInstance();
		dash = "---------------------------------------------------------";
		errorFormat = "\tException details: %s";
		headerFormat = "\tThread details : Name (%s) Group (%s) ID (%d) Priority (%d) State (%s)";
		tNames = new ArrayList <String> (10);
		tNames.add("awt");
		tNames.add("swing");
		tNames.add("eventqueue");
		tNames.add("event");
		tNames.add("receiver");
	}
	
	public void uncaughtException(Thread t, Throwable thr) {
		
		
		ThreadGroup group = t.getThreadGroup();
		String rawMessage = thr.getMessage();
		String error = null;
		
		if (rawMessage != null) {
			error = String.format(errorFormat, rawMessage);
		}
		else {
			error = "No error message in the throwable";
		}
		
		String header = String.format(headerFormat, t.getName(), group.getName(),
				t.getId(), t.getPriority(), t.getState().name());
		
		String total = String.format("%s%n%s", header, error);
		
		String name = t.getName().toLowerCase();
		String matchName = matchesToCriticalThreads(name);
		
		System.out.println(dash);
		if (matchName != null) {
			System.out.printf("ERROR%n\tprogram will shutdown now, matched to %s%n", matchName);
		}
		
		System.out.println(total);
		System.out.println("\tPRINTING STACK TRACE ELEMENTS-------------------");
		thr.printStackTrace();
		
		if (matchName != null) {
			app.close(true, 5);			// emergency shutdown (no prompt for disconnect)
		}										// if the user is connected with somebody
	}
	
	private String matchesToCriticalThreads(String targetName) {
		String res = null;
		for (String x : tNames) {
			if (targetName.contains(x)) {
				res = x;
				break;
			}
		}
		return res;
	}

}
