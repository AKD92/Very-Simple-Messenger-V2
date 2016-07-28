package net.vsmudp.engine.sequence;

import java.util.List;

public interface SequenceBuilder {
	
	public void buildSequences(CharSequence line);
	
	public Sequence[] transfer(Sequence[] output);
	
	public void transfer(List<Sequence> output);
	
	public int dumpUnusedChars(StringBuilder output);
	
	public StringBuilder getUnusedChars();
	
	public int count();
	
	public void reset();
	
}
