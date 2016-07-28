package net.vsmudp.startup;

import java.util.concurrent.Callable;
import java.io.*;

import net.vsmudp.engine.record.*;
import net.vsmudp.gui.cassette.CassetteReader;

public class CassetteLoader implements Callable <Cassette> {
	private File loadFrom;
	
	public CassetteLoader(String file) {
		loadFrom = new File(file);
	}
	
	public Cassette call() throws Exception {
		CassetteReader reader = CassetteReader.getReader();
		Cassette tape = reader.readFromFile(loadFrom);
		return tape;
	}
	
}
