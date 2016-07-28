package net.vsmudp.gui;

import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.IPAddressUtil;
import net.vsmudp.gui.handler.TextFieldFocusHandler;

import java.awt.event.*;
import static javax.swing.JOptionPane.*;

public final class AddressMaker extends AbstractViewer {
	
	private InetAddress resultAddress;
	private String resultAddressTyped;
	
	private JLabel lblText1, lblText2;
	private JTextField txtAddress;
	private JLabel lblIcon;
	
	private JButton btnOk, btnClose;
	private JPanel content;
	
	private Application app;
	
	private static final String STR_ASK1, STR_ASK2, STR_TITLE;
	private static final String STR_FRMTERROR, STR_USERTYPE;
	private static String STR_SIDENOTE;
	
	private static AddressMaker INSTANCE;
	
	static {
		INSTANCE = null;
		STR_ASK1 = "Please enter IP address of the";
		STR_ASK2 = "machine to whom you want to connect";
		STR_TITLE = "Address of Foreign Computer";
		STR_USERTYPE = "User typed : %s";
		STR_FRMTERROR = "You typed invalid IP address. Please type a valid\nIPv4 address.";
		STR_SIDENOTE = "Connection request will be sent via UDP\nprotocol. Currently supports IPv4 only.";
	}
	
	public static AddressMaker getInstance() {
		if (INSTANCE == null) INSTANCE = new AddressMaker();
		return INSTANCE;
	}
	
	public AddressMaker() {
		super("Address Maker");
		app = Application.getInstance();
		resultAddress = null;
		Insets ins = new Insets(0,0,0,0);
		Font fontSmall = app.getFont(PropertyName.FONT_SLIDER_LABEL);
		
		JPanel center = new JPanel();
		center.setLayout(new GridBagLayout());
		center.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		lblText1 = new JLabel(STR_ASK1);
		lblText2 = new JLabel(STR_ASK2);
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 1; gc.gridy = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.LINE_START;
		center.add(lblText1, gc);
		
		ins.set(2, 0, 0, 0);
		gc.gridx = 1;
		gc.gridy = 1;
		gc.insets = ins;
		center.add(lblText2, gc);
		
		txtAddress = new JTextField("Hello Brother!");
		txtAddress.setMargin(new Insets(4, 5, 4, 5));
		txtAddress.addFocusListener(new TextFieldFocusHandler());
		ins.set(8, 0, 0, 0);
		gc.gridx = 1; gc.gridy = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = gc.weighty = 1.0f;
		gc.insets = ins;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		center.add(txtAddress, gc);
		
		lblIcon = new JLabel();
		// lblIcon.setBorder(new LineBorder(Color.BLACK));
		ins.set(3, 0, 0, 10);
		gc.gridx = 0; gc.gridy = 0;
		gc.gridwidth = 1; gc.gridheight = 3;
		gc.weightx = 0; gc.weighty = 0;
		gc.insets = ins;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.PAGE_START;
		center.add(lblIcon, gc);
		
		btnOk = new JButton("OK");
		btnClose = new JButton("Close");
		btnOk.addActionListener(new OKHandler());
		btnOk.setFocusPainted(false);
		btnClose.addActionListener(getCloseAction());
		btnClose.setFocusPainted(false);
		
		JTextArea lblToSend = new JTextArea(STR_SIDENOTE);
		lblToSend.setEditable(false);
		lblToSend.setFocusable(false);
		lblToSend.setOpaque(false);
		lblToSend.setLineWrap(true);
		lblToSend.setWrapStyleWord(true);
		lblToSend.setFont(fontSmall);
		
		// init gc for Text box information
		ins.set(0, 0, 0, 0);
		gc.gridx = 0; gc.gridy = 4;
		gc.gridwidth = 2; gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1.0f; gc.weighty = 0.0f;
		gc.anchor = GridBagConstraints.LAST_LINE_START;
		gc.insets = ins;
		center.add(lblToSend, gc);
		
		// init gc for OK button
		ins.set(0, 0, 0, 5);
		gc.gridx = 2; gc.gridy = 4;
		gc.gridwidth = gc.gridheight = 1;
		gc.weightx = gc.weighty = 0.0f;
		gc.anchor = GridBagConstraints.LAST_LINE_END;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = ins;
		center.add(btnOk, gc);
		
		// init gc for Close button
		ins.set(0, 0, 0, 0);
		gc.gridx = 3;
		gc.insets = ins;
		center.add(btnClose, gc);
		
		content = center;
		
		String closeCmd = "closeDialog";
		InputMap imap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap amap = content.getActionMap();
		
		KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		imap.put(escKey, closeCmd);
		amap.put(closeCmd, getCloseAction());
		
		setContentPane(content);
	}
	
	public void setEnable(boolean enable) {
		if (btnOk.isEnabled() != enable) btnOk.setEnabled(enable);
	}
	
	public int showDialog(Window parent, Icon icon, InetAddress initial) {
		String addr = null;
		if (initial != null) addr = initial.getHostAddress();
		return showDialog(parent, icon, addr);
	}
	
	public int showDialog(Window parent, Icon icon, String initial) {
		
		if (icon == null) {
			lblIcon.setVisible(false);
		} else {
			lblIcon.setVisible(true);
			lblIcon.setIcon(icon);
		}
		
		if (initial != null) {
			txtAddress.setText(initial);
		}
		
		JDialog window =
				(JDialog) packWithWindow(parent, true, STR_TITLE, DLG_JDIALOG, btnOk);
		window.addWindowListener(getCloseWindowListener());
		setEnable(true);
		
		Dimension min = new Dimension(window.getPreferredSize());
		window.setMinimumSize(min);
		window.setSize(385, 263);
		
		window.setLocationRelativeTo(parent);
		window.setVisible(true);
		
		return getReturnType();
	}
	
	public int showDialog(Window parent, Object... args) {
		Icon icon = (Icon) args[0];
		String initial = (String) args[1];
		return showDialog(parent, icon, initial);
	}
	
	public final InetAddress getAddress() {
		return resultAddress;
	}
	public final String getAddressAsTyped() {
		return resultAddressTyped;
	}
	
	public final void reset() {
		resultAddress = null;
		resultAddressTyped = null;
		setReturnType(OPTION_CANCELLED);
	}
	
	private class OKHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			
			byte[] data = null;
			String typed = txtAddress.getText();
			Window window = getWindow();
			
			data = IPAddressUtil.textToNumericFormatV4(typed);
			if (data == null) {
				showMessageDialog(window, STR_FRMTERROR, "Error", ERROR_MESSAGE);
				setReturnType(OPTION_CANCELLED);
				return;
			}
			
			setReturnType(OPTION_PASSED);
			resultAddressTyped = typed;
			try {
				resultAddress = InetAddress.getByAddress(data);
				
				String print = String.format(STR_USERTYPE, resultAddress.getHostAddress());
				println(print);
				
				closeDialog();
				
			} catch (UnknownHostException e) {
				println("Abnormal exception " + e);
			}
		}
	}
	
}
