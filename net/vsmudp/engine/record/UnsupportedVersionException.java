package net.vsmudp.engine.record;

@SuppressWarnings("serial")
public class UnsupportedVersionException extends CassetteIOException {
	
	public final int cVer;
	public final int fVer;
	
	private static String FRMT;
	
	static {
		FRMT = "File version is higher than current supported version, supported version %s, file version %s";
	}
	
	public UnsupportedVersionException(int currentVer, int fileVer) {
		super(String.format(FRMT, currentVer, fileVer));
		cVer = currentVer;
		fVer = fileVer;
	}

}
