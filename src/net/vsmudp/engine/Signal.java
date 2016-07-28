package net.vsmudp.engine;

import java.io.*;
import java.util.*;

public final class Signal {
	
	private NetCommand cmd;
	private int port;
	private String userName;
	
	public static int sigs;
	
	private static EBAOutputStream B_OUT;
	private static DataOutputStream D_OUT;
	
	private static Queue<Signal> signalPool;
	
	static {
		signalPool = new LinkedList <Signal> ();
		for (int i=0; i < 5; i++) {
			signalPool.add(new Signal(null, 0, null));
		}
		B_OUT = new EBAOutputStream(null);
		D_OUT = new DataOutputStream(B_OUT);
	}
	
	private Signal(NetCommand cmd, int port, String user) {
		this.cmd = cmd;
		this.port = port;
		userName = user;
		sigs++;
	}
	
	public final NetCommand getNetCommand() {
		return cmd;
	}
	
	public final int getRemotePort() {
		return port;
	}
	
	public final String remoteUserName() {
		return userName;
	}
	
	public final boolean isUserNameAvailable() {
		if (userName == null ) return false;
		else return true;
	}
	
	public final void setNetCommand(NetCommand cmd) {
		this.cmd = cmd;
	}
	public final void setRemotePort(int port) {
		this.port = port;
	}
	public final void setRemoteUserName(String names) {
		userName = names;
	}
	public final void setData(NetCommand cmd, int port, String user) {
		setNetCommand(cmd);
		setRemotePort(port);
		setRemoteUserName(user);
	}
	
	public synchronized static int toByteArray(Signal signal, byte[] output, int off) throws IOException {
		
		int size = 0;
		B_OUT.setDestination(output, off, output.length);
		try {
			D_OUT.writeShort(signal.port);					// 2 byte short
			D_OUT.write(signal.cmd.getID());				// 1 byte data as byte
			
			if (signal.userName != null)					// x byte utf string
				D_OUT.writeUTF(signal.userName);			// total (2 + 1) + x = 3 + x bytes
															// very good size for udp transit
			D_OUT.flush();
		} finally {
			D_OUT.close();
			size = B_OUT.size();
		}
		return size;
	}
	
	public synchronized static Signal fromByteArray(byte[] data, int offset, int len)
	throws IOException {
		
		ByteArrayInputStream bin = null;
		DataInputStream din = null;
		
		Signal signal = null;
		
		try {
			bin = new ByteArrayInputStream(data, offset, len);
			din = new DataInputStream(bin);
			
			short port_short = din.readShort();
			short id_in = (short) din.read();
			String user_in = null;
			
			if (din.available() > 0) user_in = din.readUTF();
			
			int port_int = port_short < 0? port_short + 65536 : port_short;
			NetCommand command = NetCommand.parse(id_in);
			
			signal = Signal.getFreeSignal();
			signal.setData(command, port_int, user_in);
			
		} finally {
			if (din != null) din.close();
		}
		
		return signal;
	}
	
	public synchronized static Signal getFreeSignal() {
		Signal res = signalPool.poll();
		if (res == null)
			res = new Signal(null, 0, null);
		return res;
	}
	
	public synchronized static void returnSignal(Signal sig) {
		sig.cmd = null;
		sig.userName = null;
		sig.port = -1;
		signalPool.offer(sig);
	}
	
}
