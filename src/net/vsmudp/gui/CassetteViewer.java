package net.vsmudp.gui;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.border.*;
import net.vsmudp.Application;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.Message;
import net.vsmudp.engine.record.Cassette;
import net.vsmudp.gui.cassette.CassetteReader;
import net.vsmudp.gui.cassette.CassetteWriter;
import net.vsmudp.gui.component.MessageCellRenderer;
import net.vsmudp.gui.component.MessageListModel;
import net.vsmudp.gui.util.GUIUtils;
import net.vsmudp.gui.handler.SliderValueChangeHandler;
import org.ashish.filesize.FileSize;

public class CassetteViewer extends AbstractViewer implements PropertyName {
	
	private JLabel lblIcon;
	private JLabel lblName, lblPath, lblSize;
	
	private JButton btnOpen, btnSave, btnSettings;
	private JButton btnClose;
	
	private JList lstMessage;
	private MessageListModel listModel;
	private MessageCellRenderer listRenderer;
	private JSlider cellSizeSlider;
	
	private JPanel content;
	private Cassette cassette;
	private File activeFile;
	
	private FileSize fileSize;
	private Application app;
	
	private static final String STR_TITLE_DEF,
	STR_NAME_DEF, STR_PATH_DEF, STR_LEN_DEF, STR_FRMT_TITLE, STR_FRMT_SIZE;
	
	private static final Dimension DIM_MINIMUM_SIZE;
	
	private static CassetteViewer instance;
	
	static {
		STR_TITLE_DEF = "Cassette Viewer";
		STR_FRMT_TITLE = "%s - %s";
		STR_NAME_DEF = "Unnamed File";
		STR_PATH_DEF = "Unknown path";
		STR_LEN_DEF = "undetermined";
		STR_FRMT_SIZE = "Size: %s";
		DIM_MINIMUM_SIZE = new Dimension(200, 300);
	}
	
	public static CassetteViewer getInstance() {
		if (instance == null)
			instance = new CassetteViewer();
		return instance;
	}
	
	public CassetteViewer() {
		
		app = Application.getInstance();
		fileSize = FileSize.getNewInstance();
		
		Map <String, String> map = fileSize.getStringMap();
		map.put(FileSize.KEY_KB, " kilobytes");
		map.put(FileSize.KEY_MB, " megabytes");
		map.put(FileSize.KEY_GB, " gigabytes");
		
		content = new JPanel(new GridBagLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints gc = new GridBagConstraints();
		
		Icon icn = new ImageIcon(
				app.getImage(PropertyName.IMG_FLOPPY_2_48));
		lblIcon = new JLabel(icn);
		gc.gridx = gc.gridy = 0;
		gc.gridheight = 3;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		content.add(lblIcon, gc);
		
		lblName = new JLabel("Name of the Cassette");
		lblName.setFont(app
				.getFont(PropertyName.FONT_MSG_TYPE_RENDERER));
		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.anchor = GridBagConstraints.LINE_END;
		content.add(lblName, gc);
		
		lblPath = new JLabel("G:\\Ashish Files\\documents");
		lblPath.setFont(app.getFont(PropertyName.FONT_SLIDER_LABEL));
		gc.gridy = 1;
		content.add(lblPath, gc);
		
		lblSize = new JLabel("Size: 4.91 kilobytes");
		lblSize.setFont(app.getFont(PropertyName.FONT_SLIDER_LABEL));
		gc.gridy = 2;
		content.add(lblSize, gc);
		
		JPanel pnl = new JPanel();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));
		
		btnSave = new JButton("Save");
		btnSave.setFocusPainted(false);
		btnSave.addActionListener(new SaveFileHandler());
		btnSave.setToolTipText("Save this cassette records to file for future reading");
		pnl.add(btnSave);
		pnl.add(Box.createHorizontalStrut(5));
		
		btnOpen = new JButton("Open");
		btnOpen.setFocusPainted(false);
		btnOpen.addActionListener(new OpenFileHandler());
		btnOpen.setToolTipText("Open another cassette file to view");
		pnl.add(btnOpen);
		pnl.add(Box.createHorizontalGlue());
		
		btnSettings = new JButton("Settings");
		btnSettings.setFocusPainted(false);
		btnSettings.addActionListener(new SettingsHandler());
		pnl.add(btnSettings);
		
		gc.gridx = 0;
		gc.gridy = 3;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		gc.insets = new Insets(25, 0, 5, 0);
		gc.weightx = 1.0f;
		content.add(pnl, gc);
		
		listModel = new MessageListModel();
		listRenderer = new MessageCellRenderer();
		
		lstMessage = new JList(listModel);
		lstMessage.setBorder(null);
		lstMessage.setCellRenderer(listRenderer);
		lstMessage.setFixedCellHeight(45);
		lstMessage.setFixedCellWidth(250);
		GUIUtils.createPopupForMessageList(lstMessage);
		
		JScrollPane scr = new JScrollPane(lstMessage);
		gc.gridx = 0;
		gc.gridy = 4;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.weightx = gc.weighty = 1.0f;
		gc.insets = new Insets(0, 0, 5, 0);
		content.add(scr, gc);
		
		cellSizeSlider = new JSlider(SwingConstants.HORIZONTAL, 40, 80, 45);
		cellSizeSlider.addChangeListener(new SliderValueChangeHandler(lstMessage, false));
		cellSizeSlider.setMajorTickSpacing(10);
		cellSizeSlider.setMinorTickSpacing(1);
		cellSizeSlider.setPaintTicks(true);
		cellSizeSlider.setPaintLabels(true);
		cellSizeSlider.setPaintTrack(false);
		cellSizeSlider.setFont(app
				.getFont(PropertyName.FONT_SLIDER_LABEL));
		Dimension size = new Dimension(cellSizeSlider.getPreferredSize());
		size.width += 200;
		cellSizeSlider.setPreferredSize(size);
		size = cellSizeSlider.getMinimumSize();
		size.width += 150;
		cellSizeSlider.setMinimumSize(size);
		gc.gridx = 0;
		gc.gridy = 5;
		gc.fill = GridBagConstraints.NONE;
		gc.gridwidth = 2;
		gc.weightx = 0.0f;
		gc.weighty = 0.0f;
		gc.insets = new Insets(0, 0, 0, 5);
		content.add(cellSizeSlider, gc);
		
		btnClose = new JButton("Close");
		btnClose.setFocusPainted(false);
		btnClose.addActionListener(getCloseAction());
		gc.gridx = 1;
		gc.gridy = 5;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.weightx = gc.weighty = 0.0f;
		gc.insets = new Insets(0, 0, 0, 0);
		content.add(btnClose, gc);
		
		setFileDetails(null);
		setContentPane(content);
	}
	
	public int showDialog(Window parent, Object... args) {
		return showDialog(parent);
	}
	
	public int showDialog(Window parent) {
		Window window = null;
		Properties config = app.getConfiguration();
		int type = DLG_UNSPECIFIED;
		
		if (parent != null)
			type = DLG_JDIALOG;
		else
			type = DLG_JFRAME;
		
		window = packWithWindow(parent, true, STR_TITLE_DEF, type, btnClose);
		window.addWindowListener(getCloseWindowListener());
		
		if (type == DLG_JDIALOG) {
			InputMap imap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap amap = content.getActionMap();
			String closecmd = "closeDlg";
			KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			imap.put(escKey, closecmd);
			amap.put(closecmd, getCloseAction());
		}
		
		if (type == DLG_JFRAME) {
			JFrame frm = (JFrame) window;
			frm.setIconImage(app.getImage(IMG_APPICON));
		}
		
		window.setMinimumSize(DIM_MINIMUM_SIZE);
		window.setSize(425, 522);
		window.setLocationRelativeTo(parent);
		
		if (activeFile != null) applyWindowTitle();
		
		// apply cell font size settings
		String cellFontSize = config.getProperty(CON_OPT_CHATMESSAGE_FONTSIZE);
		listRenderer.setMessageFontSize(Float.parseFloat(cellFontSize));
		
		boolean isFontBold = Boolean.parseBoolean(
				config.getProperty(CON_OPT_CHATMESSAGE_FONTBOLD));
		listRenderer.decideFontWillBoldOrPlain(isFontBold);
		
		String sliderVal = config.getProperty(CON_CV_SLIDERVALUE);
		cellSizeSlider.setValue(Integer.parseInt(sliderVal));
		
		window.setVisible(true);
		return getReturnType();
	}
	
	public void setFileDetails(File f) {
		activeFile = f;
		applyWindowTitle();
	}
	
	private void applyWindowTitle() {
		
		String name, path, len;
		String dlgtitle;
		if (activeFile == null) {
			name = STR_NAME_DEF;
			path = STR_PATH_DEF;
			len = String.format(STR_FRMT_SIZE, STR_LEN_DEF);
			dlgtitle = STR_TITLE_DEF;
		} else {
			name = String.valueOf(activeFile.getName());
			path = String.valueOf(activeFile.getParent());
			len = String.format(STR_FRMT_SIZE,
					fileSize.byteCountToDisplaySize(activeFile.length()));
			dlgtitle = String.format(STR_FRMT_TITLE, name, STR_TITLE_DEF);
		}
		lblName.setText(name);
		lblPath.setText(path);
		lblSize.setText(len);
		if (isDestroyed() == false) setTitle(dlgtitle);
	}
	
	public JList getMessageList() {
		return lstMessage;
	}
	
	public void refresh() {
		Properties config = app.getConfiguration();
		boolean isFontBold = Boolean.parseBoolean(
				config.getProperty(CON_OPT_CHATMESSAGE_FONTBOLD));
		listRenderer.decideFontWillBoldOrPlain(isFontBold);
		listRenderer.setMessageFontSize(Float.parseFloat(
				config.getProperty(CON_OPT_CHATMESSAGE_FONTSIZE)));
		JList lst = getMessageList();
		lst.revalidate();
		lst.repaint();
	}
	
	@Override
	public final void closeDialog() {
		
		Properties config = app.getConfiguration();
		String sliderVal = String.valueOf(cellSizeSlider.getValue());
		
		config.setProperty(CON_CV_SLIDERVALUE, sliderVal);
		activeFile = null;
		clear();
		
		super.closeDialog();
		
		if (getWindowType() == DLG_JFRAME) {
			app.close(false, 0);
		}
		
	}
	
	public void clear() {
		cassette = null;
		setFileDetails(null);
		
		List<Message> list = new ArrayList<Message>(1);
		listModel = new MessageListModel(list);
		lstMessage.setModel(listModel);
		lstMessage.ensureIndexIsVisible(0);
		
	}
	
	private class SettingsHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			OptionsViewer optView = OptionsViewer.getInstance();
			optView.showDialog(getWindow(), OptionsViewer.TAB_FONT, true);
		}
	}
	
	private final class SaveFileHandler implements ActionListener {
		CassetteWriter writer = null;
		
		public SaveFileHandler() {
			writer = CassetteWriter.getWriter();
		}
		
		public final void actionPerformed(ActionEvent evt) {
			writer.saveCassetteGUI(content, cassette, activeFile);
		}
	}
	
	private final class OpenFileHandler implements ActionListener {
		CassetteReader reader = null;
		
		public OpenFileHandler() {
			reader = CassetteReader.getReader();
		}
		
		public final void actionPerformed(ActionEvent evt) {
			Cassette tape = reader.readCassetteGUI(content, activeFile);
			if (tape != null) {
				lstMessage.clearSelection();
				setCassette(tape);
				setFileDetails(reader.getLastFileRead());
			}
		}
	}
	
	public final void setCassette(Cassette tape) {
		if (tape != null) {
			cassette = tape;
			
			List<Message> list = tape.getDataList();
			// MessageListModel oldOne = listModel;
			listModel = new MessageListModel(list);
			lstMessage.setModel(listModel);
			lstMessage.ensureIndexIsVisible(0);
			
			// if (destroyPrevious == true) oldOne.destroy();
		}
		
	}
	
}
