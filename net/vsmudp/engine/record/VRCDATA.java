package net.vsmudp.engine.record;

import java.io.*;
import java.util.*;
import java.net.*;

import net.vsmudp.engine.*;

public class VRCDATA {
	
	HEADER header;
	long startTime;
	long endTime;
	String remoteUserName;
	String recorderUserName;
	
	NETADDRESS remoteAddress;
	NETADDRESS recorderAddress;
	
	int state;
	
	int totalMessages;
	List <Message> lstMessages;
	
	private static final String STR_DEFAULT_HEADER;
	private static final int INT_CURRENT_VERSION;
	
	private static final String DEF_NAME_VALUE;
	
	private static int ICOUNT;
	private static Queue<VRCDATA> structPool;
	
	static {
		STR_DEFAULT_HEADER = "VRCFILE (Recorded Cassette). Copyright (c) Ashish Kumar Das";;
		INT_CURRENT_VERSION = 3;
		DEF_NAME_VALUE = "Unnamed";
		structPool = new LinkedList <VRCDATA> ();
		ICOUNT = 0;
	}
	
	private VRCDATA() {
		header = new HEADER(STR_DEFAULT_HEADER, INT_CURRENT_VERSION);
		remoteAddress = new NETADDRESS(0, null, 0);
		recorderAddress = new NETADDRESS(0, null, 0);
		state = totalMessages = -1;
		lstMessages = new LinkedList <Message> ();
		startTime = endTime = 0;
		remoteUserName = recorderUserName = DEF_NAME_VALUE;
		ICOUNT++;
	}
	
	public void setDescription(String str) { header.desc = str; }
	public String getDescription() { return header.desc; }
	
	public int getCurrentVersion() {return INT_CURRENT_VERSION; }
	public int getFileVersion() {return header.version; }
	
	final class HEADER {
		String signature;
		int version;
		String desc;
		
		public HEADER(String sig, int ver) {
			signature = sig;
			version = ver;
			desc = null;
		}
		
		public void reset() {
			signature = STR_DEFAULT_HEADER;
			version = INT_CURRENT_VERSION;
			desc = null;
		}
		
		public final void writeHeader(DataOutput out) throws IOException {
			out.writeUTF(signature);
			out.writeInt(version);
			if (desc != null) {
				out.writeBoolean(true);
				out.writeUTF(desc);
			} else {
				out.writeBoolean(false);
			}
		}
		
		public final void readHeader(DataInput in) throws IOException, CassetteIOException {
			
			try {
				signature = in.readUTF();
			} catch (IOException ex) {
				throw new WrongFormatException();
			}
			
			if (signature.equals(STR_DEFAULT_HEADER) == false) {
				throw new WrongFormatException();
			}
			
			try {
				version = in.readInt();
			} catch (IOException ex) {
				throw new WrongFormatException();
			}
			
			if (version > INT_CURRENT_VERSION) {
				throw new UnsupportedVersionException(INT_CURRENT_VERSION, version);
			}
			
			boolean hasMore = in.readBoolean();
			if (hasMore == true) {
				desc = in.readUTF();
			}
		}
	}
	
	final class NETADDRESS {
		short addressLength;
		byte[] address;
		int port;
		
		public NETADDRESS(int len, byte[] data, int loc) {
			addressLength = (short) len;
			address = data;
			port = loc;
		}
		public void reset() {
			addressLength = 0;
			address = null;
			port = 0;
		}
		public final InetAddress getAddress() {
			InetAddress res = null;
			try {
				res = InetAddress.getByAddress(address);
			} catch (IOException ex) {}
			return res;
		}
		public final void readData(DataInput in) throws IOException {
			addressLength = in.readShort();
			address = new byte[addressLength];
			in.readFully(address);
			port = in.readInt();
		}
		public final void writeData(DataOutput out) throws IOException {
			out.writeShort(addressLength);
			out.write(address);
			out.writeInt(port);
		}
	}
	
	public synchronized final void writeToFile (File f) throws IOException {
		DataOutputStream dout = null;
		try {
			
			dout = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
			
			header.writeHeader(dout);
			
			dout.writeLong(startTime);
			dout.writeLong(endTime);
			dout.writeUTF(remoteUserName);
			dout.writeUTF(recorderUserName);
			
			remoteAddress.writeData(dout);
			recorderAddress.writeData(dout);
			
			dout.writeInt(state);
			dout.writeInt(totalMessages);
			
			for (Message x : lstMessages) {
				Message.writeMessageToStream(x, dout);
			}
			
		} finally {
			if (dout != null) {
				dout.flush();
				dout.close();
			}
		}
	}
	
	public synchronized final void readFromFile(File f) throws IOException, CassetteIOException {
		
		DataInputStream din = null;
		try {
			
			din = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
			
			header.readHeader(din);
			
			startTime = din.readLong();
			endTime = din.readLong();
			remoteUserName = din.readUTF();
			recorderUserName = din.readUTF();
			
			remoteAddress.readData(din);
			recorderAddress.readData(din);
			
			state = din.readInt();
			
			totalMessages = din.readInt();
			
			Message x = null;
			for (int i = 0; i < totalMessages; i++) {
				x = Message.readMessageFromStream(din);
				lstMessages.add(x);
			}
			
		}
		finally {
			if (din != null) din.close();
		}
		
	}
	
	public void reset() {
		header.reset();
		remoteAddress.reset();
		recorderAddress.reset();
		state = totalMessages = -1;
		lstMessages.clear();
		startTime = endTime = 0;
		remoteUserName = recorderUserName = DEF_NAME_VALUE;
	}
	
	public synchronized static VRCDATA getInstance() {
		VRCDATA res = null;
		res = structPool.poll();
		if (res == null) {
			res = new VRCDATA();
		}
		return res;
	}
	
	public synchronized static boolean returnInstance(VRCDATA file) {
		boolean res = false;
		if (file == null) {
			res = false;
		}
		else {
			file.reset();
			res = structPool.offer(file);
		}
		return res;
	}
	
	public static int instances() {
		return ICOUNT;
	}
	
}
