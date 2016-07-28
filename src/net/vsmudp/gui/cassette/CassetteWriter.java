package net.vsmudp.gui.cassette;

import java.io.*;
import java.awt.*;

import net.vsmudp.engine.record.*;

public abstract class CassetteWriter {
	
	private static CassetteWriterImpl impl_instance;
	
	static {
		impl_instance = null;
	}
	
	public static CassetteWriter getWriter() {
		if (impl_instance == null) impl_instance = new CassetteWriterImpl();
		return impl_instance;
	}
	
	public abstract void saveToFile(Cassette tape, File f) throws IOException;
	
	public abstract void saveCassetteGUI(Component parent, Cassette tape);
	
	public abstract void saveCassetteGUI(Component parent, Cassette tape, File initSel);
	
	public abstract File getLastFileWritten();
	
	public abstract boolean showWriteError(Component parent, Exception ex);

}
