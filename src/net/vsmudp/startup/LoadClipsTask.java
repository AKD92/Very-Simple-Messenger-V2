package net.vsmudp.startup;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

import net.vsmudp.*;

public class LoadClipsTask implements Runnable {
	
	private Application app;
	
	public LoadClipsTask() {
		app = Application.getInstance();
	}
	
	public void run() {
		loadAllClips();
	}
	
	private final void loadAllClips() {
		
		// THREAD_NAME all the aydio file without the .au extension. These clips then
		// will be add to the audioMap MAP with there names as key.
		
		String[] names = new String[] {"alert_new_req", "msg_received",
				"connection_broken", "request_accepted", "request_denied",
				"user_busy" };
		
		String path = "gui/res/%s.au";
		String clip_path = null;
		int count = 0;
		
		URL url = null;
		Clip c = null;
		boolean inserted = false;
		
		for (String name : names) {
			clip_path = String.format(path, name);
			url = Application.class.getResource(clip_path);
			
			try {
				c = createAudioClip(url);	// this method can throw exception
				inserted = true;
			} catch (Exception ex) {		// is safe from unmatured add
				app.println(ex.getMessage());
				inserted = false;
			}
			
			if (inserted == true) {
				app.audioMap.put(name, c);
				count++;
			}
		}
		app.println(String.format("Total loaded audios: %d", count));
	}
	
	private final Clip createAudioClip(URL url) throws IOException,
	LineUnavailableException, UnsupportedAudioFileException {
		InputStream in = null;
		AudioInputStream ain = null;
		Clip clip = null;
		
		try {
			in = new BufferedInputStream(url.openStream());
			ain = AudioSystem.getAudioInputStream(in);
			clip = AudioSystem.getClip();
			clip.open(ain);
			
		} finally {
			if (in != null) in.close();
			if (ain != null) ain.close();
		}
		return clip;
	}

}
