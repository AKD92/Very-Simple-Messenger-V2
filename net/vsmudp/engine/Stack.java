package net.vsmudp.engine;

public interface Stack<T> {
	
	public boolean empty();
	
	public void push(T element);
	
	public T pop();
	
	public T peek();

}
