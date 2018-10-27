package org.yarnandtail.andhow.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Utility class to handle some IO purposes.
 * @author ericeverman
 */
public class IOUtil {

	private static final String JAVA_TMP_KEY = "java.io.tmpdir";

	/**
	 * All utility methods are static.
	 */
	private IOUtil() {
		//no instances
	}

	/**
	 * Retrieve a String from an UTF-8 encoded classpath resource.
	 * @param path location on the classpath.
	 * @return a string reprensenting the entire content of the resource.
	 * @throws IOException when the resource cannot be loaded.
	 */
	public static String getUTF8ResourceAsString(String path) throws IOException {
		InputStream in = IOUtil.class.getResourceAsStream(path);

		if (in == null) {
			throw new IOException("Unable to find the resource '" + path + "'");
		}

		return toString(in, Charset.forName("UTF-8"));
	}

	/**
	 * Retrieve a String from a classpath resource.
	 * @param path location on the classpath.
	 * @return a string reprensenting the entire content of the resource.
	 * @throws IOException when the resource cannot be loaded.
	 */
	public static String getResourceAsString(String path, Charset encoding) throws IOException {
		InputStream in = IOUtil.class.getResourceAsStream(path);

		if (in == null) {
			throw new IOException("Unable to find the resource '" + path + "'");
		}

		return toString(in, encoding);
	}

	/**
	 * Build a String from the content on an InputStream using the encoding provided.
	 * @param input Stream containing the data to extract.
	 * @param encoding Charset used to read the input.
	 * @return String representing the content of the input.
	 * @throws IOException when the resource cannot be read.
	 */
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

	/**
	 * Expands directory path, replacing known values like java.io.tmpdir
	 * w/ their values. Paths are assumed to use forward slashes, which are replaced
	 * on Windows systems to be backslashes.
	 *
	 * As the provided path has to be a directory, the returned value will end with
	 * a file separator character.
	 *
	 * @param path directory path to expand if needed.
	 * @return the expanded directory path.
	 */
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
	 * @param path path to expand if needed.
	 * @return the expanded path.
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
