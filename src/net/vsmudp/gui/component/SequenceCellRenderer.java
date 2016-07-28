package net.vsmudp.gui.component;

import java.awt.*;
import javax.swing.*;

import net.vsmudp.engine.sequence.Sequence;

@SuppressWarnings("serial")
public class SequenceCellRenderer extends SequencePanel implements ListCellRenderer {
	
	private boolean painted;
	
	public SequenceCellRenderer(Font f) {
		if (f != null) txtURL.setFont(f);
		painted = false;
	}
	public SequenceCellRenderer() {
		this(null);
	}

    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
    	
    	Sequence aes = (Sequence)value;
    	Color selColor = list.getSelectionBackground();
    	
    	setSequenceData(aes);
    	
    	if (isSelected == true) {
    		setColorAsSelected(selColor);
    	} else {
    		setColorAsNotSelected();
    	}
    	
    	if (painted == false) {
    		list.revalidate();
        	list.repaint();
        	painted = true;
    	}
    	
    	return this;
    }
}
