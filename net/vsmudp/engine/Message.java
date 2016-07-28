package net.vsmudp.engine;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.nio.charset.Charset;

import javax.crypto.*;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.security.DataSecurity;

public final class Message {
	
	String body;
	int type;
	Date date; long time;
	
	public static final int MESSAGE_SENT_BY_ME;
	public static final int MESSAGE_RECEIVED;
	
	private static final StringBuilder STR_BUFFER;
	private static final char[] CH_BUFFER;
	private static EBAOutputStream B_OUT;
	
	public static final byte BYTE_MARK_PLAIN;
	public static final byte BYTE_MARK_ENCRYPTED;
	
	static {
		STR_BUFFER = new StringBuilder(600);
		CH_BUFFER = new char[10];
		MESSAGE_SENT_BY_ME = 5;
		MESSAGE_RECEIVED = 10;
		BYTE_MARK_PLAIN = (byte) 10;
		BYTE_MARK_ENCRYPTED = (byte) 11;
		B_OUT = new EBAOutputStream(null);
	}
	
	public Message(String body, int type) {
		this(body, type, System.currentTimeMillis());
	}
	public Message(String body) {
		this(body, 0);
	}
	
	public Message(String body, int type, long time) {
		this.body = body;
		this.type = type;
		this.date = null;
		this.time = time;
	}
	Message() {
		this(null, 0, 0);
	}
	
	public final String getMessageBody() {
		return body;
	}
	
	public final Date getDate() {
		if (date == null) {
			date = new Date(time);
		}
		return date;
	}
	
	public final long getTime() {
		return time;
	}
	
	public final int getMessageType() {
		return type;
	}
	
	public boolean equals(Object obj) {
		boolean res = false;
		if (!(obj instanceof Message)) res = false;
		else {
			Message msg = (Message) obj;
			if (body.equals(msg) == true && time == msg.time && type == msg.type) {
				res = true;
			} else {
				res = false;
			}
		}
		return res;
	}
	
	public synchronized static Message createMessage(byte[] data, int ofst, int len, Charset cset, int type)
			throws UnreadableMessageException, IOException {
		String message = null;
		Message msg = new Message();
			message = fromBytes(data, ofst, len, cset);
			msg.type = type;
		if (message == null) return null;
		else {
			msg.body = message;
			return msg;
		}
	}
	
	private static String fromBytes(byte[] data, int offset, int len, Charset charset) 
			throws IOException {
		InputStream in = null;
		InputStreamReader reader = null;
		byte encryptMark = 0;
		String msg = null;
		InputStream temp = null;
		
		Printer pr = Application.getInstance().getPrinter();
		
		DataSecurity sec = DataSecurity.getInstance();
		Cipher cipD = sec.getCipher(DataSecurity.CIPHER_FOR_DECRYPTION);
		
		in = new ByteArrayInputStream(data, offset, len);
		encryptMark = (byte) in.read();
		
		if (encryptMark == BYTE_MARK_PLAIN) {
			temp = in;
		} else if (encryptMark == BYTE_MARK_ENCRYPTED) {
			temp = new CipherInputStream(in, cipD);
		} else {
			pr.println("1st byte mark unknown: " + encryptMark);
			throw new UnreadableMessageException(encryptMark);
		}
		
		try {
			reader = new InputStreamReader(temp, charset);
			int readchars = 0;
			while(true) {
				readchars = reader.read(CH_BUFFER);
				if (readchars == -1) break;
				STR_BUFFER.append(CH_BUFFER, 0, readchars);
			}
			msg = STR_BUFFER.toString();
		} finally {
			reader.close();
			STR_BUFFER.setLength(0);
		}
		
		return msg;
	}
	
	@SuppressWarnings("resource")
	public synchronized static int toByteArray(String msg, Charset ch, byte[] out, int off) throws IOException {
		int writebytes = -1;
		Application app = Application.getInstance();
		Properties config = app.getConfiguration();
		OutputStreamWriter s_out = null;
		
		DataSecurity sec = DataSecurity.getInstance();
		Cipher cipE = sec.getCipher(DataSecurity.CIPHER_FOR_ENCRYPTION);
		
		B_OUT.setDestination(out, off, out.length);
		boolean toBeEncrypted = Boolean.parseBoolean(config.getProperty(PropertyName.CON_OPT_ENCRYPTMESSAGE));
		
		try {
			OutputStream temp = null;
			if (toBeEncrypted == true) {
				temp = new CipherOutputStream(B_OUT, cipE);
			} else {
				temp = B_OUT;
			}
			s_out = new OutputStreamWriter(temp, ch);
			
			// now write data
			byte encryptMark = toBeEncrypted == true? BYTE_MARK_ENCRYPTED : BYTE_MARK_PLAIN;
			B_OUT.write(encryptMark);
			
			s_out.write(msg);
			
		} finally {
			if (s_out != null) s_out.close();
			writebytes = B_OUT.size();
		}
		
		return writebytes;
	}
	
	public static void writeMessageToStream(Message msg, DataOutput out) throws IOException {
		out.writeInt(msg.type);
		out.writeLong(msg.time);
		out.writeUTF(msg.body);
	}
	
	public static Message readMessageFromStream(DataInput in) throws IOException {
		int m_type = in.readInt();
		long m_time = in.readLong();
		String m_body = in.readUTF();
		
		Message res = new Message(m_body, m_type, m_time);
		return res;
	}
	
}
