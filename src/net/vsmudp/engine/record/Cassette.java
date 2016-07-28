package net.vsmudp.engine.record;

import java.util.*;
import java.io.*;
import java.net.InetAddress;

import net.vsmudp.Application;
import net.vsmudp.engine.*;

public final class Cassette implements Serializable {
	
	List<Message> aList;
	Date startDate;
	Date stopDate;
	
	String remoteUserName;
	InetAddress remoteAddress;
	int remotePort;
	
	String recorderUserName;
	InetAddress recorderAddress;
	int recorderPort;
	
	int state;
	
	public static final long serialVersionUID;
	public static final int STATE_FRESH, STATE_RUNNING, STATE_CLOSED;
	
	static {
		STATE_FRESH = 0;
		STATE_RUNNING = 5;
		STATE_CLOSED = 10;
		serialVersionUID = 32002995959L;
	}
	
	public Cassette() {
		aList = new LinkedList<Message> ();
		startDate = null;
		stopDate = null;
		remoteUserName = null;
		remoteAddress = null;
		recorderUserName = null;
		recorderAddress = null;
		state = STATE_FRESH;
		remotePort = 0;
		recorderPort = 0;
	}
	
	Cassette(long startD, long endD, String remUserName,
			String recUserName, InetAddress remAddr, InetAddress recAddr, int remPort,
			int recPort, int state, List <Message> list) {
		aList = list;
		startDate = new Date(startD);
		stopDate = new Date(endD);
		remoteUserName = remUserName;
		recorderUserName = recUserName;
		remoteAddress = remAddr;
		recorderAddress = recAddr;
		remotePort = remPort;
		recorderPort = recPort;
		this.state = state;
		
	}
	
	public void allocateRecorderUserDetails() {
		Net net = Net.getCurrent();
		if (net != null) {
			
			Application app = Application.getInstance();
			Properties config = app.getConfiguration();
			recorderUserName = config.getProperty("user.name");
			
			if (recorderUserName.equals("null")) {
				recorderUserName = System.getProperty("user.name");
			}
			
			recorderAddress = net.getLocalAddress();
			recorderPort = net.getLocalPort();
			
			state = STATE_RUNNING;
		}
	}
	
	public void allocateRemoteUserDetails() {
		Net net = Net.getCurrent();
		if (net != null && net.isConnected() == true) {
			remoteUserName = net.getRemoteUserName();
			
			// Remote User Name may be null (if remote user did't send THREAD_NAME)
			if (remoteUserName == null) {
				remoteUserName = "** No name available **";
			}
			remoteAddress = net.getRemoteAddress();
			remotePort = net.getRemotePort();
			
			state = STATE_RUNNING;
		}
	}
	
	public void allocateAllUsersDetails() {
		allocateRecorderUserDetails();
		allocateRemoteUserDetails();
	}
	
	public void storeMessage(Message msg) {
		if (state == STATE_CLOSED) throw new UnsupportedOperationException("Tape is closed");
		aList.add(msg);
		if (state != STATE_RUNNING) state = STATE_RUNNING;
	}
	public int dataSize() {
		return aList.size();
	}
	public List<Message> getDataList() {
		return aList;
	}
	
	public int getState() {
		return state;
	}
	
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
	public String getRemoteUserName() {
		return remoteUserName;
	}
	public void setRemoteUserName(String name) {
		remoteUserName = name;
		if (state != STATE_RUNNING) state = STATE_RUNNING;
	}
	public void setRemoteAddress(InetAddress addr) {
		remoteAddress = addr;
		if (state != STATE_RUNNING) state = STATE_RUNNING;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public String getRecorderUserName() {
		return recorderUserName;
	}
	public InetAddress getRecorderAddress() {
		return recorderAddress;
	}
	public int getRecorderPort() {
		return recorderPort;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void start() {
		startDate = new Date();
		if (state != STATE_RUNNING) state = STATE_RUNNING;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void close() {
		stopDate = new Date();
		state = STATE_CLOSED;
	}
	public boolean isClosed() {
		if (state == STATE_CLOSED) return true;
		else return false;
	}
	
	public static Cassette readFrom(VRCDATA file) {
		Cassette cas = new Cassette();
		createFrom(file, cas);
		return cas;
	}
	
	public static void createFrom(VRCDATA file, Cassette cas) {
		InetAddress remAdr = file.remoteAddress.getAddress();
		InetAddress recAdr = file.recorderAddress.getAddress();
		
		cas.startDate = new Date(file.startTime);
		cas.stopDate = new Date(file.endTime);
		cas.remoteUserName = file.remoteUserName;
		cas.recorderUserName = file.recorderUserName;
		cas.remoteAddress = remAdr;
		cas.recorderAddress = recAdr;
		cas.remotePort = file.remoteAddress.port;
		cas.recorderPort = file.recorderAddress.port;
		cas.state = file.state;
		cas.aList.clear();
		cas.aList.addAll(file.lstMessages);
	}
	
	public void writeTo(VRCDATA file) {
		file.startTime = startDate.getTime();
		file.endTime = stopDate.getTime();
		file.remoteUserName = remoteUserName;
		file.recorderUserName = recorderUserName;
		
		byte[] adr = remoteAddress.getAddress();
		int port = remotePort;
		file.remoteAddress.addressLength = (short)adr.length;
		file.remoteAddress.address = adr;
		file.remoteAddress.port = port;
		
		adr = recorderAddress.getAddress();
		port = recorderPort;
		file.recorderAddress.addressLength = (short)adr.length;
		file.recorderAddress.address = adr;
		file.recorderAddress.port = port;
		
		file.totalMessages = aList.size();
		file.lstMessages.addAll(aList);
	}
}
