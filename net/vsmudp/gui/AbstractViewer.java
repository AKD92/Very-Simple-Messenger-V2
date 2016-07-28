package net.vsmudp.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import net.vsmudp.Application;
import net.vsmudp.Printer;

public abstract class AbstractViewer implements Viewer {
	
	private String objName;
	
	private Window window;
	private JPanel content;
	private JMenuBar menuBar;
	
	private Action actionClose;
	private WindowListener winHandler;
	
	public int returnType;
	
	private int winType;
	
	private static final String STR_NOWIN,
	STR_NOCONTENT, STR_BADTYPE, STR_WINHANDLER_DESC,
	STR_CLOSEACTION_DESC, STR_OWNNAME;
	
	private static String STR_CLOSE_NAME, STR_CLOSE_DESC,
	STR_OWN_DESC_FORMAT;
	
	private static String PRINT_PATTERN;
	
	static {
		STR_OWNNAME = "Abstract Viewer";
		STR_NOWIN = "No container window instance is present, window has been destroyed";
		STR_NOCONTENT = "No container pane instance is present, contentPane is not set";
		STR_BADTYPE = "Bad dialog type is specified, specify Viewer.DLG_* constants";
		STR_CLOSE_NAME = "Close";
		STR_CLOSE_DESC = "Close this dialog box";
		STR_WINHANDLER_DESC = "Abstract Viewer Window Handler object, only invokes closeDialog() in windowClosing event";
		STR_CLOSEACTION_DESC = "Abstract Viewer Close Action object, only invokes closeDialog()";
		STR_OWN_DESC_FORMAT = "%s object, title: %s, isShowing: %b, isDestroyed: %b";
		PRINT_PATTERN = "Viewer (%s) : %s%n";
	}
	
	public AbstractViewer() {
		this (STR_OWNNAME);
	}
	
	public AbstractViewer(String name) {
		window = null;
		content = null;
		menuBar = null;
		winHandler = null;
		actionClose = null;
		objName = name;
		returnType = OPTION_VOID;
		winType = DLG_UNSPECIFIED;
	}
	
	public void enable() {
		setEnable(true);
	}
	public void disable() {
		setEnable(false);
	}
	
	protected void setReturnType(int n) {
		returnType = n;
	}
	protected int getReturnType() {
		return returnType;
	}
	
	protected void setEnable(boolean val) {
		
	}
	
	protected void println(String line) {
		Printer p = Application.getInstance().getPrinter();
		p.printf(PRINT_PATTERN, objName, line);
	}
	protected void println(Object obj) {
		println(obj.toString());
	}
	
	public String getName() {
		return objName;
	}
	
	public int getWindowType() {
		return winType;
	}
	public Window getWindow() {
		return window;
	}
	
	public String getTitle() {
		if (isDestroyed() == true) return null;
		else {
			String title = null;
			if (winType == DLG_JDIALOG) title = ((JDialog)window).getTitle();
			else if (winType == DLG_JFRAME) title = ((JFrame)window).getTitle();
			return title;
		}
	}
	
	public void setTitle(String title) {
		if (title == null) return;
		if (isDestroyed() == true) throw new UnsupportedOperationException(STR_NOWIN);
		if (winType == DLG_JDIALOG) ((JDialog)window).setTitle(title);
		else if (winType == DLG_JFRAME) ((JFrame)window).setTitle(title);
	}
	
	protected JPanel getContentPane() {
		return content;
	}
	protected void setContentPane(JPanel pnl) {
		content = pnl;
	}
	protected JMenuBar getMenuBar() {
		return menuBar;
	}
	protected void setMenuBar(JMenuBar bar) {
		menuBar = bar;
	}
	
	public boolean isShowing() {
		if (window == null) return false;
		else return window.isShowing();
	}
	
	public boolean isDestroyed() {
		if (window == null) return true;
		else return false;
	}
	
	public void refresh() {
		
	}
	
	public void closeDialog() {
		if (window != null) {
			window.dispose();
			window = null;
		}
	}
	
	protected Window packWithWindow(String title, int type, JButton defButton) {
		return packWithWindow(null, title, type, defButton);
	}
	
	protected Window packWithWindow(Window parent, String title, int type, JButton defButton) {
		return packWithWindow(parent, true, title, type, defButton);
	}
	
	protected Window packWithWindow(Window parent, boolean modal, String title, int type, JButton defButton) {
		if (content == null)
			throw new UnsupportedOperationException(STR_NOCONTENT);
		
		if (type != DLG_JFRAME && type != DLG_JDIALOG) 
			throw new IllegalArgumentException(STR_BADTYPE);
		
		winType = type;
		
		if (winType == DLG_JDIALOG) {
			JDialog dlg = null;
			if (parent instanceof Frame) {
				dlg = new JDialog((Frame)parent, title, modal);
			} else if (parent instanceof Dialog) {
				dlg = new JDialog((Dialog)parent, title, modal);
			} else {
				dlg = new JDialog();
				dlg.setTitle(title);
				dlg.setModal(modal);
			}
			dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			dlg.setContentPane(content);
			if (defButton != null) {
				dlg.getRootPane().setDefaultButton(defButton);
			}
			dlg.pack();
			window = dlg;
		}
		
		else if (winType == DLG_JFRAME) {
			JFrame dlg = new JFrame(title);
			dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			dlg.setContentPane(content);
			if (menuBar != null) {
				dlg.setJMenuBar(menuBar);
			}
			if (defButton != null) {
				dlg.getRootPane().setDefaultButton(defButton);
			}
			dlg.pack();
			window = dlg;
		}
		return window;
	}
	
	protected WindowListener getCloseWindowListener() {
		if (winHandler == null) winHandler = new WindowHandler();
		return winHandler;
	}
	protected Action getCloseAction() {
		if (actionClose == null) actionClose = new CloseAction();
		return actionClose;
	}
	
	public String toString() {
		String dlgName = getTitle();
		if (dlgName == null) dlgName = String.valueOf(dlgName);
		String unitName = String.valueOf(objName);
		String desc = String.format(
				STR_OWN_DESC_FORMAT, unitName, dlgName, isShowing(), isDestroyed());
		return desc;
	}
	
	private class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			closeDialog();
		}
		public String toString() {
			return STR_WINHANDLER_DESC;
		}
	}
	
	@SuppressWarnings("serial")
	private class CloseAction extends AbstractAction {
		CloseAction() {
			super.putValue(Action.NAME, STR_CLOSE_NAME);
			super.putValue(Action.SHORT_DESCRIPTION, STR_CLOSE_DESC);
		}
		public void actionPerformed(ActionEvent evt) {
			closeDialog();
		}
		public String toString() {
			return STR_CLOSEACTION_DESC;
		}
	}

}
