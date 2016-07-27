package net.vsmudp;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.vsmudp.engine.Net;
import net.vsmudp.gui.VSMViewer;

public class StartMainProgram implements Runnable {
	
	private Future<Net> netRes;
	private Future<?>[] guiDependentTasks;
	private String frmt_status;
	private String frmt_info;
	
	private Application app;
	
	public StartMainProgram(Future<Net> arg1, Future<?>... others) {
		netRes = arg1;
		guiDependentTasks = others;
		frmt_status = "Your address is %s, port %d";
		frmt_info =
		"Main GUI construction completed, waitForTasks: %d ms, creationTime: %d ms";
		app = Application.getInstance();
	}
	
	public final void run() {
		
		Net net= null;
		Printer p = app.getPrinter();
		
		long time = System.currentTimeMillis();
		
		for (Future<?> task : guiDependentTasks) {
			try {
				task.get();
			} catch (Exception ex) {
				Throwable th = ex.getCause();
				th.printStackTrace();
			}
		}
		
		time = System.currentTimeMillis() - time;
		long creationTime = System.currentTimeMillis();
		
		VSMViewer mainViewer = new VSMViewer();
		app.setMainViewer(mainViewer);
		creationTime = System.currentTimeMillis() - creationTime;
		
		String info = String.format(frmt_info, time, creationTime);
		app.println(info);
		
		try {
			net = netRes.get(10, TimeUnit.SECONDS);
			app.setStatusText(String.format(frmt_status
					, net.getLocalAddress().getHostAddress(), net.getLocalPort()));
		} catch (Exception e) {
			Throwable t = e.getCause();
			String error = "No access to network (NET initialization failed)";
			app.setStatusText(error);
			p.printStackTrace(t);
		}
		mainViewer.showDialog(null, "Very Simple Messenger");
	}

}
