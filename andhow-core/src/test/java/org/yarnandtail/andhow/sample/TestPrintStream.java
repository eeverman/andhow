package org.yarnandtail.andhow.sample;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ericeverman
 */
public class TestPrintStream extends PrintStream {


	final ByteArrayOutputStream baos;
	
	public TestPrintStream() throws UnsupportedEncodingException {
		this(new ByteArrayOutputStream());
	}
	
	public TestPrintStream(ByteArrayOutputStream baos) throws UnsupportedEncodingException {
		super(baos, true, "UTF-8");
		this.baos = baos;
	}

	public String getTextAsString() {
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}
    
	public String[] getTextAsLines() {
		String all = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		return all.split("\\R");
	}
	
	public void reset() {
		baos.reset();
	}
}
