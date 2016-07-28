package net.vsmudp.engine.sequence;

import java.util.regex.Pattern;
import net.vsmudp.Application;
import net.vsmudp.Printer;

class MailAddressBuilder extends AbstractSequenceBuilder {
	
	private String type1, type2;
	private Pattern p1, p2;
	
	public MailAddressBuilder() {
		
		super(400);
		
		type1 = "mailto:[\\w_\\-.]+@[\\w.\\-_]+\\.[\\w.]{2,}"; // a mail pattern
		type2 = "[\\w_\\-.]+@[\\w.\\-_]+\\.[\\w.]{2,}"; // katpah.com
		
		try {
			p1 = Pattern.compile(type1);
			p2 = Pattern.compile(type2);
		} catch (Exception ex) {
			Printer p = Application.getInstance().getPrinter();
			p.printStackTrace(ex);
		}
	}
	
	public void buildSequences(CharSequence line) {
		super.buildSequences(line, p1, p2);
		
//		breakAndAppend(p1); 					// mailto:xxx.xxx-xxx@xxx.xxx
//		// System.out.println("p1 completed");
//		breakAndAppend(p2); 					// xxx.xxx-xxx@xxx.xxx
//		// System.out.println("p2 completed");
		
		splitAndMakeSequence(Sequence.TYPE_MAIL);
		
	}

}
