package net.vsmudp.engine.reaction;

import java.net.InetAddress;
import net.vsmudp.engine.*;
import net.vsmudp.engine.edt.PlaySound;

public abstract class AbstractReaction implements Reaction {
	
	private NetCommand command;
	private PlaySound sound;
	
	private static String line_ter;
	private static String flash_name;
	private static final String print_pattern;
	
	static {
		line_ter = System.getProperty("line.separator");
		flash_name = "SIGNAL REACTION";
		print_pattern = "%s : %s%s";
	}
	
	public AbstractReaction(NetCommand cmd) {
		command = cmd;
		sound = new PlaySound();
	}
	
	public abstract void react(Net net, Signal signal, InetAddress remoteAddress, int port);
	
	public final NetCommand getCommandType() {
		return command;
	}
	
	public final void setSound(String audioName) {
		sound.setAudio(audioName);
	}
	
	public final void playReactionSound() {
		sound.run();
	}
	
	public static final void println(String line) {
		System.out.printf(print_pattern, flash_name, line, line_ter);
	}

}
