package net.vsmudp;

import java.io.File;
import java.util.concurrent.Future;

import net.vsmudp.engine.record.Cassette;
import net.vsmudp.gui.CassetteViewer;
import net.vsmudp.gui.cassette.CassetteReader;

public class OpenRecordedCassette implements Runnable {
	
	private File cassetteFile;
	private Future<Cassette> cassetteLoadTask;
	
	private Future<?>[] otherTasks;
	private Application app;
	
	private String frmt_file, frmt_debug;
	
	public OpenRecordedCassette(Future<Cassette> task, String path, Future<?>... others) {
		cassetteFile = new File(path);
		cassetteLoadTask = task;
		otherTasks = others;
		app = Application.getInstance();
		frmt_file = "Running in file opening mode: %s";
		frmt_debug = "waitForTasks: %d ms, creationTime: %d ms";
	}
	
	public final void run() {
		
		CassetteReader reader = CassetteReader.getReader();
		Printer p = app.getPrinter();
		
		long time = System.currentTimeMillis();
		
		for (Future<?> task : otherTasks) {
			try {
				task.get();
			} catch (Exception ex) {
				Throwable th = ex.getCause();
				th.printStackTrace();
			}
		}
		
		time = System.currentTimeMillis() - time;
		
		long creationTime = System.currentTimeMillis();
		
		CassetteViewer csv = CassetteViewer.getInstance();
		creationTime = System.currentTimeMillis() - creationTime;
		
		String debugFile = String.format(frmt_file, cassetteFile.getAbsolutePath());
		String debugTime = String.format(frmt_debug, time, creationTime);
		
		app.println(debugFile);
		app.println(debugTime);
		
		Cassette tape = null;
		try {
			tape = cassetteLoadTask.get();
		} catch (Exception ex) {
			Exception cause = (Exception) ex.getCause();
			p.printStackTrace(cause);
			reader.showInputError(null, cassetteFile, cause);
			app.close(true, 2);
		}
		
		csv.setCassette(tape);
		csv.setFileDetails(cassetteFile);
		csv.showDialog(null);
	}
}
