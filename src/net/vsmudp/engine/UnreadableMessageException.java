package net.vsmudp.engine;

import java.io.IOException;

@SuppressWarnings("serial")
public class UnreadableMessageException extends IOException {
	int number;			// 1st byte number of received message
	public UnreadableMessageException(int num) {
		number = num;
	}

}
