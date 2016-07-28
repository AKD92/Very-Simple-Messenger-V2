package net.vsmudp.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.net.URL;

import java.io.*;
import java.nio.charset.Charset;

import javax.swing.*;
import javax.swing.border.*;

import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import static javax.swing.JOptionPane.*;

@SuppressWarnings("serial")
public final class TextViewer extends AbstractViewer {
	
	private JTextArea txtDocument;
	private JLabel lblAd;
	private JScrollPane scrollPane;
	
	private JButton btnOK;
	private JButton btnClose;
	
	private Action actionViewInfo;
	private JPanel content;
	
	private StringBuilder strBuffer;
	private Application app;
	
	private static TextViewer INSTANCE;
	private static String STR_DEF_TEXT;
	
	static {
		INSTANCE = null;
		STR_DEF_TEXT = "Text Viewer text goes here";
	}
	
	public TextViewer() {
		super("Text Data Viewer");
		app = Application.getInstance();
		
		strBuffer = new StringBuilder(3072);			// 3K memory
		actionViewInfo = new InfoViewAction();
		
		content = new JPanel(new GridBagLayout());
		content.setBorder(new EmptyBorder(30,10,10,10));
		GridBagConstraints gc = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		
		Font f_text = app.getFont(PropertyName.FONT_TEXT_VIEWER);
		Font f_ad = app.getFont(PropertyName.FONT_MSG_TYPE_RENDERER);
		
		txtDocument = new JTextArea();
		txtDocument.setWrapStyleWord(true);
		txtDocument.setLineWrap(true);
		txtDocument.setEditable(false);
		txtDocument.setText(STR_DEF_TEXT);
		
		txtDocument.setFont(f_text);
		scrollPane = new JScrollPane(txtDocument);
		scrollPane.setBorder(null);
		
		gc.gridx = 0; gc.gridy = 0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = gc.weighty = 1.0f;
		content.add(scrollPane,  gc);
		
		JPanel infoPanel = new JPanel();
		Dimension infoSize = new Dimension(20,40);
		infoPanel.setMinimumSize(infoSize);
		infoPanel.setPreferredSize(infoSize);
		
		infoPanel.setBackground(app.getApplicationTheme().getAdvertisementColor1());
		infoPanel.setBorder( BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		infoPanel.setLayout( new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));
		
		infoPanel.add(Box.createHorizontalGlue());
		lblAd = new JLabel("Advertisement");
		lblAd.setHorizontalAlignment(JLabel.CENTER);
		lblAd.setForeground(Color.white);
		lblAd.setOpaque(false);
		lblAd.setFont(f_ad);
		infoPanel.add(lblAd);
		infoPanel.add(Box.createHorizontalGlue());
		
		ins.set(10, 0, 0, 0);
		gc.gridx = 0; gc.gridy = 1;
		gc.weightx = 1.0; gc.weighty = 0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = ins;
		content.add(infoPanel, gc);
		
		btnClose = new JButton("Close");
		btnClose.addActionListener(getCloseAction());
		btnClose.setFocusPainted(false);
		
		btnOK = new JButton("OK");
		btnOK.setFocusPainted(false);
		
		btnOK.setPreferredSize(btnClose.getPreferredSize());
		btnOK.setMinimumSize(btnClose.getMinimumSize());
		
		// gc fot btnOK
		ins.set(10, 0, 0, 5);
		gc.gridx = 1; gc.gridy = 2;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LAST_LINE_END;
		gc.gridwidth = 1;
		gc.weightx = 1.0; gc.weighty = 0.0;
		content.add(btnOK, gc);
		
		// gc for buttonClose
		ins.set(10, 0, 0, 0);
		gc.gridx = 2; gc.gridy = 2;
		gc.weightx = gc.weighty = 0.0f;
		content.add(btnClose, gc);
		
		InputMap iMap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aMap = content.getActionMap();
		
		KeyStroke infoKey = KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_DOWN_MASK);
		iMap.put(infoKey, "ViewInfoDialog");
		aMap.put("ViewInfoDialog", actionViewInfo);
		
		KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		iMap.put(escKey, "closeDlg");
		aMap.put("closeDlg", getCloseAction());
		
		setTextBoxOpaque(false);
		setContentPane(content);
		
	}
	
	public void loadTextDataFrom(InputStream in) throws IOException {
		Charset charset = app.charset();
		
		Reader reader = new InputStreamReader(in, charset);
		strBuffer.setLength(0);
		
		char[] buffer = new char[100];
		int readchars = 0;
		
		while (true) {
			readchars = reader.read(buffer);
			if (readchars == -1) break;
			strBuffer.append(buffer, 0, readchars);
		}
		
		loadTextDataFrom(strBuffer.toString());
	}
	
	public void loadTextDataFrom(URL url) throws IOException {
		
		InputStream in = null;
		
		try {
			in = new BufferedInputStream(url.openStream());
			loadTextDataFrom(in);
		} finally {
			if (in != null) in.close();
		}
	}
	
	public Font getTextFont() {
		return txtDocument.getFont();
	}
	public void loadTextDataFrom(String text) {
		txtDocument.setText(text);
		txtDocument.setCaretPosition(1);
	}
	public void setAdvertisement(String text) {
		lblAd.setText(text);
	}
	public void setTextBoxOpaque(boolean val) {
		txtDocument.setOpaque(val);
	}
	
	public int showDialog(Window parent, Object... args) {
		
		String title = (String) args[0];
		boolean isModal = (Boolean) args[1];
		int width = (Integer) args[2];
		int height = (Integer) args[3];
		return showDialog(parent, title, isModal, width, height);
	}
	
	public int showDialog(Window parent, String title,
			boolean isModal, int width, int height) {
		Window window = packWithWindow(parent, isModal, title, DLG_JDIALOG, btnClose);
		window.addWindowListener(getCloseWindowListener());
		window.setSize(width, height);
		window.setLocationRelativeTo(parent);
		window.setVisible(true);
		return getReturnType();
	}
	
	public void showDialog(Window parent, String title) {
		this.showDialog(parent, title, false, 324, 430);
	}
	
	public static TextViewer getSharedInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TextViewer();
		}
		return INSTANCE;
	}
	
	private class InfoViewAction extends AbstractAction {
		
		private String str_first;
		private String str_notfound;
		private String str_title;
		public InfoViewAction() {
			str_first = "Please enter System Property Key";
			str_title = "Property Query";
			str_notfound = "Query key value not found";
			putValue(Action.NAME, str_title);
		}
		
		public void actionPerformed(ActionEvent evt) {
			
			Component parent = getWindow();
			
			String result = (String) showInputDialog(parent, str_first, str_title, QUESTION_MESSAGE, null, null, null);
			
			if (result != null) {
				
				result = System.getProperty(result);
				
				if (result != null) {
					showMessageDialog(parent, result, str_title, INFORMATION_MESSAGE);
				}
				else {
					showMessageDialog(parent, str_notfound, str_title, ERROR_MESSAGE);
				}
			}
		}
	}
	
}
