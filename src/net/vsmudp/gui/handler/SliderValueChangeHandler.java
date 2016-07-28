package net.vsmudp.gui.handler;

import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderValueChangeHandler implements ChangeListener {
	
	int val;
	JSlider source;
	JList lstMessage;
	boolean updateInAdjusting;
	
	public SliderValueChangeHandler(JList lst, boolean update) {
		lstMessage = lst;
		updateInAdjusting = update;
		val = 0;
		source = null;
	}
	
	public final void stateChanged(ChangeEvent evt) {
		source = (JSlider) evt.getSource();
		boolean isAdjusting = source.getValueIsAdjusting();
		if (isAdjusting == true) {
			if (updateInAdjusting == true) update(source);
		} else {
			update(source);
		}
	}
	
	private void update(JSlider source) {
		val = source.getValue();
		lstMessage.setFixedCellHeight(val);
	}
}