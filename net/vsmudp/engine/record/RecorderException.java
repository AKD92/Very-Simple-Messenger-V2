package net.vsmudp.engine.record;

@SuppressWarnings("serial")
public class RecorderException extends Exception {
	
	private String errmsg;
	
	public RecorderException (String details) {
		errmsg = details;
	}
	
	public String toString() {
		return errmsg;
	}
}
