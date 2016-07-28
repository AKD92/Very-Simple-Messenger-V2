package net.vsmudp.gui.util;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.KeyboardFocusManager;
import javax.swing.*;

import net.vsmudp.Application;

public class GUIUtils {
	
	static final String STR_COPYMENU_LABEL,
	STR_CAPTUREMENU_LABEL;
	
	static {
		STR_COPYMENU_LABEL = "Copy text to clipboard";
		STR_CAPTUREMENU_LABEL = "Capture web addresses";
	}
	
	public static void sizeMenuProperly(JPopupMenu menu) {
		
		JMenuItem item = null;
		Dimension size = null;
		
		MenuElement[] menus = menu.getSubElements();
		
		for (MenuElement x : menus) {
			item = (JMenuItem) x;
			if (item != null) {
				size = item.getPreferredSize();
				size.height += 2;
				item.setPreferredSize(size);
			}
		}
	}
	
	public static void sizeMenuProperly(JMenu mnu) {
		sizeMenuProperly(mnu.getPopupMenu());
	}
	
	public static JPopupMenu createPopupForMessageList(JList msgList) {
		
		JPopupMenu listMenu;
		JMenuItem mntmCopy;
		JMenuItem mntmCaptureURL;
		
	    listMenu = new JPopupMenu();
	    mntmCopy = new JMenuItem(STR_COPYMENU_LABEL);
	    mntmCopy.addActionListener(new CopyTextHandler(msgList));
	    listMenu.add(mntmCopy);
	    
	    mntmCaptureURL = new JMenuItem(STR_CAPTUREMENU_LABEL);
	    mntmCaptureURL.addActionListener(new CaptureWebAddressHandler(msgList));
	    listMenu.add(mntmCaptureURL);
	    
	    listMenu.pack();
	    sizeMenuProperly(listMenu);
	    
	    msgList.addMouseListener(new MessageListPopupHandler(listMenu));
	    return listMenu;
	}
	
	private static boolean isOwnsShowing(Window[] all) {
		boolean res = false;
		for (Window x : all) {
			if (x.isShowing() == true) {
				res= true;
				break;
			}
		}
		return res;
	}
	
	// get the current active window
	private static Window getActiveWindow(Window start) {
		
		if (start.isShowing() == true) {
			Window[] owns = start.getOwnedWindows();
			boolean isThis = owns == null || owns.length == 0 || isOwnsShowing(owns) == false;
			if (isThis == true) return start;
			else {
				Window res = null;
				for (Window x : owns) {
					Window gres = getActiveWindow(x);
					if (gres != null) {
						res = gres;
						break;
					}
				}
				return res;
			}
		}
		else {
			return null;
		}
	}
	
	public static Window getActiveWindow() {
		KeyboardFocusManager kfg = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Window win = kfg.getActiveWindow();
		
		if (win == null) {
			win = (Window) Application.getInstance().getMainViewer();
			win = getActiveWindow(win);
		}
		return win;
	}

}
