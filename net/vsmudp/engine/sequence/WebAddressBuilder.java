package net.vsmudp.engine.sequence;

import java.util.regex.*;
import net.vsmudp.*;

class WebAddressBuilder extends AbstractSequenceBuilder {
	
	private String type1, type2, type3, type4;
	private Pattern p1, p2, p3, p4;
	
	public WebAddressBuilder() {
		
		super(800);
		
		type1 = "\\w+://www\\.[\\w\\-_.]+\\.[\\w_=/\\?#\\-.:%,]{2,}";
		type2 = "\\w+://[\\w\\-_.]+\\.[\\w_=/\\?#\\-.:%,]{2,}";
		type3 = "www\\.[\\w\\-_.]+\\.[\\w_=/\\?#\\-.:%,]{2,}";
		type4 = "[\\w\\-_]+\\.[\\w_=/\\?#\\-.:%,]{2,}"; 			// katpah.org

		try {
			p1 = Pattern.compile(type1);
			p2 = Pattern.compile(type2);
			p3 = Pattern.compile(type3);
			p4 = Pattern.compile(type4);
		} catch (Exception ex) {
			Printer p = Application.getInstance().getPrinter();
			p.printStackTrace(ex);
		}
	}
	
	public void buildSequences(CharSequence line) {
		super.buildSequences(line, p1, p2, p3, p4);
		
//		breakAndAppend(p1); 			// http://www.xxx.xx/xxx=_90/xxx.php
//		// System.out.println("p1 completed");
//		breakAndAppend(p2); 			// http://xxxx-xxx_xxx.xxx/xxxxx
//		// System.out.println("p2 completed");
//		breakAndAppend(p3); 			// www.xxxxx.xxx/xxxx
//		// System.out.println("p3 completed");
//		breakAndAppend(p4); 			// xxxxx.xxxx
//		// System.out.println("p4 completed");
		
		splitAndMakeSequence(Sequence.TYPE_WEB);
		
	}

}
