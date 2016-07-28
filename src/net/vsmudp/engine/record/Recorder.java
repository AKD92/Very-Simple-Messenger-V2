package net.vsmudp.engine.record;

import javax.swing.*;

public interface Recorder {
	
	public Action getStartAction();
	public Action getStopAction();
	
	public boolean isRecording();
	public void startRecording() throws RecorderException;
	public void stopRecording();
	public Cassette getCassette() throws RecorderException;
	
	public void addRecorderListener(RecorderListener rl);
	public void removeRecorderListener(RecorderListener rl);
	
}
