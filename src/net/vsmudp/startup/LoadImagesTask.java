package net.vsmudp.startup;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import net.vsmudp.*;

public class LoadImagesTask implements Runnable, PropertyName {
	
	public static final int IMG_ESSENTIALS;
	public static final int IMG_AUXILIARIES;
	
	private int loadWhich;
	private String[] STR_ESSENTIALS;
	private String[] STR_AUXILIARIES;
	
	private Application app;
	
	public LoadImagesTask(int which) {
		loadWhich = which;
		STR_ESSENTIALS = new String[] {IMG_APPICON, IMG_FLOPPY_32, IMG_EMAIL_32
				, IMG_INTERNET_32, IMG_FLOPPY_2_48};
		STR_AUXILIARIES = new String[] {IMG_REQUEST_CONNECT_32,
				IMG_REQUEST_BUSY_32, IMG_REQUEST_UNTRUSTED_32, IMG_FLOPPY_2_32,
				IMG_BULB_32, IMG_SHUTDOWN_32};
		app = Application.getInstance();
	}
	
	static {
		IMG_ESSENTIALS = 100;
		IMG_AUXILIARIES = 200;
	}
	
	public final void run() {
		loadApplicationIcons();
	}
	
	private void loadApplicationIcons() {
			
		String[] names = null;
		if (loadWhich == IMG_ESSENTIALS) {
			names = STR_ESSENTIALS;
		} else if (loadWhich == IMG_AUXILIARIES) {
			names = STR_AUXILIARIES;
		} else {
			return;
		}
		
		String path_pattern = "gui/res/%s.png";
		String error_pattern = "Error in loading picture : %s";
		String path = null;
		int count = 0;
		
		URL url = null;
		Image img = null;
		boolean loaded = false;
		Map <String, Image> imageMap = app.imageMap;
		
		for (String name : names) {
			path = String.format(path_pattern, name);
			url = Application.class.getResource(path);
			
			try {
				img = loadResPicture(url);
				loaded = true;
			} catch (Exception ex) {
				app.println(String.format(error_pattern, name));
				loaded = false;
			}
			
			if (loaded == true) {
				imageMap.put(name, img);
				count++;
			}
		}
		app.println(String.format("Total loaded images: %d", count));
	}
	
	public BufferedImage loadResPicture(URL url) throws IOException {
		
		InputStream in = null;
		ImageInputStream iin = null;
		BufferedImage img= null;
		
		try {
			in = new BufferedInputStream(url.openStream());
			iin = ImageIO.createImageInputStream(in);
			img = ImageIO.read(iin);
			
		} finally {
			if (in != null) in.close();
		}
		
		return img;
		
	}
	
	public void setWhichShouldLoad(int which) {
		loadWhich = which;
	}

}
