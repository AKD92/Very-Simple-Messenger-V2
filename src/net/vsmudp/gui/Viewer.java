package net.vsmudp.gui;

import java.awt.Window;

public interface Viewer {
	
	public int DLG_JFRAME = 5;
	public int DLG_JDIALOG = 10;
	public int DLG_UNSPECIFIED = -1;
	
	public int OPTION_PASSED = 15;
	public int OPTION_CANCELLED = 16;
	public int OPTION_VOID = 0;
	public int OPTION_ERROR = -5;
	
	public void enable();
	public void disable();
	
	public String getName();
	
	public int getWindowType();
	public boolean isShowing();
	public boolean isDestroyed();
	
	public Window getWindow();
	public String getTitle();
	public void setTitle(String title);
	
	public void refresh();
	
	public int showDialog(Window parent, Object... args);
	public void closeDialog();

}
