package net.vsmudp;

import java.net.*;
import java.nio.charset.Charset;

import java.util.Properties;
import javax.swing.*;
import javax.swing.plaf.metal.*;

import java.awt.*;

import javax.sound.sampled.*;

import org.ashish.filesize.FileSize;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

import net.vsmudp.engine.*;
import net.vsmudp.engine.record.Cassette;
import net.vsmudp.engine.record.VRCDATA;
import net.vsmudp.gui.MainViewer;
import net.vsmudp.gui.themes.*;
import net.vsmudp.startup.*;

import java.io.*;

public final class Application {
	
	private MainViewer main;
	private Properties config;
	private final Charset CHARSET_DEFAULT;
	
	public final String CONFIG_FILE;
	public String VSM_HOME;
	public String LOG_FILE;
	
	private final String line_ter, flash_name, print_pattern;
	
	private JFileChooser filedlg;
	VsmTheme theme;
	
	// VERSION infos
	public final int VERSION_MAJ;
	public final int VERSION_MIN;
	public final int VERSION_REV;
	public final String VERSION;
	
	private ExecutorService execService;
	
	private PrintStream out;
	private Printer printer;
	
	public Map<String, Clip> audioMap;
	public Map<String, Image> imageMap;
	public Map<String, Font> fontMap;
	
	private static Application INSTANCE;
	
	static {
		INSTANCE = null;
	}
	
	private Application() {
		CONFIG_FILE = "/config.dat";
		LOG_FILE= "/program_log.txt";
		VSM_HOME = String.format("%s/.vsm.udp", System.getProperty("user.home"));
		CHARSET_DEFAULT = Charset.forName("UTF-8");
		filedlg = null;
		theme = null;
		config = null;
		out = null;
		line_ter = System.getProperty("line.separator");
		printer = new Printer();
		flash_name = "APP";
		print_pattern = "%s : %s%s";
		VERSION_MAJ = 1;
		VERSION_MIN = 3;
		VERSION_REV = 9;
		VERSION = String.format("%d.%d.%d", VERSION_MAJ, VERSION_MIN, VERSION_REV);
		audioMap = Collections.synchronizedMap(new HashMap <String, Clip> (15));
		imageMap = Collections.synchronizedMap(new HashMap <String, Image> (15));
		fontMap = Collections.synchronizedMap(new HashMap <String, Font> (15));
		System.setProperty("swing.metalTheme", "ocean");
		System.setProperty("vsm.home", VSM_HOME);
		System.setProperty("vsm.version", VERSION);
	}
	
	public static Application getInstance() {
		return INSTANCE;
	}
	
	public synchronized final void println(String line) {
		System.out.printf(print_pattern, flash_name, line, line_ter);
	}
	
	public Printer getPrinter() {
		return printer;
	}
	
	public final MainViewer getMainViewer() {
		return main;
	}
	public final void setMainViewer(MainViewer winMain) {
		main = winMain;
	}
	public final Properties getConfiguration() {
		return config;
	}
	public final Charset charset() {
		return CHARSET_DEFAULT;
	}
	public final Image getImage(String name) {
		return imageMap.get(name);
	}
	
	public final VsmTheme getApplicationTheme() {
		return theme;
	}
	public ExecutorService backgroundExecutor() {
		return execService;
	}
	
	public final Clip getAudio(String key) {
		return audioMap.get(key);
	}
	public Font getFont(String key) {
		return fontMap.get(key);
	}
	public void setFont(String key, Font val) {
		fontMap.put(key, val);
	}
	public final void setApplicationTheme(VsmTheme theme) {
		this.theme = theme;
		MetalTheme th = (MetalTheme) theme;
		MetalLookAndFeel.setCurrentTheme(th);
	}
	
	private void start(String[] args) {
		
		Thread.setDefaultUncaughtExceptionHandler(new ThreadExceptionHandler());
		setApplicationTheme(new VsmOceanTheme());
		
		// initialize background thread pool executor
		execService = Executors.newSingleThreadExecutor(new VSMThreadFactory());
//		execService = Executors.newFixedThreadPool(2, new VSMThreadFactory());
		
		makeSureFolderExist();
		divertProgramStreams();
		
		// initialize file chooser
		SwingUtilities.invokeLater(new InitFileChooser());
		Future<?> initFonts = execService.submit(new InitializeFonts());
		
		int argLength = args.length;
		if (argLength == 0) printMachineInfo();
		
		// first load essentials images
		LoadImagesTask imageTask = new LoadImagesTask(0);
		imageTask.setWhichShouldLoad(LoadImagesTask.IMG_ESSENTIALS);
		Future<?> initImages = execService.submit(imageTask);
		
		loadConfiguration();
		
		if (argLength > 0) {
			String fileName = args[0];
			
			// load the cassette in the background
			CassetteLoader casLoader = new CassetteLoader(fileName);
			Future<Cassette> task = execService.submit(casLoader);
			
			// start cassette viewer program
			Runnable r = new OpenRecordedCassette(task, fileName, initFonts, initImages);
			SwingUtilities.invokeLater(r);
		}
		else {
			
			// initialize NET
			Future <Net> initEngine = execService.submit(new InitEngine());
			
			//load rest of the image
			imageTask = new LoadImagesTask(LoadImagesTask.IMG_AUXILIARIES);
			execService.execute(imageTask);
			
			// start main window
			Runnable r = new StartMainProgram(initEngine, initFonts, initImages);
			SwingUtilities.invokeLater(r);
			
			// load audio clips
			execService.execute(new LoadClipsTask());
		}
		
		adjustFileSizingParameters();
	}
	
	public static void main(String args[]) {
		
		INSTANCE = new Application();
		INSTANCE.start(args);
	}
	
	public final void close(boolean emergencyShutdown, int exitcode) {
		
		println("Shutting down program");
		execService.shutdown();
		
		Net net = Net.getCurrent();
		if (net != null) net.dispose();
		
		flushAllClips();
		flushAllImages();
		
		makeSureFolderExist();
		saveConfiguration();
		
		println("Signals: " + Signal.sigs);
		println("VRCDATA: " + VRCDATA.instances());
		
		try {
			boolean isTerminated = execService.awaitTermination(10, TimeUnit.SECONDS);
			if (isTerminated == false) {
				println("ExecServices did not shutdown. Now applying force shutdown");
				execService.shutdownNow();
				isTerminated = execService.awaitTermination(5, TimeUnit.SECONDS);
			}
		} catch (InterruptedException ex) {
			println("EXECUTOR SHUTDOWN INTERRUPTED");
		}
		
		println("Program shutdown");
		
		closeProgramStreams();
		System.exit(exitcode);
	}
	
	public synchronized final void setStatusText(String text) {
		
		RunnableTextSetter textSetter = RunnableTextSetter.getInstance();
		textSetter.setTextData(text, RunnableTextSetter.LOC_STATUS_TEXT);
		
		if (SwingUtilities.isEventDispatchThread() == true) {
			textSetter.run();
//			println("directRun STATUS: " + Thread.currentThread().getName() + " " + text);
		}
		else {
			SwingUtilities.invokeLater(textSetter);
//			println("invokeLater STATUS: " + text);
		}
	}
	
	public synchronized final void setInfoText(String text) {
		
		RunnableTextSetter textSetter = RunnableTextSetter.getInstance();
		textSetter.setTextData(text, RunnableTextSetter.LOC_INFO_TEXT);
		
		if (SwingUtilities.isEventDispatchThread() == true) {
			textSetter.run();
//			println("directRun INFO: " + Thread.currentThread().getName() + " " + text);
		}
		else {
			SwingUtilities.invokeLater(textSetter);
//			println("invokeLater INFO: " + text);
		}
	}
	
	private final void flushAllClips() {
		Collection <Clip> c = audioMap.values();
		Iterator <Clip> t = c.iterator();
		Clip ad = null;
		int size = 0;
		while(t.hasNext() == true) {
			ad = t.next();
			if (ad != null) {
				ad.close();
				size++;
			}
		}
		audioMap.clear();
		c.clear();
		String msg = String.format("All clips closed, %d clips total", size);
		println(msg);
	}
	
	private final void flushAllImages() {
		Collection<Image> asCollection = imageMap.values();
		int size = 0;
		for (Image img : asCollection) {
			if (img != null) {
				img.flush();
				size++;
			}
		}
		imageMap.clear();
		asCollection.clear();
		String msg = String.format("All images flushed, %d images total", size);
		println(msg);
	}
	
	private final void loadConfiguration() {
		
		InputStream in = null;
		File file = new File(VSM_HOME + CONFIG_FILE);
		
		URL prop = Application.class.getResource("gui/res/defaults.properties");
		Properties defaults = new Properties();
		
		try {
			in = new BufferedInputStream( prop.openStream());
			defaults.load(in);
			config = new Properties(defaults);
			println("Default properties loaded successfully");
		} catch (Exception ex) {
			println("Could not load default properties");
			printer.printStackTrace(ex);
		} finally {
			closeStream(in);
		}
		
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			config.load(in);
		} catch (FileNotFoundException ex) {
			println("Configuration file not found. Using default configurations");
			printer.printStackTrace(ex);
		} catch (Exception ex) {
			printer.printStackTrace(ex);
		} finally {
			closeStream(in);
		}
		
	}
	
	private final void saveConfiguration() {
		
		OutputStream out = null;
		File file = new File(VSM_HOME + CONFIG_FILE);
		
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			config.store(out, "VSM UDP version "+ VERSION);
			println("Configurations saved to file");
		} catch (IOException ex) {
			println("Could not store application properties to file");
			printer.printStackTrace(ex);
		} finally {
			closeStream(out);
		}
	}
	
	private void makeSureFolderExist() {
		File folder = new File(VSM_HOME, "");
		if (folder.exists() == false) {
			folder.mkdir();
			println("Directory [program default] does not exist. Created one.");
		}
	}
	
	private void divertProgramStreams() {
		
		File f = new File(VSM_HOME + LOG_FILE);
		
		try {
			out = new PrintStream(
						new BufferedOutputStream (new FileOutputStream(f)), true);
			System.setOut(out);
			System.setErr(out);
		} catch (Exception ex) {
			printer.printStackTrace(ex);
		}
	}
	
	public void closeStream(Closeable in) {
		try {
			if (in != null) in.close();
		} catch (Exception ex) {
			printer.printStackTrace(ex);
		}
	}
	
	public JFileChooser getCassetteFileChooser() {
		return filedlg;
	}
	public void setCassetteFileChooser(JFileChooser cs) {
		filedlg = cs;
	}
	
	private void adjustFileSizingParameters() {
		FileSize fs = FileSize.getSharedInstance();
		Map <String, String> stringMap = fs.getStringMap();
		stringMap.put(FileSize.KEY_KB, " Kilo B");
		stringMap.put(FileSize.KEY_MB, " Mega B");
		stringMap.put(FileSize.KEY_GB, " Giga B");
	}
	
	private void closeProgramStreams() {
		
		try {
			if (out != null) out.close();
		} catch (Exception ex) {
			println("Cannot stop stdout stream");
			printer.printStackTrace(ex);
		}
	}
	
	void printMachineInfo() {
		
		// String line_sep = System.getProperty("line.separator");
		
		String os_name = System.getProperty("os.name");
		String os_ver = System.getProperty("os.version");
		String os_arch = System.getProperty("os.arch");
		
		String user_home = System.getProperty("user.home");
		String user_dir = System.getProperty("user.dir");
		String java_home = System.getProperty("java.home");
		
		String java_ver = System.getProperty("java.version");
		String java_name = System.getProperty("java.runtime.name");
		
		String vm_name = System.getProperty("java.vm.name");
		String vm_ver = System.getProperty("java.vm.version");
		
		System.out.println("Welcome to VSM (Very Simple Messenger) Program");
		System.out.printf("Version %s%n", VERSION);
		System.out.printf("Greetings from : Author (space.angles@gmail.com)%n%n");
		
		System.out.println("** Environment Details");
		System.out.printf("Runtime : \t\t\t\t%s v%s%n", java_name, java_ver);
		System.out.printf("VM : \t\t\t\t\t%s v%s%n", vm_name, vm_ver);
		System.out.printf("OS : \t\t\t\t\t%s (%s) v%s%n", os_name, os_arch, os_ver);
		
		System.out.println("User home path : \t\t\t" + user_home);
		System.out.println("Running directory : \t\t\t" + user_dir);
		System.out.println("JRE home path : \t\t\t" + java_home);
		System.out.println();
	}
	


}
