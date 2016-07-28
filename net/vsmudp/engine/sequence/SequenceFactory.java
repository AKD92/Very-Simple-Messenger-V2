package net.vsmudp.engine.sequence;

public class SequenceFactory {
	
	public static SequenceBuilder getNewWebAddressBuilder() {
		return new WebAddressBuilder();
	}
	public static SequenceBuilder getNewMailAddressBuilder() {
		return new MailAddressBuilder();
	}
	
}