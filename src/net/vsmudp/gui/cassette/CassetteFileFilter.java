package net.vsmudp.gui.cassette;

import java.io.File;

public final class CassetteFileFilter extends ExtensionFileFilter {
	
	private static final CassetteFileFilter sharedInstance;
	
	static {
		sharedInstance = new CassetteFileFilter();
	}
	
	public CassetteFileFilter() {
		super("Recorded Cassette Files (.vrc)", "vrc");
	}
	
	public static final CassetteFileFilter getSharedInstance() {
		return sharedInstance;
	}
	
	public final boolean accept(File f) {
		if (f == null) return false;
		else if (f.isDirectory() == true) {
			return true;
		}
		else {
			String name = f.getName().toLowerCase();
			int dotLoc = name.lastIndexOf('.');
			boolean hasExt = dotLoc > -1 && dotLoc < name.length() - 1;
			if (hasExt == true) {
				String fileExt = name.substring(dotLoc + 1);
				if (fileExt.equals(extension) == true) {
					return true;
				} else {
					return false;
				}
			} else return false;
		}
	}
	
	public String toString() {
		String format = "Cassette File Filter, description %s, extension %s, hashcode %d";
		String res = String.format(format, desc, extension, hashCode());
		return res;
	}

}
