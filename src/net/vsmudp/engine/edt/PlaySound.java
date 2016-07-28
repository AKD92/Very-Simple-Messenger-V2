package net.vsmudp.engine.edt;

import javax.sound.sampled.Clip;
import java.util.Properties;
import net.vsmudp.Application;
import net.vsmudp.PropertyName;

public class PlaySound implements Runnable {
	
	private Clip audio;
	private Properties config;
	
	// variables for intermediate counting
	private boolean toBePlayed;
	private String savedValue;
	private Application app;
	
	public static final String AUDIO_USER_BUSY;
	public static final String AUDIO_CONNECTION_BROKEN;
	public static final String AUDIO_REQUEST_ACCEPTED;
	public static final String AUDIO_REQUEST_DENIED;
	public static final String AUDIO_MSG_RECEIVED;
	public static final String AUDIO_ALERT_NEW_REQUEST;
	
	private static String V_TRUE;
	
	static {
		AUDIO_USER_BUSY = "user_busy";
		AUDIO_CONNECTION_BROKEN = "connection_broken";
		AUDIO_REQUEST_ACCEPTED = "request_accepted";
		AUDIO_REQUEST_DENIED = "request_denied";
		AUDIO_MSG_RECEIVED = "msg_received";
		AUDIO_ALERT_NEW_REQUEST = "alert_new_req";
		
		V_TRUE = "true";
	}
	
	public PlaySound(String audioName) {
		app = Application.getInstance();
		setAudio(audioName);
		config = app.getConfiguration();
		toBePlayed = false;
		savedValue = null;
	}
	public PlaySound() {
		this(null);
	}
	
	public final void setAudio(String name) {
		if (name == null) audio = null;
		else audio = app.getAudio(name);
	}
	
	public final void run() {
		savedValue = config.getProperty(PropertyName.CON_OPT_PLAYSOUND);
		toBePlayed = (audio != null) && savedValue.equalsIgnoreCase(V_TRUE);
		if (toBePlayed == true) {
			audio.setFramePosition(0);
			audio.start();
		}
	}
}
