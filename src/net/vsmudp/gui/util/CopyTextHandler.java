package net.vsmudp.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;
import net.vsmudp.engine.Message;
import net.vsmudp.gui.component.MessageListModel;

class CopyTextHandler implements ActionListener {
	
	private JList source;
	private RunnableTextCopier runCopier;
	
	CopyTextHandler(JList source) {
		this.source = source;
		runCopier = new RunnableTextCopier(null);
	}
	
	public void actionPerformed(ActionEvent evt) {
		
		MessageListModel model = (MessageListModel) source.getModel();
		int sel = source.getSelectedIndex();
		Message msg = (Message) model.getElementAt(sel);
		
		String bodyText = msg.getMessageBody();
		runCopier.setText(bodyText);
		runCopier.runInBackground();
	}
	
}
