package net.vsmudp.engine.edt;

import net.vsmudp.gui.*;

public class CloseCriticalWindow implements Runnable {
	
	public void run() {
		OptionsViewer ov = OptionsViewer.getInstance();
		if (ov.isShowing() == true) ov.closeDialog();
		AddressMaker adm = AddressMaker.getInstance();
		if (adm.isShowing() == true) adm.disable();
	}

}
