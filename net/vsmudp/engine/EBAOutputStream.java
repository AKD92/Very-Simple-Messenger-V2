package net.vsmudp.engine;

import java.io.OutputStream;
import java.io.IOException;

public class EBAOutputStream extends OutputStream {
	
	private int count;
	private int start;
	private int end;
	private byte[] buf;
	
	public static final int DEFAULT_BUFFER_SIZE;
	
	static {
		DEFAULT_BUFFER_SIZE = 100;
	}
	
	public EBAOutputStream() {
		this(DEFAULT_BUFFER_SIZE);
	}
	
	public EBAOutputStream(int len) {
		this(new byte[len], 0, len);
	}
	
	public EBAOutputStream(byte[] buffer) {
		setDestination(buffer);
	}
	
	public EBAOutputStream(byte[] buffer, int offset, int len) {
		setDestination(buffer, offset, len);
	}
	
	@Override
	public void write(int value) throws IOException {
		int newposition = count + start;
		if (newposition > end)
			throw new ArrayIndexOutOfBoundsException();
		byte val = (byte) value;
		buf[newposition] = val;
		count++;
	}
	
	@Override
	public void write(byte[] buffer, int offset, int len) throws IOException {
		int remaining = remaining();
		if (remaining < len)
			throw new ArrayIndexOutOfBoundsException();
		int newpos = count + start;
		System.arraycopy(buffer, offset, buf, newpos, len);
		count += len;
	}
	
	@Override
	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}
	
	@Override
	public void close() throws IOException {
	}
	
	public byte[] internalBuffer() {
		return buf;
	}
	
	public byte[] toByteArray() {
		byte[] res = new byte[count];
		System.arraycopy(buf, start, res, 0, count);
		return res;
	}
	
	public int size() {
		return count;
	}
	
	public int offset() {
		return start;
	}
	
	public int remaining() {
		int capacity = end - start + 1;
		int remaining = capacity - count;
		return remaining;
	}
	
	public void reset() {
		count = 0;
	}
	
	public void setDestination(byte[] buffer) {
		int len = 0;
		if (buffer != null) len = buffer.length;
		setDestination(buffer, 0, len);
	}
	
	public void setDestination(byte[] buffer, int offset, int len) {
		reset();
		buf = buffer;
		start = offset;
		end = len + offset - 1;
	}
	
}
