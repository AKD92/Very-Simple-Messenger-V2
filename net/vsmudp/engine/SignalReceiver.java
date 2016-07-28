package net.vsmudp.engine;

import java.awt.Component;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.SwingUtilities;
import static javax.swing.JOptionPane.*;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.engine.reaction.Reaction;
import net.vsmudp.engine.reaction.ReactionFactory;

final class SignalReceiver implements Runnable {
	
	public static final String THREAD_NAME;
	public static final int BUFFER_SIZE;
	
	public static volatile long receivedSignalSize;
	
	private static String flash_name;
	private static final String print_pattern;
	
	private final DatagramSocket soc;
	private final byte[] rcvdata;
	private Net net;
	
	private ReactionFactory reactionFac;
	
	static {
		THREAD_NAME = "Signal Receiver Thread";
		BUFFER_SIZE = 100;
		receivedSignalSize = 0;
		flash_name = "SIGNAL THREAD";
		print_pattern = "%s : %s%n";
	}
	
	SignalReceiver(DatagramSocket soc, Net network) {
		this.soc = soc;
		rcvdata = new byte[BUFFER_SIZE];
		net = network;
		reactionFac = new ReactionFactory();
	}
	
	static final Thread getNewInstanceAsThread(DatagramSocket soc, Net network) {
		SignalReceiver sigr = new SignalReceiver(soc, network);
		Thread t = new Thread(sigr, THREAD_NAME);
		t.setDaemon(true);
		return t;
	}
	
	//@Override
	public final void run() {
		
		DatagramPacket dp = new DatagramPacket(rcvdata, BUFFER_SIZE);
		int len = 0;
		InetAddress remoteAddress = null;
		int remotePort = -1;
		Signal signal;
		NetCommand cmd = null;
		Reaction reaction = null;
		
		String signal_received_msg = "Signal received, command: %s (%d byte)";
		String bad_signal_received = "Bad signal received from %s : %d (%d byte)";
		
		try {
			while (true) {
				
				soc.receive(dp);		// receive signal datagram packet
				
				signal = null;
				len = dp.getLength();
				remoteAddress = dp.getAddress();
				
				receivedSignalSize += len;
				
				try {
					signal = Signal.fromByteArray(rcvdata, 0, len);
				} catch (IOException ex) {
					
					String address_str = remoteAddress.getHostAddress();
					SwingUtilities.invokeLater(new WrongSignalError(address_str));
					println(String.format(bad_signal_received ,address_str, dp.getPort(), len));
					continue;
				}
				
				// Important variables
				cmd = signal.getNetCommand();
				remotePort = signal.getRemotePort();
				
				println(String.format(signal_received_msg, cmd, len));
				
				// NOW PROCESS THE REQUEST
				
				reaction = reactionFac.getReaction(cmd);
				if (reaction != null)
					reaction.react(net, signal, remoteAddress, remotePort);
				
				Signal.returnSignal(signal);
			}  // end while
			
		} catch (SocketException ex) {
			println("Signal Socket closed, signal receiver is turned off");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private static void println(String line) {
		Printer pr = Application.getInstance().getPrinter();
		pr.printf(print_pattern, flash_name, line);
	}
	
	private final class WrongSignalError implements Runnable {
		private Component parent;
		private String msg;
		public WrongSignalError(String addr) {
			Application app = Application.getInstance();
			msg = String.format("Wrong or corrupted signal detected, no furthur action is taken%nfrom: %s"
					, addr);
			parent = (Component) app.getMainViewer();
		}
		public void run() {
			showMessageDialog(parent, msg, "Signal", ERROR_MESSAGE);
		}
	}
	
}
