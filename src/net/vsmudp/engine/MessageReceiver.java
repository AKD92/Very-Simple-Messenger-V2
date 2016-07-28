package net.vsmudp.engine;

import java.net.*;
import java.nio.charset.Charset;
import java.awt.Font;
import java.io.IOException;

import javax.swing.SwingUtilities;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.PropertyName;
import net.vsmudp.engine.edt.UpdateGUI;

final class MessageReceiver implements Runnable {
	
	public static final String THREAD_NAME;
	public static final int BUFFER_SIZE;
	private static final String CANNOT_DISPLAY_MESSAGE;
	private static final String UNKNOWN_SOURCE_FORMAT;
	
	public static volatile long receivedMessageSize;
	
	private static String flash_name;
	private static String print_pattern;
	
	private final DatagramSocket socket;
	private final byte[] rcvdata;
	private Application app;
	
	static {
		flash_name = "MSG THREAD";
		print_pattern = "%s : %s%n";
		THREAD_NAME = "Message Receiver Thread";
		BUFFER_SIZE = 700;
		CANNOT_DISPLAY_MESSAGE = "Some characters cannot be displayed";
		receivedMessageSize = 0;
		UNKNOWN_SOURCE_FORMAT = "Unknown message came from : %s,  port : %d";
	}
	
	MessageReceiver(DatagramSocket socket) {
		this.socket = socket;
		rcvdata = new byte[BUFFER_SIZE];
		app = Application.getInstance();
	}
	
	static final Thread getNewInstanceAsThread(DatagramSocket soc) {
		MessageReceiver rcv = new MessageReceiver(soc);
		Thread t = new Thread(rcv, THREAD_NAME);
		t.setDaemon(true);
		return t;
	}
	
	private static void println(String line) {
		Printer pr = Application.getInstance().getPrinter();
		pr.printf(print_pattern, flash_name, line);
	}
	
	public final void run() {
		
		DatagramPacket dp = new DatagramPacket(rcvdata, BUFFER_SIZE);
		
		UpdateGUI guiUpdate = UpdateGUI.getNewInstance();
		int len = 0;
		String received_format = "Message datagram received, length: %d bytes";
		InetAddress remoteAddress = null;
		
		while (true) {
			try {
				socket.receive(dp);
				
				len = dp.getLength();
				remoteAddress = dp.getAddress();
				receivedMessageSize += len;
				
				println(String.format(received_format, len));
				
				Charset charset = app.charset();
				Message msg = null;
				
				try {
					msg = Message.createMessage(rcvdata, 0, len, charset, Message.MESSAGE_RECEIVED);
				} catch (UnreadableMessageException ex) {
					String ip = remoteAddress.getHostAddress();
					String body = String.format(UNKNOWN_SOURCE_FORMAT, ip, dp.getPort());
					msg = new Message(body);
				} catch (IOException ex) {
					String body = "Undisplayable message. Maybe corrupted in transit";
					msg = new Message(body, Message.MESSAGE_RECEIVED);
				}
				
				guiUpdate.setMessage(msg);
				SwingUtilities.invokeLater(guiUpdate);
				
				// determine whether current font can display the message string
				
				String line = msg.getMessageBody();
				Font f = app.getFont(PropertyName.FONT_MSG_VIEW_EDITOR);
				int res = f.canDisplayUpTo(line);		// -1 means normal (can display all chars)
				if (res != -1) {
					String error = CANNOT_DISPLAY_MESSAGE;
					app.setInfoText(error);
					println(res + f.getName());
				}
				
			} catch (SocketException ex) {
				break;
			} catch (IOException ex) {
				ex.printStackTrace(System.err);
				break;
			}	// end try-catch
			
		}	// end while
		
		println("Main Socket closed, message receiver is turned off");
		
	}	// end run()

}
