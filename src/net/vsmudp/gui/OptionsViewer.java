package net.vsmudp.gui;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.util.Properties;
import javax.swing.border.*;
import static javax.swing.JOptionPane.*;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.Net;

public class OptionsViewer extends AbstractViewer implements PropertyName {
	
	private JTextField txtNameBox;
	private JCheckBox chkSendName;
	private JCheckBox chkFontBold;
	private JCheckBox chkInfoBoxVis;
	private JCheckBox chkHideClock;
	private JCheckBox chkPlayAudio;
	private JCheckBox chkEncryptMessage;
	
	private JComboBox cmbTextTyperFontSize;
	private JComboBox cmbMessageViewerFontSize;
	
	private JPanel pnlGeneral, pnlFont, pnlSecurity, pnlContent;
	private JTabbedPane tab;
	
	private JButton btnOk, btnCancel;
	private String strTitle;
	
	private Application app;
	
	private static Float[] FONT_SIZE_VALUES;
	public static final int TAB_GENERAL,
							TAB_FONT,
							TAB_DATA_SECURITY;
	
	private static final String TRUE, FALSE;
	private static OptionsViewer INSTANCE;
	
	static {
		FONT_SIZE_VALUES = new Float[10];
		for (int i=0; i < FONT_SIZE_VALUES.length; i++) {
			FONT_SIZE_VALUES[i]= (float)(i + 12);
		}
		TAB_GENERAL = 0;
		TAB_FONT = 1;
		TAB_DATA_SECURITY= 2;
		TRUE = "true";
		FALSE = "false";
	}
	
	public static OptionsViewer getInstance() {
		if (INSTANCE == null) INSTANCE = new OptionsViewer();
		return INSTANCE;
	}
	
	public OptionsViewer() {
		super("Options Viewer");
		app = Application.getInstance();
		Border inside = new EmptyBorder(7, 7, 7, 7);
		Insets insIn = new Insets(0, 0, 0, 0);
		GridBagConstraints gc = new GridBagConstraints();
		strTitle = "Program Options";
		
		pnlGeneral = new JPanel(new GridBagLayout());
		pnlGeneral.setBorder(inside);
		
		JLabel lblName = new JLabel("Name of the user (can be a fancy name)");
		insIn.set(0, 0, 0, 5);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.LINE_START;
		gc.insets = insIn;
		pnlGeneral.add(lblName, gc);
		
		txtNameBox = new JTextField(3);
		txtNameBox.setName("user name box");
		txtNameBox.setMargin(new Insets(5, 7, 5, 7));
		txtNameBox.setHorizontalAlignment(SwingConstants.CENTER);
		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.weightx = 1.0f;
		insIn.set(0, 0, 0, 0);
		pnlGeneral.add(txtNameBox, gc);
		
		chkSendName = new JCheckBox(
				"Send your name to the remote user when making a connection");
		chkSendName.setFocusPainted(false);
		insIn.set(5, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 1;
		gc.weightx = 0;
		gc.fill = GridBagConstraints.VERTICAL;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.LINE_START;
		pnlGeneral.add(chkSendName, gc);
		
		chkInfoBoxVis = new JCheckBox(
				"Information box of messaging window will be visible");
		chkInfoBoxVis.setFocusPainted(false);
		insIn.set(0, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 2;
		pnlGeneral.add(chkInfoBoxVis, gc);
		
		chkHideClock = new JCheckBox("Hide time clock of messaging window");
		chkHideClock.setFocusPainted(false);
		gc.gridy = 3;
		pnlGeneral.add(chkHideClock, gc);
		
		chkPlayAudio = new JCheckBox("Play audible beeps on various contexts");
		chkPlayAudio.setFocusPainted(false);
		gc.gridy = 4;
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = gc.weighty = 1.0f;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		pnlGeneral.add(chkPlayAudio, gc);
		
		pnlFont = new JPanel(new GridBagLayout());
		pnlFont.setBorder(inside);
		
		JLabel lblFontSizeRender = new JLabel("Font size of chat message viewer:");
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = gc.gridheight = 1;
		gc.fill = GridBagConstraints.VERTICAL;
		gc.anchor = GridBagConstraints.LINE_START;
		gc.weightx = gc.weighty = 0.0f;
		pnlFont.add(lblFontSizeRender, gc);
		
		cmbMessageViewerFontSize = new JComboBox(FONT_SIZE_VALUES);
		Dimension size = cmbMessageViewerFontSize.getPreferredSize();
		size.width += 40;
		size.height -= 2;
		insIn.set(2, 0, 0, 2);
		cmbMessageViewerFontSize.setPreferredSize(size);
		gc.gridx = 1;
		gc.anchor = GridBagConstraints.LINE_END;
		pnlFont.add(cmbMessageViewerFontSize, gc);
		
		JLabel lblFontSizeTyper = new JLabel("Font size of message text typer box:");
		gc.gridx = 0;
		gc.gridy = 1;
		insIn.set(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		pnlFont.add(lblFontSizeTyper, gc);
		
		cmbTextTyperFontSize = new JComboBox(FONT_SIZE_VALUES);
		cmbTextTyperFontSize.setPreferredSize(size);
		gc.gridx = 1;
		gc.gridy = 1;
		insIn.set(2, 0, 0, 2);
		gc.anchor = GridBagConstraints.LINE_END;
		pnlFont.add(cmbTextTyperFontSize, gc);
		
		chkFontBold = new JCheckBox("Font will be BOLD to display the messages");
		chkFontBold.setFocusPainted(false);
		insIn.set(5, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 2;
		gc.fill = GridBagConstraints.NONE;
		gc.weightx = gc.weighty = 1.0f;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		pnlFont.add(chkFontBold, gc);

		pnlSecurity = new JPanel(new GridBagLayout());
		pnlSecurity.setBorder(inside);
		
		chkEncryptMessage = new JCheckBox(
				"Encrypt text message before sending over the network");
		chkEncryptMessage.setFocusPainted(false);
		insIn.set(0, 0, 0, 0);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.weighty = gc.weightx = 1.0f;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		pnlSecurity.add(chkEncryptMessage, gc);
		
		pnlContent = new JPanel(new GridBagLayout());
		pnlContent.setBorder(new EmptyBorder(5, 7, 10, 7));
		
		tab = new JTabbedPane(SwingConstants.TOP);
		tab.addTab("General", pnlGeneral);
		tab.addTab("Font", pnlFont);
		tab.addTab("Data Security", pnlSecurity);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		gc.weightx = gc.weighty = 1.0f;
		gc.insets = new Insets(0, 0, 10, 0);
		pnlContent.add(tab, gc);
		
		btnOk = new JButton("OK");
		btnOk.setFocusPainted(false);
		btnOk.addActionListener(new OKHandler());
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.VERTICAL;
		gc.weighty = 0.0f;
		gc.weightx = 1.0f;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		pnlContent.add(btnOk, gc);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(getCloseAction());
		btnCancel.setFocusPainted(false);
		gc.gridx = 2;
		gc.gridy = 1;
		gc.weightx = 0;
		gc.insets = new Insets(0, 0, 0, 0);
		pnlContent.add(btnCancel, gc);
		
		lblName.setDisplayedMnemonic('N');
		lblName.setLabelFor(txtNameBox);
		lblFontSizeRender.setDisplayedMnemonic('c');
		lblFontSizeRender.setLabelFor(cmbMessageViewerFontSize);
		
		lblFontSizeTyper.setDisplayedMnemonic('t');
		lblFontSizeTyper.setLabelFor(cmbTextTyperFontSize);
		lblFontSizeTyper.setDisplayedMnemonicIndex(26);
		
		InputMap imap = pnlContent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap amap = pnlContent.getActionMap();
		
		KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		String closecmd = "closeDialog";
		
		imap.put(escKey, closecmd);
		amap.put(closecmd, getCloseAction());
		
		setContentPane(pnlContent);
	}
	
	private void setSelectedTab(int sel, boolean isOnlyEnabled) {
		
		tab.setSelectedIndex(sel);
		int tabcount = tab.getTabCount();
		
		if (isOnlyEnabled == false) {
			int c=0;
			while (c < tabcount) {
				if (tab.isEnabledAt(c) == false) tab.setEnabledAt(c, true);
				c++;
			}
		}
		else {
			int c = sel - 1;
			tab.setEnabledAt(sel, true);
			while (c >= 0) {
				if (tab.isEnabledAt(c) == true) tab.setEnabledAt(c, false);
				c--;
			}
			c = sel + 1;
			while ( c < tabcount) {
				if (tab.isEnabledAt(c) == true) tab.setEnabledAt(c, false);
				c++;
			}
		}
	}
	
	public int getSelectedTab() {
		return tab.getSelectedIndex();
	}
	
	public void showDialog(Window parent, int selTab) {
		showDialog(parent, selTab, false);
	}
	
	public int showDialog(Window parent, Object... args) {
		int selTab = (Integer)args[0];
		boolean isOnlyEnabled = (Boolean) args[1];
		return showDialog(parent, selTab, isOnlyEnabled);
	}
	
	public int showDialog(Window parent, int selTab, boolean isOnlyEnabled) {
		
		JDialog window =
				(JDialog) packWithWindow(parent, true, strTitle, DLG_JDIALOG, btnOk);
		window.addWindowListener(getCloseWindowListener());
		
		loadOptionsFromConfig(app.getConfiguration());
		setSelectedTab(selTab, isOnlyEnabled);
		configureControls();
		
		Dimension size = new Dimension(window.getPreferredSize());
		size.height += 80;
		window.setSize(size);
		
		window.setLocationRelativeTo(parent);
		window.setVisible(true);
		return getReturnType();
	}
	
	private void configureControls() {
		Net net = Net.getCurrent();
		if (net == null || net.isConnected() == true) disable();
	}
	
	protected void setEnable(boolean val) {
		txtNameBox.setEnabled(val);
	}
	
	public static final String valueOf(boolean val) {
		if (val == true) return TRUE;
		else return FALSE;
	}
	
	private final void loadOptionsFromConfig(Properties config) {
		
		String name = config.getProperty(CON_USER_NAME);
		if (name == null) name = System.getProperty(CON_USER_NAME);
		
		boolean isNameToBeSend = Boolean.parseBoolean(config.getProperty(CON_USER_SENDNAME));
		boolean isFontBoldForMsg = Boolean.parseBoolean(config.getProperty(CON_OPT_CHATMESSAGE_FONTBOLD));
		boolean isBigBoxVis = Boolean.parseBoolean(config.getProperty(CON_OPT_INFOTEXTVISIBLE));
		float textTyperFontSize = Float.parseFloat(config.getProperty(CON_OPT_TEXTTYPER_FONTSIZE));
		float chatMessageFontSize = Float.parseFloat(config.getProperty(CON_OPT_CHATMESSAGE_FONTSIZE));
		boolean isHideClock = Boolean.parseBoolean(config.getProperty(CON_OPT_HIDECLOCK));
		boolean isPlaySound = Boolean.parseBoolean(config.getProperty(CON_OPT_PLAYSOUND));
		boolean isEncrypted = Boolean.parseBoolean(config.getProperty(CON_OPT_ENCRYPTMESSAGE));
		
		this.txtNameBox.setText(name);
		this.chkSendName.setSelected(isNameToBeSend);
		this.chkFontBold.setSelected(isFontBoldForMsg);
		this.chkInfoBoxVis.setSelected(isBigBoxVis);
		this.cmbTextTyperFontSize.setSelectedItem(textTyperFontSize);
		this.cmbMessageViewerFontSize.setSelectedItem(chatMessageFontSize);
		this.chkHideClock.setSelected(isHideClock);
		this.chkPlayAudio.setSelected(isPlaySound);
		this.chkEncryptMessage.setSelected(isEncrypted);
	}
	
	private final void storeOptionsToConfig (Properties config) {
		
		// update configurations
		
		config.setProperty(CON_USER_NAME, txtNameBox.getText());
		config.setProperty(CON_USER_SENDNAME, valueOf(chkSendName.isSelected()));
		config.setProperty(CON_OPT_CHATMESSAGE_FONTBOLD, valueOf(chkFontBold.isSelected()));
		config.setProperty(CON_OPT_INFOTEXTVISIBLE, valueOf(chkInfoBoxVis.isSelected()));
		
		config.setProperty(CON_OPT_HIDECLOCK, valueOf(chkHideClock.isSelected()));
		
		float textTyperSize = (Float) this.cmbTextTyperFontSize.getSelectedItem();
		float chatMessageSize = (Float) this.cmbMessageViewerFontSize.getSelectedItem();
		config.setProperty(CON_OPT_TEXTTYPER_FONTSIZE, String.valueOf(textTyperSize));
		config.setProperty(CON_OPT_CHATMESSAGE_FONTSIZE, String.valueOf(chatMessageSize));
		config.setProperty(CON_OPT_PLAYSOUND, valueOf(chkPlayAudio.isSelected()));
		config.setProperty(CON_OPT_ENCRYPTMESSAGE, valueOf(chkEncryptMessage.isSelected()));
		
	}
	
	private Component findFaultyDataComponent() {
		Component res = null;
		String userName = txtNameBox.getText();
		if (userName.length() == 0) res = txtNameBox;
		return res;
	}
	
	private JPanel tabPaneForComponent(Component comp) {
		if (comp == null) return null;
		JPanel res = null;
		Container[] allCont = new Container[]{pnlGeneral, pnlFont, pnlSecurity};
		for (Container x : allCont) {
			if (x.isAncestorOf(comp) == true) {
				res = (JPanel) x;
				break;
			}
		}
		return res;
	}
	
	private final void applyConfigSettings(Properties config) {
		
		MainViewer main = app.getMainViewer();
		if (main != null) main.refresh();
		
		CassetteViewer casView = CassetteViewer.getInstance();
		if (casView != null) casView.refresh();
		
	}
	
	private class OKHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			
			Properties config = app.getConfiguration();
			Component faulty = findFaultyDataComponent();
			Window window = getWindow();
			
			if (faulty != null) {
				showMessageDialog(window, "Error in " + faulty.getName(), "Error", ERROR_MESSAGE);
				JPanel cont = tabPaneForComponent(faulty);
				tab.setSelectedComponent(cont);
				faulty.requestFocusInWindow();
				return;
			}
			
			storeOptionsToConfig(config);
			applyConfigSettings(config);
			closeDialog();
		}
	}
}
