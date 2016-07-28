package net.vsmudp.gui.component;

import javax.swing.ListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

import net.vsmudp.engine.*;

@SuppressWarnings("serial")
public final class MessageCellRenderer extends MsgPanel implements ListCellRenderer {
	
	//private static Color borderColor;
	private boolean painted;
	
	public MessageCellRenderer() {
		painted = false;
	}
	
	static {
		//borderColor = new Color(99, 130, 191);			// original border color
		//borderColor = new Color(79, 113, 183);		// modified border color (2nd color)
		//borderColor = new Color(86, 119, 186);		// modified border color (3rd color)
	}
	
	
	public final Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
    	
    	Message msg = (Message) value;
    	
    	setMessage(msg);
    	
    	if (isSelected == true) {
    		setColorAsSelected(list.getSelectionBackground());
    	}
    	else {
    		setColorAsNotSelected();
    	}
    	
    	if (painted == false) {
    		list.revalidate();
        	list.repaint();
        	painted = true;
    	}
    	
//    	if (cellHasFocus == true) attachBorder(borderColor);
//    	else removeBorder();
    	
    	return this;
    }
    
}
