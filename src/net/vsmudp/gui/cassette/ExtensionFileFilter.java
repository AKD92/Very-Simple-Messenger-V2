package net.vsmudp.gui.cassette;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public abstract class ExtensionFileFilter extends FileFilter {
	
	protected final String extension;
	protected final String desc;
	
	public ExtensionFileFilter(String desc, String ext) {
		this.desc = desc;
		extension = ext;
	}
	
	public final String getDescription() {
		return desc;
	}
	
	public final String getExtension() {
		return extension;
	}
	
	public final File getFilePathWithExtension(File args) {
		if (accept(args) == true) return args;
		else {
			String path = args.getPath();
			path = path.concat(".").concat(extension);;
			return new File(path);
		}
	}
}
