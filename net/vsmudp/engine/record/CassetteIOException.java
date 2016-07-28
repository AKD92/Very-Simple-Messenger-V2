package net.vsmudp.engine.record;

@SuppressWarnings("serial")
public abstract class CassetteIOException extends Exception {
	
	private String msg;
	
	public CassetteIOException(String m) {
		msg = m;
	}
	
	public String toString() {
		return msg;
	}

}
