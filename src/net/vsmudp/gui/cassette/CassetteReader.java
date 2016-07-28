package net.vsmudp.gui.cassette;

import java.io.*;
import net.vsmudp.engine.record.*;
import java.awt.*;

public abstract class CassetteReader {
	
	private static CassetteReaderImpl impl_instance;
	
	static {
		impl_instance = null;
	}
	
	public static CassetteReader getReader() {
		if (impl_instance == null) impl_instance = new CassetteReaderImpl();
		return impl_instance;
	}
	
	public abstract Cassette readFromFile(File f) throws IOException, CassetteIOException;
	
	public abstract Cassette readCassetteGUI(Component parent);
	
	public abstract Cassette readCassetteGUI(Component parent, File initSel);
	
	public abstract File getLastFileRead();
	
	public abstract boolean showInputError(Component parent, File inputFile, Exception ex);

}
