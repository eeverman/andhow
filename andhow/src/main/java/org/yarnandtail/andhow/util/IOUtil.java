package org.yarnandtail.andhow.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 *
 * @author ericeverman
 */
public class IOUtil {

	
	private IOUtil() {
		//no instances
	}
	
	public static String getUTF8ResourceAsString(String path) throws IOException {
		InputStream in = IOUtil.class.getResourceAsStream(path);
		
		if (in == null) {
			throw new IOException("Unable to find the resource '" + path + "'");
		}
		
		return toString(in, Charset.forName("UTF-8"));
	}
	
	public static String getResourceAsString(String path, Charset encoding) throws IOException {
		InputStream in = IOUtil.class.getResourceAsStream(path);
		
		if (in == null) {
			throw new IOException("Unable to find the resource '" + path + "'");
		}
		
		return toString(in, encoding);
	}
	
	public static String toString(InputStream input, Charset encoding) throws IOException {

		StringBuilder buf = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding))) {
			String line = reader.readLine();
			while (line != null) {
				buf.append(line).append(System.lineSeparator());
				line = reader.readLine();
			}
		}
		
		return buf.toString();
	}

}
