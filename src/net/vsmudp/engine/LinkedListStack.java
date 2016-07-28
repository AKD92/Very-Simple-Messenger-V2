package net.vsmudp.engine;

import java.util.LinkedList;

public class LinkedListStack<T> extends LinkedList<T> implements Stack<T> {
	
	private static final long serialVersionUID;
	
	static {
		serialVersionUID = 342442434532L;
	}
	
	public LinkedListStack() {
		super();
	}
	
	public boolean empty() {
		return isEmpty();
	}
	
	public void push(T element) {
		add(0, element);
	}
	
	public T pop() {
		return poll();
	}

}
