package net.vsmudp.engine.sequence;

import java.util.LinkedList;
import java.util.Queue;

public final class IndexRange {
	
	// INSTANCE VARIABLES
	int start;
	int end;
	
	// STATIC VARIABLES
	private static final String FRMT_TOSTRING;
	private static final Queue<IndexRange> rangePool;
	private static int INT_OCCUPIED_OBJ;
	
	static {
		rangePool = new LinkedList<IndexRange>();
		for (int i = 0; i < 5; i++) {
			rangePool.add(new IndexRange());
		}
		FRMT_TOSTRING = "Index Range, starts from index %d ends at index %d";
		INT_OCCUPIED_OBJ = 0;
	}
	
	IndexRange() {
		start = end = 0;
	}
	
	void setData(int s, int e) {
		start = s;
		end = e;
	}
	
	public boolean equals(IndexRange rng) {
		if (start == rng.start && end == rng.end) return true;
		else return false;
	}
	
	public String toString() {
		return String.format(FRMT_TOSTRING, start, end);
	}
	
	
	public static IndexRange getFromPool() {
		IndexRange rnge = rangePool.poll();
		if (rnge == null)
			rnge = new IndexRange();
		INT_OCCUPIED_OBJ++;
		return rnge;
	}
	
	public static int occupiedObjects() {
		return INT_OCCUPIED_OBJ;
	}
	
	public static int totalObjectsInPool() {
		return rangePool.size();
	}
	
	public static void returnToPool(IndexRange rnge) {
		rangePool.offer(rnge);
		INT_OCCUPIED_OBJ--;
	}
}
