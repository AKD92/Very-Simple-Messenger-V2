package net.vsmudp.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;

import net.vsmudp.engine.sequence.*;
import net.vsmudp.gui.component.SequenceListModel;
import net.vsmudp.gui.component.SequenceCellRenderer;
import net.vsmudp.gui.handler.TextFieldFocusHandler;
import net.vsmudp.gui.util.RunnableTextCopier;

public class SequenceViewer extends AbstractViewer {
	
	protected JPanel content;
	protected JTextField txtSequence;
	protected JList lstSequence;
	protected JButton btnClose;
	protected JButton btnCopy;
	protected ListModel model;
	
	private Action actionCopy;
	private RunnableTextCopier runCopier;
	
	protected SequenceBuilder webBuilder;
	protected SequenceBuilder mailBuilder;
	protected List<Sequence> seqOutput;
	
	private static String STR_EMPTY;
	private static String STR_DIALOG_TITLE;
	private static String STR_CMD_CLOSE;
	
	private static SequenceViewer INSTANCE;
	
	static {
		STR_EMPTY = "";
		STR_DIALOG_TITLE = "%s (%d)";
		STR_CMD_CLOSE = "closeDlg";
		INSTANCE = null;
	}
	
	public SequenceViewer() {
		
		super("Sequence Viewer");
		
		actionCopy = new CopyAction();
		runCopier = new RunnableTextCopier(null);
		
		Insets ins = new Insets(-1,-1,-1,-1);
		content = new JPanel();
		content.setLayout(new GridBagLayout());
		ins.set(8,8,10,8);
		content.setBorder(new EmptyBorder(ins));
		
		GridBagConstraints gc = new GridBagConstraints();
		
		JLabel url = new JLabel("URL");
		url.setDisplayedMnemonic(KeyEvent.VK_U);
		url.setDisplayedMnemonicIndex(0);
		gc.gridx = gc.gridy = 0;
		gc.fill = GridBagConstraints.BOTH;
		content.add(url, gc);
		
		txtSequence = new JTextField(30);
		txtSequence.setMargin(new Insets(5,3,5,3));
		txtSequence.addFocusListener(new TextFieldFocusHandler());
		gc.gridx = 1; gc.gridy = 0;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		ins.set(0,10,0,0);
		gc.insets = ins;
		url.setLabelFor(txtSequence);
		content.add(txtSequence, gc);
		
		lstSequence = new JList();
		lstSequence.setBorder(null);
		lstSequence.setCellRenderer(new SequenceCellRenderer());
		lstSequence.setFixedCellHeight(50);
		lstSequence.setFixedCellWidth(200);
		lstSequence.addListSelectionListener(new ListSelectionHandler());
		JScrollPane scr = new JScrollPane(lstSequence);
		
		gc.gridx = 0; gc.gridy = 1;
		gc.weightx = gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		ins.set(8,0,8,0);
		gc.insets = ins;
		content.add(scr, gc);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		gc.gridx = 0; gc.gridy = 2;
		gc.weightx = 1.0; gc.weighty = 0.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridheight = 1; gc.gridwidth = GridBagConstraints.REMAINDER;
		ins.set(0, 0, 0, 0);
		gc.insets = ins;
		content.add(buttonPanel, gc);
		
		btnClose = new JButton("Close");
		btnClose.setFocusPainted(false);
		btnClose.addActionListener(getCloseAction());
		btnCopy = new JButton(actionCopy);
		btnCopy.setFocusPainted(false);
		btnCopy.setFocusable(false);
		
		buttonPanel.add(btnCopy);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(btnClose);
		
		InputMap imap = content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap amap = content.getActionMap();
		
		String escString = STR_CMD_CLOSE;
		KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		
		imap.put(escKey, escString);
		amap.put(escString, getCloseAction());
		
		imap = lstSequence.getInputMap(JComponent.WHEN_FOCUSED);
		amap = lstSequence.getActionMap();
		String copyCommand = "copyText";
		KeyStroke copyKey = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
		
		imap.put(copyKey, copyCommand);
		amap.put(copyCommand, new CopyAction());
		
		webBuilder = SequenceFactory.getNewWebAddressBuilder();
		mailBuilder = SequenceFactory.getNewMailAddressBuilder();
		
		seqOutput = new LinkedList<Sequence> ();
		setContentPane(content);
	}
	
	public void setData(CharSequence sequence) {
		
		mailBuilder.buildSequences(sequence);
		mailBuilder.transfer(seqOutput);
		
		webBuilder.buildSequences(mailBuilder.getUnusedChars());
		webBuilder.transfer(seqOutput);
		
		mailBuilder.reset();
		webBuilder.reset();
		
		Comparator<Sequence> comp = new SequenceComparator();
		Collections.sort(seqOutput, comp);
		
		model = new SequenceListModel(seqOutput);
		txtSequence.setText(STR_EMPTY);
		lstSequence.clearSelection();
		lstSequence.setModel(model);
		lstSequence.ensureIndexIsVisible(0);
	}
	
	public int showDialog(Window parent, Object... args) {
		String title = (String) args[0];
		return showDialog(parent, title);
	}
	
	public int showDialog(Window parent, String title) {
		
		if (model == null) {
			setReturnType(Viewer.OPTION_ERROR);
			return getReturnType();
		}
		
		int count = model.getSize();
		String dlg_title = String.format(STR_DIALOG_TITLE, title, count);
		
		if (count == 0) disable();
		else enable();
		
		Window window = packWithWindow(parent, true, dlg_title, DLG_JDIALOG, btnClose);
		
		window.addWindowListener(getCloseWindowListener());
		window.setSize(430,303);
		window.setLocationRelativeTo(parent);
		window.setVisible(true);
		return getReturnType();
	}
	
	public void closeDialog() {
		super.closeDialog();
		seqOutput.clear();
		setReturnType(Viewer.OPTION_CANCELLED);
	}
	
	protected void setEnable(boolean enabled) {
		this.txtSequence.setEnabled(enabled);
		this.lstSequence.setEnabled(enabled);
		this.actionCopy.setEnabled(enabled);
	}
	
	private void copyToClipboard(String toBeCopied) {
		if (toBeCopied == null) return;
		runCopier.setText(toBeCopied);
		runCopier.runInBackground();
	}
	
	@SuppressWarnings("serial")
	private class CopyAction extends AbstractAction {
		
		public CopyAction() {
			putValue(Action.NAME, "Copy to clipboard");
		}
		public void actionPerformed(ActionEvent evt) {
			JList list = lstSequence;
			int sel = list.getSelectedIndex();
			if (sel > -1) {
				SequenceListModel model = (SequenceListModel) list.getModel();
				Sequence adrs = (Sequence) model.getElementAt(sel);
				String toBeCopied = adrs.getText();
				copyToClipboard(toBeCopied);
			}
		}
	}
	
	private class ListSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent evt) {
			JList list = (JList) evt.getSource();
			if (evt.getValueIsAdjusting() == false) {
				int sel = list.getSelectedIndex();
				if (sel > -1) {
					Sequence adr = (Sequence) model.getElementAt(sel);
					String url = adr.getText();
					txtSequence.setText(url);
				}
			}
		}
	}
	
	public static SequenceViewer getSequenceViewer() {
		if (INSTANCE == null) {
			INSTANCE = new SequenceViewer();
		}
		return INSTANCE;
	}
}
