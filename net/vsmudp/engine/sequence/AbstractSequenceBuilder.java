package net.vsmudp.engine.sequence;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vsmudp.Application;
import net.vsmudp.Printer;
import net.vsmudp.engine.Stack;
import net.vsmudp.engine.LinkedListStack;

abstract class AbstractSequenceBuilder implements SequenceBuilder {
	
	private Pattern splitter;
	private Pattern doubleDot;
	
	private String split_text;
	private String dotDuo;
	
	private List<Sequence> sequenceList;			// output result sequence list
	private StringBuilder sbOutput, sbTemp;			// buffers for operation
	
	private static final int DEFAULT_TEMP_SIZE;
	private final Stack<IndexRange> currentRange;
	
	static {
		DEFAULT_TEMP_SIZE = 2000;
	}
	
	AbstractSequenceBuilder(int outputBufferSize) {
		this(outputBufferSize, DEFAULT_TEMP_SIZE);
	}

	AbstractSequenceBuilder(int outputBufferSize, int tempBufferSize) {
		split_text = "==";
		dotDuo = "\\.\\.";

		try {
			splitter = Pattern.compile(split_text);
			doubleDot = Pattern.compile(dotDuo);
		} catch (Exception ex) {
			Printer p = Application.getInstance().getPrinter();
			p.printStackTrace(ex);
		}

		sbOutput = new StringBuilder(outputBufferSize);
		sbTemp = new StringBuilder(tempBufferSize);
		sequenceList = new LinkedList<Sequence>();
		currentRange = new LinkedListStack<IndexRange>();
	}
	
	protected void buildSequences(CharSequence line, Pattern... list) {
		sbTemp.append(line);
		
		for (Pattern x : list) {
			breakAndAppend(x);
		}
	}
	
	public int count() {
		return sequenceList.size();
	}
	public Sequence[] transfer(Sequence[] output) {
		Sequence[] res = sequenceList.toArray(output);
		return res;
	}
	public void transfer(List <Sequence> output) {
		output.addAll(sequenceList);
	}
	public int dumpUnusedChars(StringBuilder sbDump) {	// get rest of the chars dumped
		sbDump.append(sbTemp);
		return sbTemp.length();
	}
	public StringBuilder getUnusedChars() {		// get rest of the chars as-in buffer
		return sbTemp;
	}
	public void reset() {						// this can be reset for furthur use
		sbOutput.setLength(0);
		sbTemp.setLength(0);
		sequenceList.clear();
	}
	
	private void breakAndAppend(Pattern mainPattern) {
		Matcher m = mainPattern.matcher(sbTemp);
		
		while (m.find() == true) {
			String myString = removeTrailings(m.group()); 		// remote last dots (if
																// present)
			Matcher dotMatch = doubleDot.matcher(myString); 	// in-line dot finder to
																// fine more than one
																// dot (.., ....)
			Matcher again = mainPattern.matcher(myString); 		// match again
			boolean proceed = dotMatch.find() == false
					&& again.matches() == true;
			if (proceed == true) {
				sbOutput.append(myString);
				sbOutput.append(split_text);
				IndexRange rnge = IndexRange.getFromPool();
				rnge.setData(m.start(), m.end());
				currentRange.push(rnge);
			}
		}
		
		while (currentRange.empty() == false) {
			IndexRange x = currentRange.pop();
			sbTemp.delete(x.start, x.end);
			IndexRange.returnToPool(x);
		}
		
	}
	
	protected void splitAndMakeSequence(int typeOfSequence) {
		if (sbOutput.length() > 0) {
			String[] seqs = splitter.split(sbOutput);
			for (String x : seqs) {
				sequenceList.add(new Sequence(x, typeOfSequence));
			}
		}
	}
	
	private String removeTrailings(String sequence) {
		int len = sequence.length();
		String res = null;
		boolean contains = false;
		int last = 0;
		do {
			last = len - 1;
			contains = sequence.charAt(last) == '.'
					|| sequence.charAt(last) == '?'
					|| sequence.charAt(last) == ',';
			if (contains == true)
				len--;
		} while (contains == true);
		
		res = sequence.substring(0, len);
		return res;
	}
	
}
