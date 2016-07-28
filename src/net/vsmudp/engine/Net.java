package net.vsmudp.engine;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.PropertyName;

public class Net implements PropertyName{
	
	private static Net current;						// current running instance
	private static volatile boolean isInitialized;  // flag variable
	
	private static String flash_name;
	private static String print_pattern;
	
	private static final String SOC_INIT_FORMAT;			//////////////////////////////
	private static final String FRMT_DATAGRAM_SENT;			// some strings which are	//
	private static final String FRMT_SOC_CONNECT;			// constants. these are		//
	private static final String FRMT_SOC_ALREADY_CON;		// used for strig formatting//
	private static final String FRMT_SIGNAL_SENT;			//////////////////////////////
	
	private DatagramSocket socket;				// normal message receiver socket
	private DatagramSocket signalSocket;		// signal receiver socket
	private DatagramPacket sendPacket;			// packet which is used to send data (both signals & messages)
	
	private final byte[] messageBuffer;		// byte array to store string messages after various formatting
	private final byte[] signalBuffer;
	
	public static long sentSignalSize;			// total size of all sent signals
	public static long sentMessageSize;			// total size of all sent messages
	
	private InetAddress localAddress;			// local inet sequence
	private int localPort;						// port number in local machine
	private volatile boolean isConnected;  		// is connected or not
	
	private String remoteUserName;				// name of remote user (optional)
	private InetAddress remoteAddress;			// remote inet sequence
	private int remotePort;
	
	private static List<NetListener> listeners;	// listener list for connect/disconnect events
	
	private Application app;					// application instance;
	
	private Thread receiver;					// message receiver thread
	private Thread signalReceiver;				// signal receiver thread
	private volatile boolean receiverOn;  		// flag variable
	private volatile boolean signalReceiverOn;	// flag variable
	
	private static Random random;				// for random port generation
	
	public static final int DEFAULT_SIGNAL_PORT;
	
	static {
		current = null;
		isInitialized = false;
		DEFAULT_SIGNAL_PORT = 35902;
		sentSignalSize = sentMessageSize = 0;
		random = null;
		listeners = new ArrayList <NetListener> (10);
		flash_name = "NET";
		print_pattern = "%s : %s%n";
		SOC_INIT_FORMAT = "Socket initialized to %s port: %d";
		FRMT_DATAGRAM_SENT = "Datagram sent, length: %d bytes";
		FRMT_SOC_CONNECT = "Socket connected to %s, port: %d, user name: %s";
		FRMT_SOC_ALREADY_CON = "Socket already connected to %s, port: %d, user name: %s";
		FRMT_SIGNAL_SENT = "Signal (%d byte) sent to %s, command : %s, name: %s";
	}
	
	private Net() {
		socket = null;
		signalSocket = null;
		localAddress = null;
		localPort = -1;
		isConnected = false;
		receiver= null;
		signalReceiver = null;
		receiverOn = false;
		signalReceiverOn = false;
		remoteUserName = null;
		remoteAddress = null;
		remotePort = -1;
		sendPacket = new DatagramPacket(new byte[0], 0);
		messageBuffer = new byte[500];
		signalBuffer = new byte[50];
		app = Application.getInstance();
	}
	
	public static Net getCurrent() {
		return current;
	}
	private static void setCurrent(Net net) {
		current = net;
	}
	
	private static void println(String line) {
		Printer pr = Application.getInstance().getPrinter();
		pr.printf(print_pattern, flash_name, line);
	}
	
	public static synchronized final Net initialize() throws SocketException,
	UnknownHostException {
		
		if (isInitialized() == true) {
			println("NET already initialized");
			return getCurrent();
		}
		
		else {
			Net net = new Net();
			InetAddress local = InetAddress.getLocalHost();
			
			net.activateSignalSocket(local);
			net.activateMessageSocket(local);
			
			setCurrent(net);
			setInitialized(true);
			
			String format = "Initialized successfully, local spec: %s, port: %d (sigsoc:%d)";
			
			println(String.format(format, net.localAddress.getHostAddress(),
					net.localPort, net.signalSocket.getLocalPort()));
			
			return getCurrent();
		}
	}
	
	private synchronized final void activateSignalSocket(InetAddress localAddress) throws SocketException {

		signalSocket = new DatagramSocket(DEFAULT_SIGNAL_PORT, localAddress);
		signalReceiver = SignalReceiver.getNewInstanceAsThread(signalSocket, this);
		signalReceiver.start();
		
		setSignalReceiverOn(true);
			
	}
	
	private synchronized final void deActivateSignalSocket() {
		signalSocket.close();
		try {
			if (isSignalReceiverOn() == true) signalReceiver.join();
			setSignalReceiverOn(false);
		} catch (Exception ex) {
			Printer pr = Application.getInstance().getPrinter();
			pr.printStackTrace(ex);
		}
		
	}
	
	private static int generateRandomPort() {
		
		if (random == null) random = new Random();
		int port = 0;
		String format = "Generated Port: %s";
		boolean again = false;
		
		do {
			port = random.nextInt(65535);
			println(String.format(format, port));
			again = (port == Net.DEFAULT_SIGNAL_PORT) || (port <= 1024);
		} while (again == true);
		
		return port;
	}
	
	public static final boolean isInitialized() {
		return isInitialized;
	}
	private static final void setInitialized(boolean initFlag) {
		isInitialized = initFlag;
	}
	
	public boolean connectToHost(InetAddress address, int port, String remoteUser) {
		
		if (isConnected() == false) {
			//socket.connect(sequence, port);
			setConnected(true);
			setRemoteUserName(remoteUser);
			setRemoteAddress(address);
			setRemotePort(port);
			
			int size = listeners.size();
			for (int i=0; i < size; i++) {
				NetListener nl = listeners.get(i);
				nl.NetConnected();
			}
			
			println(String.format(FRMT_SOC_CONNECT, address.getHostAddress(), port, getRemoteUserName()));
			return true;
		} else {
			println(String.format(FRMT_SOC_ALREADY_CON, address.getHostAddress(), port, getRemoteUserName()));
			return false;
		}
	}
	
	public synchronized int sendSignalMessage(InetAddress address, NetCommand cmd) throws IOException {
		
		Properties config = app.getConfiguration();
		boolean isNameToBeSend = Boolean.parseBoolean(config.getProperty(CON_USER_SENDNAME));
		
		String name = null;
		if (isNameToBeSend == true) {
			name = config.getProperty(CON_USER_NAME);
			if (name == null) name = System.getProperty(CON_USER_NAME);
		}
		
		Signal signal = Signal.getFreeSignal();					// accquire a signal
		signal.setData(cmd, getLocalPort(), name);					// configure signal
		
		int sigLength = Signal.toByteArray(signal, signalBuffer, 0);// convert to bytes
		Signal.returnSignal(signal);
		
		sendPacket.setData(signalBuffer, 0, sigLength);
		sendPacket.setAddress(address);
		sendPacket.setPort(Net.DEFAULT_SIGNAL_PORT);
		
		signalSocket.send(sendPacket);
		sentSignalSize += sigLength;
		
		println(String.format(FRMT_SIGNAL_SENT, sigLength, address.getHostAddress(), cmd, String.valueOf(name)));
		
		return sigLength;	// return how many bytes are sent
		
	}
	
	public boolean isSignalReceiverOn() {
		return signalReceiverOn;
	}
	private void setSignalReceiverOn(boolean flag) {
		this.signalReceiverOn = flag;
	}
	
	public synchronized boolean openReceiverThread() {
		if (isReceiverOn() == false) {
			receiver = MessageReceiver.getNewInstanceAsThread(socket);
			receiver.start();
			setReceiverOn(true);
			println("Receiver Thread started scanning");
			return true;
		} else {
			println("Receiver thread already running");
			return false;
		}
	}
	
	public void connectToItself() {
		InetAddress add = getLocalAddress();
		int port = getLocalPort();
		connectToHost(add, port, null);
	}
	
	public void disconnectFromHost(boolean autoSocketInitialize) {
		
		if (isConnected() == true) {
			
			InetAddress remoteMachine = getRemoteAddress();
			
			socket.close();
			setConnected(false);
			setRemoteUserName(null);
			setRemoteAddress(null);
			setRemotePort(-1);
			
			try {
				sendSignalMessage(remoteMachine, NetCommand.REQUEST_DISCONNECT);
			} catch (IOException ex){
				Printer pr = Application.getInstance().getPrinter();
				pr.printStackTrace(ex);
			}
			
			if (isReceiverOn() == true) {
				try {
					receiver.join();
					setReceiverOn(false);
				} catch (Exception ex) {
					Printer pr = Application.getInstance().getPrinter();
					pr.printStackTrace(ex);
				}
			}
			
			println("Remote connection DISCONNECTED by user");
			
			if (autoSocketInitialize == true) {
				try {
					InetAddress local = InetAddress.getLocalHost();
					activateMessageSocket(local);
				} catch (Exception ex) {
					Printer pr = Application.getInstance().getPrinter();
					pr.printStackTrace(ex);
				}
			}
			
			for (NetListener nl : listeners) {
				nl.NetDisconnected();
			}
			
		} else {
			println("Main socket already disconnected");
		}
		
	}
	
	private void activateMessageSocket(InetAddress address) throws SocketException, UnknownHostException {
			
		socket = new DatagramSocket(generateRandomPort(), address);
		localAddress = socket.getLocalAddress();
		localPort = socket.getLocalPort();
		
		String print = String.format(SOC_INIT_FORMAT, localAddress.getHostAddress(), localPort);
		println(print);
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	private void setConnected(boolean conFlag) {
		isConnected = conFlag;
	}
	
	public int getLocalPort() {
		return localPort;
	}
	public InetAddress getLocalAddress() {
		return localAddress;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}
	private void setRemotePort(int i) {
		remotePort = i;
	}
	private void setRemoteUserName(String name) {
		remoteUserName = name;
	}
	private void setRemoteAddress(InetAddress addr) {
		remoteAddress = addr;
	}
	public String getRemoteUserName() {
		return remoteUserName;
	}
	public synchronized boolean isReceiverOn() {
		return receiverOn;
	}
	private void setReceiverOn(boolean onFlag) {
		receiverOn = onFlag;
	}
	
	public synchronized void dispose() {
		
		disconnectFromHost(false);
		
		try {
			deActivateSignalSocket();
			println("receiver joined & closed - totally disposed");
		} catch (Exception ex) {
			Printer pr = Application.getInstance().getPrinter();
			pr.printStackTrace(ex);
		}
		
	}
	
	public synchronized int sendDatagram(String message) throws IOException {
		
		Charset charset = app.charset();
		
		int msgLength = Message.toByteArray(message, charset, messageBuffer, 0);
		
		if (msgLength == -1) {
			println("Cannot build byte from user typed message");
			return msgLength;
		}
		
		sendPacket.setData(messageBuffer, 0, msgLength);	// fill up payload
		sendPacket.setAddress(getRemoteAddress());			// destination sequence
		sendPacket.setPort(getRemotePort());				// destination udp port
		socket.send(sendPacket);							// send datagram via UDP
		
		println(String.format(FRMT_DATAGRAM_SENT, msgLength));
		sentMessageSize += msgLength;
		return msgLength;
	}
	
	public synchronized static void addNetListener(NetListener nl) {
		listeners.add(nl);
		println("Listener added, address hash: " + nl.hashCode());
	}
	
	public synchronized static boolean removeNetListener(NetListener nl) {
		return listeners.remove(nl);
	}
	
}
