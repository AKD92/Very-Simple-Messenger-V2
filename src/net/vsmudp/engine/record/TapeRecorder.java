package net.vsmudp.engine.record;

import javax.swing.*;
import static javax.swing.JOptionPane.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.util.*;
import java.awt.Component;
import java.awt.event.ActionEvent;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.engine.*;
import net.vsmudp.gui.component.MessageListModel;

public class TapeRecorder implements Recorder, ListDataListener, NetListener {
	
	private MessageListModel source;
	private boolean isRecording;
	
	private Action startAction;
	private Action stopAction;
	
	private Cassette tape;
	private Component parent;
	
	private static TapeRecorder recorder;
	private List <RecorderListener> listeners;
	
	static {
		recorder = null;
	}
	
	private TapeRecorder(Component par) {
		source = null;
		isRecording = false;
		
		parent = par;
		startAction = new StartAction();
		stopAction = new StopAction();
		
		startAction.setEnabled(false);
		stopAction.setEnabled(false);
		listeners = new LinkedList <RecorderListener> ();
		
		Net.addNetListener(this);
	}
	
	public static TapeRecorder initialize(MessageListModel source, Component parent) {
		recorder = new TapeRecorder(parent);
		recorder.source = source;
		return recorder;
	}
	
	public static TapeRecorder getRecorder() {
		return recorder;
	}
	
	public void loadANewCassette() {
		tape = new Cassette();
	}
	public Cassette getCassette() {
		return tape;
	}
	
	public boolean isRecording() {
		return isRecording;
	}
	public Action getStartAction() {
		return startAction;
	}
	public Action getStopAction() {
		return stopAction;
	}
	
	public void startRecording() throws RecorderException {
		Net net = Net.getCurrent();
		if (net == null) throw new NullPointerException("NET Object is null");
		
		if (tape == null) throw new NoCassetteIsAvailableException("No Cassette in slot");
		if (tape.getState() != Cassette.STATE_FRESH) throw new CassetteUsedException("This cassette is used & full");
		
		tape.allocateAllUsersDetails();
		tape.start();
		
		source.addListDataListener(this);
		isRecording = true;
		
		startAction.setEnabled(false);
		stopAction.setEnabled(true);
		
		fireRecordingStarted();
	}
	
	public void intervalAdded(ListDataEvent evt) {
		if (tape.isClosed() == false) {
			for (int i= evt.getIndex0(); i<= evt.getIndex1(); i++) {
				Message msg = (Message) source.getElementAt(i);
				tape.storeMessage(msg);
			}
		}
	}
	public void intervalRemoved(ListDataEvent evt) {
		
	}
	public void contentsChanged(ListDataEvent evt) {
		
	}
	
	public void stopRecording() {
		if (isRecording() == true) {
			source.removeListDataListener(this);
			tape.close();
			isRecording = false;
			
			startAction.setEnabled(true);
			stopAction.setEnabled(false);
			
			fireRecordingStopped();
		}
	}
	
	public void addRecorderListener(RecorderListener rl) {
		listeners.add(rl);
	}
	public void removeRecorderListener(RecorderListener rl) {
		listeners.remove(rl);
	}
	
	@SuppressWarnings("serial")
	private class StartAction extends AbstractAction {
		
		public StartAction() {
			putValue(NAME, "Start Recording");
			putValue(SHORT_DESCRIPTION, "Start a new recording on current conversation");
		}
		
		public void actionPerformed(ActionEvent evt) {
			try {
				startRecording();
			} catch (CassetteUsedException ex) {
				String msg = "There is a cassette that was previously recorded"
						+ "\nby the recorder. Would you like to use a new cassette?"
						+ "\nNote: Records made in previous cassette will be lost";
				int res = showOptionDialog(parent, msg, "Cassette Is Used", YES_NO_OPTION
						, QUESTION_MESSAGE, null, null, null);
				if (res == YES_OPTION) {
					loadANewCassette();
					try {
						startRecording();
					} catch (RecorderException e) {}
				} else {
					String msg2 = "Can not start recording. Cassette is full & user\n"
							+ "did not change cassette.";
					showMessageDialog(parent, msg2, "Cassette is full", ERROR_MESSAGE);
				}
			} catch (RecorderException ex) {
				showMessageDialog(null,"DDD");
				Printer pr = Application.getInstance().getPrinter();
				pr.printStackTrace(ex);
			}
		}
	}
	
	@SuppressWarnings("serial")
	public class StopAction extends AbstractAction {
		
		StopAction() {
			putValue(NAME, "Stop Recording");
		}
		
		public void actionPerformed(ActionEvent evt) {
			stopRecording();
		}
	}
	
	public void NetConnected() {
		startAction.setEnabled(true);
	}
	public void NetDisconnected() {
		
		if (isRecording == true) {
			stopRecording();
		}
		
		startAction.setEnabled(false);
		stopAction.setEnabled(false);
	}
	
	protected void fireRecordingStarted() {
		for (int i=0; i < listeners.size(); i++) {
			RecorderListener rl = listeners.get(i);
			rl.recordingStarted();
		}
	}
	
	protected void fireRecordingStopped() {
		for (int i=0; i < listeners.size(); i++) {
			RecorderListener rl = listeners.get(i);
			rl.recordingStopped();
		}
	}
	
}

