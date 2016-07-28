package net.vsmudp.engine.sequence;

// uniform sequence object describing every type of sequence in the library

public class Sequence {
	
	private String text;
	private int type;

	public static final int TYPE_WEB;
	public static final int TYPE_MAIL;
	public static final int TYPE_NONE;

	static {
		TYPE_WEB = 1;
		TYPE_MAIL = 3;
		TYPE_NONE = 0;
	}

	public Sequence(String add, int typ) {
		text = add;
		type = typ;
	}

	public Sequence() {
		this(null, TYPE_NONE);
	}

	public String getText() {
		return text;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean equals(Sequence seq) {
		if (type == seq.type && text.equals(seq.text)) return true;
		else return false;
	}
}
