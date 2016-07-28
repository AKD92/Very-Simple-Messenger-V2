package net.vsmudp.gui.util;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import net.vsmudp.engine.Message;
import net.vsmudp.gui.SequenceViewer;
import net.vsmudp.gui.component.MessageListModel;

class CaptureWebAddressHandler implements ActionListener {
	
	private SequenceViewer seqViewer;
	private StringBuilder inputText;
	private String dialogTitle;
	private JList source;
	
	CaptureWebAddressHandler(JList source) {
		seqViewer = SequenceViewer.getSequenceViewer();
		inputText = new StringBuilder(2000);
		dialogTitle = "Captured Web Address";
		this.source = source;
	}
	
	public void actionPerformed(ActionEvent evt) {
		MessageListModel model = (MessageListModel) source.getModel();
		List<Message> lst = model.getInternalList();
		for (Message x : lst) {
			inputText.append(x.getMessageBody());
			inputText.append('\n');
		}
		seqViewer.setData(inputText);
		inputText.setLength(0);
		
		Window win = SwingUtilities.getWindowAncestor(source);
		seqViewer.showDialog(win, dialogTitle);
	}
}
