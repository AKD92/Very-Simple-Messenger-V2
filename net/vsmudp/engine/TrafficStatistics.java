package net.vsmudp.engine;

import org.ashish.filesize.FileSize;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public final class TrafficStatistics {
	
	private long sentSignal;
	private long sentMessage;
	
	private long receivedSignal;
	private long receivedMessage;
	
	private final long totalSent;
	private final long totalReceived;
	
	private String sentSignal_S;
	private String sentMessage_S;
	
	private String receivedSignal_S;
	private String receivedMessage_S;
	
	private String totalSent_S;
	private String totalReceived_S;
	
	private boolean isPrintJustified;
	
	private JPanel previewPane;
	private Window containerWindow;
	
	public TrafficStatistics() {
		
		isPrintJustified = true;
		previewPane = null;
		containerWindow = null;
		
		sentSignal = Net.sentSignalSize;
		sentMessage = Net.sentMessageSize;
		
		receivedSignal = SignalReceiver.receivedSignalSize;
		receivedMessage = MessageReceiver.receivedMessageSize;
		
		totalSent = sentSignal + sentMessage;
		totalReceived = receivedSignal + receivedMessage;
		
		FileSize fs = FileSize.getSharedInstance();
		sentSignal_S = fs.byteCountToDisplaySize(sentSignal);
		sentMessage_S = fs.byteCountToDisplaySize(sentMessage);
		
		receivedSignal_S = fs.byteCountToDisplaySize(receivedSignal);
		receivedMessage_S = fs.byteCountToDisplaySize(receivedMessage);
		
		totalSent_S = fs.byteCountToDisplaySize(totalSent);
		totalReceived_S = fs.byteCountToDisplaySize(totalReceived);
	}
	
	public Object getSignalSent() {
		if (isPrintJustified == true) return sentSignal_S;
		else return sentSignal;
	}
	
	public Object getSignalReceived() {
		if (isPrintJustified == true) return receivedSignal_S;
		else return receivedSignal;
	}
	
	public Object getMessageSent() {
		if (isPrintJustified == true) return sentMessage_S;
		else return sentMessage;
	}
	
	public Object getMessageReceived() {
		if (isPrintJustified == true) return receivedMessage_S;
		else return receivedMessage;
	}
	
	public Object getTotalSent() {
		if (isPrintJustified == true) return totalSent_S;
		else return totalSent;
	}
	
	public Object getTotalReceived() {
		if (isPrintJustified == true) return totalReceived_S;
		else return totalReceived;
	}
	
	public void setJustified(boolean val) {
		isPrintJustified = val;
	}
	
	public final JPanel getPreviewPane() {
		return previewPane;
	}
	
	public final void setContainerWindow(Window window) {
		containerWindow = window;
	}
	public final void createPreviewPane(Window container_p) {
		
		Border internal = new EmptyBorder(5,5,5,5);
		
		previewPane = new JPanel(new GridLayout(3,0,5,5));
		previewPane.setBorder(internal);
		
		JPanel sentPanel = new JPanel(new GridBagLayout());
		Border titled = new TitledBorder("UP / SENT");
		Border compound = new CompoundBorder(titled, internal);
		sentPanel.setBorder(compound);
		
		JLabel lbl1 = new JLabel("Signals:");
		GridBagConstraints gc1 = new GridBagConstraints();
		gc1.gridx = gc1.gridy = 0;
		gc1.weightx = gc1.weighty = 1.0f;
		gc1.fill = GridBagConstraints.BOTH;
		sentPanel.add(lbl1, gc1);
		
		JLabel lbl3 = new JLabel("Messages:");
		gc1.gridy = 1;
		sentPanel.add(lbl3, gc1);
		
		JLabel lbl2 = new JLabel(getSignalSent().toString());
		lbl2.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gc2 = new GridBagConstraints();
		gc2.gridx = 1;
		gc2.gridy = 0;
		gc2.fill = GridBagConstraints.BOTH;
		gc2.weighty = 1.0f;
		sentPanel.add(lbl2, gc2);
		
		JLabel lbl4 = new JLabel(getMessageSent().toString());
		lbl4.setHorizontalAlignment(SwingConstants.RIGHT);
		gc2.gridy = gc2.gridx = 1;
		sentPanel.add(lbl4, gc2);
		
		previewPane.add(sentPanel);
		
		JPanel receivedPanel = new JPanel(new GridBagLayout());
		titled = new TitledBorder("DOWN / RECEIVED");
		compound = new CompoundBorder(titled, internal);
		receivedPanel.setBorder(compound);
		
		lbl1 = new JLabel("Signals:");
		gc1.gridx = gc1.gridy = 0;
		receivedPanel.add(lbl1, gc1);
		
		lbl2 = new JLabel(getSignalReceived().toString());
		lbl2.setHorizontalAlignment(SwingConstants.RIGHT);
		gc2.gridx = 1;
		gc2.gridy = 0;
		receivedPanel.add(lbl2, gc2);
		
		lbl3 = new JLabel("Messages:");
		gc1.gridx = 0;
		gc1.gridy = 1;
		receivedPanel.add(lbl3, gc1);
		
		lbl4 = new JLabel(getMessageReceived().toString());
		lbl4.setHorizontalAlignment(SwingConstants.RIGHT);
		gc2.gridx = gc2.gridy = 1;
		receivedPanel.add(lbl4, gc2);
		
		previewPane.add(receivedPanel);
		
		JPanel totalPanel = new JPanel(new GridBagLayout());
		titled = new TitledBorder("TOTAL TRAFFIC");
		compound = new CompoundBorder(titled, internal);
		totalPanel.setBorder(compound);
		
		lbl1 = new JLabel("Total up/sent:");
		gc1.gridx = gc1.gridy = 0;
		totalPanel.add(lbl1, gc1);
		
		lbl2 = new JLabel(getTotalSent().toString());
		lbl2.setHorizontalAlignment(SwingConstants.RIGHT);
		gc2.gridx = 1;
		gc2.gridy = 0;
		totalPanel.add(lbl2, gc2);
		
		lbl3 = new JLabel("Total down/received:");
		gc1.gridy = 1;
		totalPanel.add(lbl3, gc1);
		
		lbl4 = new JLabel(getTotalReceived().toString());
		lbl4.setHorizontalAlignment(SwingConstants.RIGHT);
		gc2.gridy = 1;
		totalPanel.add(lbl4, gc2);
		
		previewPane.add(totalPanel);
		
		KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		KeyStroke spaceKey = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false);
		Action closeAction = new CloseWindowAction();
		String cmd_closeWin = "closeWindow";
		
		InputMap imap = previewPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap amap = previewPane.getActionMap();
		
		imap.put(escKey, cmd_closeWin);
		imap.put(enterKey, cmd_closeWin);
		imap.put(spaceKey, cmd_closeWin);
		
		amap.put(cmd_closeWin, closeAction);
		
		setContainerWindow(container_p);
		
	}
	
	@SuppressWarnings("serial")
	private final class CloseWindowAction extends AbstractAction {
		public final void actionPerformed(ActionEvent evt) {
			if (containerWindow != null) {
				containerWindow.dispose();
			}
		}
	}
}
