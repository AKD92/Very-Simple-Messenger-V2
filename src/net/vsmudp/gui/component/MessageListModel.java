package net.vsmudp.gui.component;

import javax.swing.AbstractListModel;
import java.util.*;

import net.vsmudp.engine.*;

@SuppressWarnings("serial")
public final class MessageListModel extends AbstractListModel {
	
	private List <Message> messageList;
	
	public MessageListModel() {
		this(new LinkedList <Message> ());
	}
	public MessageListModel(List <Message> list) {
		messageList = list;
	}
	
	public final int getSize() {
		return messageList.size();
	}
	
	public final Object getElementAt(int pos) {
		return messageList.get(pos);
	}
	
	public final void clearList() {
		int size = getSize();
		messageList.clear();
		fireContentsChanged(this, 0, size - 1);
	}
	
	public final void addElement(Message msg, int index) {
		messageList.add(index, msg);
		fireIntervalAdded(this, index, index);
	}
	
	public final boolean addElement(Message msg) {
		int size = getSize();
		boolean res = messageList.add(msg);
		fireIntervalAdded(this, size, size);
		return res;
	}
	
	public final Message removeElement(int index) {
		Message msg = messageList.remove(index);
		fireIntervalRemoved(this, index, index);
		return msg;
	}
	
	public final Message changeElementAt(Message msg, int index) {
		Message previous = messageList.set(index, msg);
		fireContentsChanged(this, index, index);
		return previous;
	}
	public final Message[] getAllElements() {
		return (Message[]) messageList.toArray();
	}
	public List<Message> getInternalList() {
		return messageList;
	}
	public void setMessageList(List<Message> list) {
		messageList.clear();
		messageList.addAll(list);
		fireContentsChanged(this, 0, list.size()-1);
	}
	
	public final void destroy() {
		messageList.clear();
		messageList = null;
	}
}
