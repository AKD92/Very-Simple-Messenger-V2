package net.vsmudp.engine.record;

@SuppressWarnings("serial")
public class WrongFormatException extends CassetteIOException {
	
	public WrongFormatException() {
		super("Not in correct format, header did not match");
	}

}
