package net.vsmudp.engine.sequence;

import java.util.Comparator;

public class SequenceComparator implements Comparator <Sequence> {
	
	public int compare(Sequence seq1, Sequence seq2) {
		int diff = 0;
		if (seq1.equals(seq2)) diff = 0;
		else{
			int type1 = seq1.getType();
			int type2 = seq2.getType();
			if (type1 != type2) diff = type1 - type2;
			else diff = seq1.getText().compareTo(seq2.getText());
		}
		return diff;
	}

}
