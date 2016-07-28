package net.vsmudp.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.Properties;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.*;
import net.vsmudp.engine.record.*;
import net.vsmudp.gui.component.MessageCellRenderer;
import net.vsmudp.gui.component.MessageListModel;
import net.vsmudp.gui.handler.*;
import net.vsmudp.gui.util.GUIUtils;
import net.vsmudp.gui.vsmviewer.AboutAction;
import net.vsmudp.gui.vsmviewer.ConnectAction;
import net.vsmudp.gui.vsmviewer.DateTimeViewHandler;
import net.vsmudp.gui.vsmviewer.DisconnectAction;
import net.vsmudp.gui.vsmviewer.FocusInSendTextAction;
import net.vsmudp.gui.vsmviewer.KeyboardShortcutsAction;
import net.vsmudp.gui.vsmviewer.LoadNewCassetteListener;
import net.vsmudp.gui.vsmviewer.MyAddressViewHandler;
import net.vsmudp.gui.vsmviewer.SendDatagramAction;
import net.vsmudp.gui.vsmviewer.ShowOptionsAction;
import net.vsmudp.gui.vsmviewer.TrafficHandler;
import net.vsmudp.gui.vsmviewer.ViewExistingTapeAction;
import net.vsmudp.gui.vsmviewer.ViewLastRecordAction;
import static java.awt.event.KeyEvent.*;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class VSMViewer extends AbstractViewer implements MainViewer,
PropertyName, RecorderListener {
	
	protected JPanel contentPane;
	
	protected JButton btnSend;
	protected JButton btnDisconnect;
	protected JButton btnConnect;
	
	protected JList lstMessages;
	protected JPopupMenu popupForMsgList;
	protected JSplitPane splitPane;
	
	protected JTextField txtSendText;
	protected JLabel lblStatusBar;
	
	protected TapeRecorder recorder;
	
	protected MessageListModel listModel;
	protected MessageCellRenderer listRenderer;
	
	protected JMenu mnProgram;
	protected JMenu mnRecorder;
	protected JMenu mnHelp;
	
	protected JMenuItem mntmConnect;
	protected JMenuItem mntmDisconnect;
	protected JMenuItem mntmMyInformations;
	protected JMenuItem mntmStatistics;
	protected JMenuItem mntmExit;
	protected JMenuItem mntmOptions;
	
	protected JMenuItem mnStartRecord;
	protected JMenuItem mnStopRecord;
	protected JMenuItem mnViewLastRecord;
	protected JMenuItem mntmLoadNewCassette;
	protected JMenuItem mntmViewExistingTape;
	
	protected JMenuItem mntmKeyboardShortcuts;
	protected JMenuItem mntmAbout;
	
	protected JSlider cellSizeSlider;
	
	private Action actionFocusInSendText;
	private Action actionConnect;
	private Action actionDisconnect;
	private Action actionSendDatagram;
	private Action actionShowOptions;
	private Action actionAbout;
	private Action actionKeyboardShortcut;
	private Action actionViewLastRecord;
	private Action actionViewExistingCassette;
	
	protected JLabel lblInfoText;
	protected JLabel lblDateTime;
	
	protected Timer dateTimer;
	private Application app;
	
	private void initActions() {
		actionFocusInSendText = new FocusInSendTextAction();
		actionConnect = new ConnectAction();
		actionDisconnect = new DisconnectAction();
		actionSendDatagram = new SendDatagramAction();
		actionShowOptions = new ShowOptionsAction();
		actionKeyboardShortcut = new KeyboardShortcutsAction();
		actionAbout = new AboutAction();
		actionViewLastRecord = new ViewLastRecordAction();
		actionViewExistingCassette = new ViewExistingTapeAction();
	}
	
	public VSMViewer() {
		
		super("Very Simple Messenger");
	    app = Application.getInstance();
	    
	    initActions();       // initialize all action objects
	    
	    listModel = new MessageListModel();  // list model for use with our JList
	    listRenderer = new MessageCellRenderer();
	    
	    recorder = TapeRecorder.initialize(listModel, getWindow());
	    recorder.loadANewCassette();
	    
	    JMenuBar menuBar = new JMenuBar();  // set the menu bar
	    setMenuBar(menuBar);
	    
	    mnProgram = new JMenu("Program");	// start initializing menus & menu items
	    mnRecorder = new JMenu("Recorder");
	    mnHelp = new JMenu("Help");
	    menuBar.add(mnProgram);
	    menuBar.add(mnRecorder);
	    menuBar.add(mnHelp);
	    
	    mntmConnect = new JMenuItem(actionConnect);
	    mnProgram.add(mntmConnect);
	    
	    mntmDisconnect = new JMenuItem(actionDisconnect);
	    mnProgram.add(mntmDisconnect);
	    
	    mntmMyInformations = new JMenuItem("My Address");
	    mntmMyInformations.setAccelerator(KeyStroke.getKeyStroke(VK_F3, 0));
	    mntmMyInformations.addActionListener( new MyAddressViewHandler());
	    mnProgram.add(mntmMyInformations);
	    mnProgram.addSeparator();
	    
	    JMenu mntOptions= new JMenu("Options");
	    mnProgram.add(mntOptions);
	    
	    mntmOptions = new JMenuItem(actionShowOptions);
	    mntOptions.add(mntmOptions);
	    mnProgram.addSeparator();
	    
	    mntmStatistics = new JMenuItem("Traffic Statistics");
	    mntmStatistics.addActionListener(new TrafficHandler());
	    mnProgram.add(mntmStatistics);
	    
	    mntmExit = new JMenuItem("Quit");
	    mntmExit.setAccelerator(KeyStroke.getKeyStroke(VK_Q, ALT_DOWN_MASK, false));
	    mntmExit.addActionListener(getCloseAction());
	    mnProgram.add(mntmExit);
	    
	    mnStartRecord = new JMenuItem(recorder.getStartAction());
	    mnRecorder.add(mnStartRecord);
	    
	    mnStopRecord = new JMenuItem(recorder.getStopAction());
	    mnRecorder.add(mnStopRecord);
	    
	    mnRecorder.addSeparator();
	    
	    mntmLoadNewCassette = new JMenuItem("Insert New Cassette into Recorder");
	    mntmLoadNewCassette.addActionListener(new LoadNewCassetteListener());
	    mnRecorder.add(mntmLoadNewCassette);
	    
	    mnRecorder.addSeparator();
	    
	    mnViewLastRecord = new JMenuItem(actionViewLastRecord);
	    mnRecorder.add(mnViewLastRecord);
	    
	    mntmViewExistingTape = new JMenuItem(actionViewExistingCassette);
	    mnRecorder.add(mntmViewExistingTape);
	    
	    mntmKeyboardShortcuts = new JMenuItem(actionKeyboardShortcut);
	    mnHelp.add(mntmKeyboardShortcuts);
	    
	    mntmAbout = new JMenuItem(actionAbout);
	    mnHelp.add(mntmAbout);
	    
		GUIUtils.sizeMenuProperly(mnProgram);
		GUIUtils.sizeMenuProperly(mnRecorder);
		GUIUtils.sizeMenuProperly(mnHelp);
	    
	    // build a JSlider inside of a JPanel to view in a Popup Menu
	    cellSizeSlider = new JSlider(JSlider.HORIZONTAL, 40, 80, 45);
	    cellSizeSlider.setMajorTickSpacing(10);
	    cellSizeSlider.setMinorTickSpacing(2);
	    cellSizeSlider.setPaintTicks(true);
	    cellSizeSlider.setPaintLabels(true);
	    cellSizeSlider.setFont(app.getFont(PropertyName.FONT_SLIDER_LABEL));
	    
	    JLabel sliderLbl = new JLabel("Message Item size");
	    sliderLbl.setAlignmentX(0.5f);
	    
	    JPanel sliderPanel = new JPanel();
	    sliderPanel.setOpaque(true);
	    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
	    sliderPanel.add(sliderLbl);
	    sliderPanel.add(Box.createVerticalStrut(5));
	    sliderPanel.add(cellSizeSlider);
	    sliderPanel.setBorder(new EmptyBorder(5,7,5,7));
	    
	    //mntOptions.addSeparator();
	    mntOptions.add(sliderPanel);
	    GUIUtils.sizeMenuProperly(mntOptions);
	    
	    JPanel controls = new JPanel();
	    controls.setLayout(new FlowLayout(FlowLayout.CENTER, 5,5));
	    
	    btnConnect = new JButton(actionConnect);
	    btnConnect.setFocusPainted(false);
	    btnDisconnect = new JButton(actionDisconnect);
	    btnDisconnect.setFocusPainted(false);
	    
	    controls.add(btnConnect);
	    controls.add(btnDisconnect);
	    
	    btnConnect.setPreferredSize(btnDisconnect.getPreferredSize());	// hint to make sizes equal
	    
	    contentPane = new JPanel();
	    contentPane.setLayout(new GridBagLayout());
	    contentPane.setBorder(new EmptyBorder(5,10,5,10));	// top, left, bottom, right
	    
	    GridBagConstraints cons = new GridBagConstraints();
	    cons.gridx = 0;
	    cons.gridy = 0;
	    cons.gridwidth = 2;
	    cons.fill = GridBagConstraints.HORIZONTAL;
	    
	    contentPane.add(controls, cons);
	    
	    lstMessages = new JList(listModel);
	    lstMessages.setCellRenderer( listRenderer);
	    lstMessages.setFixedCellHeight(43);
	    lstMessages.setFixedCellWidth(300);
	    popupForMsgList = GUIUtils.createPopupForMessageList(lstMessages);
	    cellSizeSlider.addChangeListener(new SliderValueChangeHandler(lstMessages, true));
	    
	    JScrollPane list_scrollPane = new JScrollPane(lstMessages);
	    list_scrollPane.setBorder(null);
	    
	    txtSendText = new JTextField(10);
	    txtSendText.setBorder(new EmptyBorder(0,3,0,3));
	    txtSendText.setMinimumSize(new Dimension(100, 45));
	    txtSendText.addFocusListener(new TextFieldFocusHandler());
	    txtSendText.setFont(app.getFont(PropertyName.FONT_MSG_VIEW_EDITOR));
	    
	    InputMap iMap_f = txtSendText.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    InputMap iMap = txtSendText.getInputMap(JComponent.WHEN_FOCUSED);
	    ActionMap aMap = txtSendText.getActionMap();
	    
	    KeyStroke selectAllKStroke = KeyStroke.getKeyStroke(VK_R, CTRL_DOWN_MASK);
	    iMap_f.put(selectAllKStroke, "setFocus");							// when in a focused window, pressing Ctrl + R
	    aMap.put("setFocus", actionFocusInSendText);						// will cause SelectAllTexts action
	    
	    String cmdSend = "Send";
	    KeyStroke enterKStroke = KeyStroke.getKeyStroke(VK_ENTER, 0);		// when in focused, pressing ENTER
	    iMap.put(enterKStroke, cmdSend);									// will cause SendDatagram action
	    aMap.put(cmdSend, actionSendDatagram);
	    
	    recorder.addRecorderListener(this);
	    
	    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, list_scrollPane, txtSendText);
	    splitPane.setResizeWeight(1.0);
	    splitPane.setContinuousLayout(true);
	    splitPane.setOneTouchExpandable(true);
	    
	    cons = new GridBagConstraints();
	    cons.gridx = 0;
	    cons.gridy = 1;
	    cons.gridwidth = 2;
	    cons.fill = GridBagConstraints.BOTH;
	    cons.weightx = cons.weighty = 1;
	    cons.insets = new Insets(5,0,5,0);
	    
	    contentPane.add(splitPane, cons);
	    
	    lblInfoText = new JLabel("Welcome to VSM");
	    lblInfoText.setOpaque(true);
	    lblInfoText.setBorder( new EtchedBorder(EtchedBorder.LOWERED));
	    lblInfoText.setHorizontalAlignment(SwingConstants.CENTER);
	    lblInfoText.setVerticalAlignment(SwingConstants.CENTER);
	    lblInfoText.setBackground(app.getApplicationTheme().getAdvertisementColor2());
	    lblInfoText.setForeground(Color.white);
	    
	    Font f = lblInfoText.getFont();
	    float size = f.getSize() + 1;
	    f = f.deriveFont(Font.BOLD, size);
	    lblInfoText.setFont(f);
	    
	    cons = new GridBagConstraints();
	    cons.gridx = 0;
	    cons.gridy = 2;
	    cons.weightx = 1;
	    cons.insets = new Insets(0,0,0,5);
	    cons.fill = GridBagConstraints.BOTH;
	    
	    contentPane.add(lblInfoText, cons);
	    
	    lblStatusBar = new JLabel();
	    f = lblStatusBar.getFont();
	    
	    if (f.isPlain() == false){
	    	f = f.deriveFont(Font.PLAIN);
	    	lblStatusBar.setFont(f);
	    }
	    
	    cons = new GridBagConstraints();
	    cons.gridx = 0;
	    cons.gridy = 3;
	    cons.gridwidth = 1;
	    cons.weightx = 1;
	    cons.insets = new Insets(5,0,0,5);
	    cons.fill = GridBagConstraints.BOTH;
	    
	    contentPane.add(lblStatusBar, cons);
	    
	    btnSend = new JButton(actionSendDatagram);
	    btnSend.setFocusable(false);
	    cons = new GridBagConstraints();
	    cons.gridx = 1;
	    cons.gridy = 2;
	    cons.fill = GridBagConstraints.BOTH;
	    
	    contentPane.add(btnSend, cons);
	    
	    lblDateTime = new JLabel("Date & Time");
	    lblDateTime.setHorizontalAlignment(SwingConstants.CENTER);
	    lblDateTime.setFont(f);
	    lblDateTime.setVisible(false);
	    
	    cons = new GridBagConstraints();
	    cons.gridx = 1;
	    cons.gridy = 3;
	    cons.fill = GridBagConstraints.BOTH;
	    cons.insets = new Insets(5,0,0,0);
	    
	    contentPane.add(lblDateTime, cons);
	    
	    setContentPane(contentPane);
		
		dateTimer = new Timer(0, new DateTimeViewHandler());
		dateTimer.setInitialDelay(0);
		dateTimer.setDelay(1000);
		
	}
	
	public int showDialog(Window parent, Object... args) {
		
		String title = (String) args[0];
		
		// apply settings on various sectors
		Properties config = app.getConfiguration();
		
		boolean isInfoBoxVis = Boolean.parseBoolean(config.getProperty(CON_OPT_INFOTEXTVISIBLE));
		float textTyperFontSize = Float.parseFloat(config.getProperty(CON_OPT_TEXTTYPER_FONTSIZE));
		float chatMessageFontSize = Float.parseFloat(config.getProperty(CON_OPT_CHATMESSAGE_FONTSIZE));
		boolean hideClock = Boolean.parseBoolean(config.getProperty(CON_OPT_HIDECLOCK));
		boolean isFontBoldForMsg = Boolean.parseBoolean(config.getProperty(CON_OPT_CHATMESSAGE_FONTBOLD));
		int deviderLoc = Integer.parseInt(config.getProperty(CON_WIN_DEVIDER_LOCATION));
		
		setInfoBoxVisible(isInfoBoxVis);
		setTextTyperFontSize(textTyperFontSize);
		if (deviderLoc != -1) splitPane.setDividerLocation(deviderLoc);
		
		MessageCellRenderer rn = this.getListCellRenderer();
		rn.setMessageFontSize(chatMessageFontSize);
		rn.decideFontWillBoldOrPlain(isFontBoldForMsg);
		
		if (hideClock == false) startTimeClock();
		else stopTimeClock();
		
		// load window-related settings from config and apply
		int height = Integer.parseInt(config.getProperty(CON_WIN_HEIGHT));
		int width = Integer.parseInt(config.getProperty(CON_WIN_WIDTH));
		int x = Integer.parseInt(config.getProperty(CON_WIN_X));
		int y = Integer.parseInt(config.getProperty(CON_WIN_Y));
		int state = Integer.parseInt(config.getProperty(CON_WIN_STATE));
		int cellH = Integer.parseInt(config.getProperty(CON_WIN_CELL_HEIGHT));
		
		JFrame window = (JFrame) packWithWindow(title, DLG_JFRAME, null);
	    window.setMinimumSize(new Dimension(400, 400));
	    
	    // set app ICON
	    window.setIconImage(app.getImage(IMG_APPICON));
	    window.addWindowListener( getCloseWindowListener());
		
		window.setSize(width, height);
		window.setExtendedState(state);
		setListCellHeight(cellH);
		
		if (x == -1 || y == -1) {
			window.setLocationRelativeTo(null);		// centers the screen (default)
		} else {
			window.setLocation(x, y);
		}
		
		window.setVisible(true);
		return getReturnType();
	}
  	
	public int getListCellHeight() {
		return lstMessages.getFixedCellHeight();
	}
	public void setListCellHeight(int val) {
		cellSizeSlider.setValue(val);
		// this method call automatically updates lstMessage's list cell height
		// as cellSizeSlider's change event is being fired (see its ChangeListener below)
	}
	public JList getMessageList() {
		return lstMessages;
	}
	public void addMessageToMessageList(Message msg) {
		listModel.addElement(msg);
	}
	public void clearAllMessages() {
		listModel.clearList();
		this.lstMessages.clearSelection();
	}
	public Message[] getCurrentMessages() {
		return listModel.getAllElements();
	}
	public int countMessages() {
		return listModel.getSize();
	}
	public void selectMessageInList(int index) {
		lstMessages.setSelectedIndex(index);
		lstMessages.ensureIndexIsVisible(index);
	}
	public void selectLastMessageInList() {
		int indx = listModel.getSize() - 1;
		selectMessageInList(indx);
	}
	
	public MessageCellRenderer getListCellRenderer() {
		return listRenderer;
	}
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	public JLabel getInfoBoxLabel() {
		return lblInfoText;
	}
	public void setInfoBoxVisible(boolean val) {
		lblInfoText.setVisible(val);
	}
	public boolean isInfoBoxVisible() {
		return lblInfoText.isVisible();
	}
	public JTextField getTextTyperBox() {
		return txtSendText;
	}
	public float getTextTyperFontSize() {
		return txtSendText.getFont().getSize2D();
	}
	
	// change font size to size parameter
	public void setTextTyperFontSize(float size) {
		
		Font f = txtSendText.getFont();
		float current = f.getSize2D();
		
		if (current != size) {
			f = f.deriveFont(size);
			txtSendText.setFont(f);
			app.setFont(PropertyName.FONT_MSG_VIEW_EDITOR, f);
		}
	}
	
	// start timer clock and show date label
	public void startTimeClock() {
		if (dateTimer.isRunning() == false) {
			dateTimer.start();
			this.lblDateTime.setVisible(true);
		}
	}
	
	// stop timer clock and hide date label
	public void stopTimeClock() {
		
		if (dateTimer.isRunning() == true) {
			dateTimer.stop();
			this.lblDateTime.setVisible(false);
		}
	}
	
	// determine if timer clock is running
	public boolean isTimeClockRunning() {
		return dateTimer.isRunning();
	}
	
	// query the status bar label
	public JLabel getStatusBarLabel() {
		return lblStatusBar;
	}
	public JLabel getTimeViewLabel() {
		return lblDateTime;
	}
	public JPopupMenu getListPopupMenu() {
		return popupForMsgList;
	}
	
	public Action getConnectAction() {
		return actionConnect;
	}
	public Action getDisconnectAction() {
		return actionDisconnect;
	}
	
	// implementation of recorder listener interface
	public void recordingStarted() {
		this.mntmLoadNewCassette.setEnabled(false);
	}
	
	public void recordingStopped() {
		this.mntmLoadNewCassette.setEnabled(true);
		
		String msg = "Conversations has been recorded in the recorder" + 
		"\nGo to Recorder > View Last Record to view or save the cassette";
		Icon icon = new ImageIcon(app.getImage(IMG_BULB_32));
		
		showMessageDialog(getWindow(), msg, "Finished recording", PLAIN_MESSAGE, icon);
	}
	
	public void refresh() {
		Properties config = app.getConfiguration();
		
		boolean isFontBold = Boolean.parseBoolean(
				config.getProperty(CON_OPT_CHATMESSAGE_FONTBOLD));
		setInfoBoxVisible(Boolean.parseBoolean(
				config.getProperty(CON_OPT_INFOTEXTVISIBLE)));
		setTextTyperFontSize(Float.parseFloat(
				config.getProperty(CON_OPT_TEXTTYPER_FONTSIZE)));
		
		boolean hideClock = Boolean.parseBoolean(config.getProperty(CON_OPT_HIDECLOCK));
		if (hideClock == false) startTimeClock();
		else stopTimeClock();
		
		MessageCellRenderer rnd = getListCellRenderer();
		
		rnd.decideFontWillBoldOrPlain(isFontBold);
		rnd.setMessageFontSize(Float.parseFloat(
				config.getProperty(CON_OPT_CHATMESSAGE_FONTSIZE)));
		JList lst = getMessageList();
		lst.revalidate();
		lst.repaint();
	}
	
	public void closeDialog() {
		
		Net net = Net.getCurrent();
		Properties config = app.getConfiguration();
		JFrame win = (JFrame) getWindow();
		
//		if (emergencyShutdown == false) {
		boolean promtFor = net != null && net.isConnected() == true;
		
		if (promtFor == true) {
			Icon img = new ImageIcon(app.getImage(PropertyName.IMG_SHUTDOWN_32));
			String msg = "You are connected. Disconnect and quit?";
			int res = JOptionPane.showConfirmDialog((Component) win,
					msg, "Quit", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE, img);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
		}
//		}
		
		if (win != null) {
			win.setVisible(false);
			
			int state = win.getExtendedState();
			int cellHeight = getListCellHeight();
			int deviderLoc = getSplitPane().getDividerLocation();
			
			if (state != Frame.MAXIMIZED_BOTH ) {
				
				config.setProperty(PropertyName.CON_WIN_WIDTH, String.valueOf(win.getWidth())) ;
				config.setProperty(PropertyName.CON_WIN_HEIGHT, String.valueOf(win.getHeight()));
				config.setProperty(PropertyName.CON_WIN_X, String.valueOf(win.getX()));
				config.setProperty(PropertyName.CON_WIN_Y, String.valueOf(win.getY()));
			}
			
			config.setProperty(PropertyName.CON_WIN_STATE, String.valueOf(state));
			config.setProperty(PropertyName.CON_WIN_CELL_HEIGHT, String.valueOf(cellHeight));
			config.setProperty(PropertyName.CON_WIN_DEVIDER_LOCATION, String.valueOf(deviderLoc));
			
		}
		
		stopTimeClock();
		super.closeDialog();
		
		println(this);
		
		app.close(false, 0);
	}
	
}
