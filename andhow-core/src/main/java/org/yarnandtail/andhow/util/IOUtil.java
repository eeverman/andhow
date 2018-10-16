package org.yarnandtail.andhow.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 *
 * @author ericeverman
 */
public class IOUtil {
	private static final String JAVA_TMP_KEY = "java.io.tmpdir";
	private static final String USER_DIR_KEY = "user.home";
	
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

		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding))) {
			String line = reader.readLine();
			while (line != null) {
				builder.append(line).append(System.lineSeparator());
				line = reader.readLine();
			}
		}

		if (builder.length() > 0)
			builder.setLength(builder.length() - System.lineSeparator().length());
		
		return builder.toString();
	}
	
	public static String expandDirectoryPath(String path) {
		path = expandFilePath(path);
		
		if (! path.endsWith(String.valueOf(File.separatorChar))) {
			return path + File.separatorChar;
		} else {
			return path;
		}
	}
	
	/**
	 * Expands a file or directory path, replacing known values like java.io.tmpdir
	 * w/ their values.  Paths are assumed to use forward slashes, which are replaced
	 * on Windows systems to be backslashes.
	 * 
	 * The returned path may be either a file path (not ending with a separator)
	 * or directory (ending with a separator).
	 * 
	 * @param path
	 * @return 
	 */
	public static String expandFilePath(String path) {
		
		String tmpDir = System.getProperty(JAVA_TMP_KEY);
		path = path.trim();
		
		//rm the trailing separatorChar from the temp directory if there is
		//possibly more to the path than just the temp directory.  Otherwise the
		//sep char gets doubled up.
		if (path.length() > JAVA_TMP_KEY.length() && tmpDir.endsWith(System.getProperty("file.separator"))) {
			tmpDir = tmpDir.substring(0, tmpDir.length() - 1);
		}
		
		path = path.replace((CharSequence)JAVA_TMP_KEY, (CharSequence)tmpDir);
		path = path.replace('/', System.getProperty("file.separator").charAt(0));
		return path;
	}

}
