package net.vsmudp.startup;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.engine.*;
import net.vsmudp.engine.security.DataSecurity;
import net.vsmudp.engine.security.SecurityInitializer;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class InitEngine implements Callable<Net> {
	
	public Net call() throws SocketException, UnknownHostException {
		
		Net net = Net.initialize();
		
		byte[] data = getDefaultKey();
		SecurityInitializer init = DataSecurity.getInitializer();
		init.setSecretKey(data);
		init.setInitVector(data);
		init.setMultithreaded(false);
		init.setToken(35);
		
		try {
			init.applySetting();
		} catch (Exception e) {
			Printer pr = Application.getInstance().getPrinter();
			pr.printStackTrace(e);
		}
		return net;
	}
	
	private byte[] getDefaultKey() {
		byte[] data = new byte[16];
		for (int i=0; i < data.length; i++) {
			data[i] = (byte)( i * 5 - 3);
		}
		return data;
	}
}
