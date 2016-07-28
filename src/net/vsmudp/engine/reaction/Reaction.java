package net.vsmudp.engine.reaction;

import net.vsmudp.engine.*;
import java.net.*;

public interface Reaction {
	
	// invoke this react() method for specific NetCommand
	public void react(Net net, Signal signal, InetAddress remoteAddress, int port);
	
	// check to which type of NetCommand this reaction object binds with
	public NetCommand getCommandType();
}
