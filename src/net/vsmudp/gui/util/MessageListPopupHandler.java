package net.vsmudp.gui.util;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPopupMenu;

class MessageListPopupHandler extends MouseAdapter {
	
	private Point loc;
	private JPopupMenu menu;
	
	MessageListPopupHandler(JPopupMenu popup) {
		loc = new Point(-1, -1);
		menu = popup;
	}
	private void showPopup(MouseEvent evt) {
		
		JList list = (JList)evt.getComponent();
		int x = evt.getX();
		int y = evt.getY();
		
		int sel = list.getSelectedIndex();
		boolean isSel = sel == -1? false : true;
		
		loc.setLocation(x, y);
		int index = list.locationToIndex(loc);
		boolean isSame = index == sel? true : false;
		boolean isPopup = evt.isPopupTrigger() && isSel && isSame;
		
		if (isPopup == true) {
			menu.show(list, x, y);
		}
	}
	public void mousePressed(MouseEvent evt) {
		showPopup(evt);
	}
	public void mouseReleased(MouseEvent evt) {
		showPopup(evt);
	}
}
