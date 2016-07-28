package net.vsmudp.engine.reaction;

import java.util.Map;
import java.util.EnumMap;
import net.vsmudp.engine.NetCommand;

public class ReactionFactory {
	
	private Map <NetCommand, Reaction> reacMap;
	
	public ReactionFactory() {
		reacMap = new EnumMap <NetCommand, Reaction> (NetCommand.class);
		addToMap(new RequestConnectReaction());
		addToMap(new RequestDisconnectReaction());
		addToMap(new RequestAcceptedReaction());
		addToMap(new RequestDeniedReaction());
		addToMap(new UserBusyReaction());
		addToMap(new UserChattingWithOthersReaction());
		addToMap(new UserFeelsUntrustedReaction());
	}
	
	public Reaction getReaction(NetCommand type) {
		return reacMap.get(type);
	}
	
	private void addToMap(Reaction reac) {
		NetCommand cmd = reac.getCommandType();
		reacMap.put(cmd, reac);
	}

}
