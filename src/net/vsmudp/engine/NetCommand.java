package net.vsmudp.engine;

public enum NetCommand {
	
	REQUEST_CONNECT ("cmd///connect", (short)1),
	REQUEST_DISCONNECT ("cmd///disconnect", (short)2),
	REQUEST_ACCEPTED ("cmd///accepted", (short)3),
	REQUEST_DENIED ("cmd///denied", (short)4),
	USER_FEELS_UNTRUSTED ("user///untrusted", (short)5),
	USER_BUSY ("user///busy", (short)6),
	USER_CHATTING_WITH_OTHERS ("user///chattingwithothers", (short)7);
	
	private String cmdString;
	private short id;
	
	NetCommand(String cmd, short id) {
		cmdString = cmd;
		this.id = id;
	}
	
	public short getID() {
		return id;
	}
	
	public String commandString() {
		return cmdString;
	}
	
	public String toString() {
		return cmdString;
	}
	
	public static NetCommand parse(String str) {
		NetCommand[] netcmds = NetCommand.values();
		NetCommand res = null;
		
		for (NetCommand cmd : netcmds) {
			if (cmd.cmdString.equals(str)){
				res = cmd;
				break;
			}
		}
		return res;
	}
	
	public static NetCommand parse(short id) {
		NetCommand[] all = NetCommand.values();
		NetCommand res = null;
		
		for (NetCommand x : all) {
			if (x.id == id) {
				res = x;
				break;
			}
		}
		return res;
	}
	
}
