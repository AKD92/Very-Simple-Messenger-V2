package net.vsmudp.gui.handler;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

public class TextFieldFocusHandler extends FocusAdapter {
	
	JTextField txt = null;
	Object source = null;
	
	public final void focusGained(FocusEvent evt) {
		source = evt.getSource();
		if (source instanceof JTextField == false) return;
		txt = (JTextField) source;
		txt.selectAll();
	}
}
