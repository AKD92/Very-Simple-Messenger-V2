package net.vsmudp.gui;

import javax.swing.*;

import net.vsmudp.engine.*;
import net.vsmudp.gui.component.MessageCellRenderer;

public interface MainViewer extends Viewer{
	
	public Action getConnectAction();
	public Action getDisconnectAction();
	
	// Methods for changing List item heights of Message List
	public int getListCellHeight();
	public void setListCellHeight(int height);
	
	public JSplitPane getSplitPane();
	
	// Methods to get Message List (JList) and Cell Renderer it uses
	public JList getMessageList();
	public MessageCellRenderer getListCellRenderer();
	
	// Methods for adding and clearing all Messages from List
	public void addMessageToMessageList(Message msg);
	public void clearAllMessages();
	public Message[] getCurrentMessages();
	public int countMessages();
	
	// Methods for selecting (make it visible to user) a Message exist in the List
	public void selectMessageInList(int index);
	public void selectLastMessageInList();
	
	// Methods for accessing Time Clock
	public void startTimeClock();
	public void stopTimeClock();
	public boolean isTimeClockRunning();
	
	// Methods for accessing, getting or setting font size of Info Box (JLabel);
	public JLabel getInfoBoxLabel();
	public boolean isInfoBoxVisible();
	public void setInfoBoxVisible(boolean value);
	
	// Methods for accessing Text typer Box (JTextField)
	public JTextField getTextTyperBox();
	public float getTextTyperFontSize();
	public void setTextTyperFontSize(float size);
	
	public JLabel getStatusBarLabel();
	public JLabel getTimeViewLabel();
	
	// for accessing popup menu
	public JPopupMenu getListPopupMenu();

}
