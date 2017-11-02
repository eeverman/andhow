/*
 */
package org.yarnandtail.andhow.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class IOUtilTest {
	

	/**
	 * Test of expandFilePath method, of class IOUtil.
	 */
	@Test
	public void testExpandFilePath() {
		final String FILE_SEP_KEY = "file.separator";
		final String TEMP_DIR_KEY = "java.io.tmpdir";
		
		String orgSeparator = System.getProperty(FILE_SEP_KEY);
		String orgTmpFile = System.getProperty(TEMP_DIR_KEY);
		
		try {
			//Pretend we are in 'nix world
			System.setProperty(FILE_SEP_KEY, "/");
			System.setProperty(TEMP_DIR_KEY, "/temp/");

			assertEquals("/temp/some/path", IOUtil.expandFilePath("java.io.tmpdir/some/path"));
			assertEquals("/temp/", IOUtil.expandFilePath("java.io.tmpdir"));

			//Pretend we are in Alt world
			System.setProperty(FILE_SEP_KEY, "\\");
			System.setProperty(TEMP_DIR_KEY, "C:\\WINNT\\TEMP\\");

			assertEquals("C:\\WINNT\\TEMP\\some\\path", IOUtil.expandFilePath("java.io.tmpdir/some/path"));
			assertEquals("C:\\WINNT\\TEMP\\", IOUtil.expandFilePath("java.io.tmpdir"));
		} finally {
			//Restore world order
			System.setProperty(FILE_SEP_KEY, orgSeparator);
			System.setProperty(TEMP_DIR_KEY, orgTmpFile);
		}
		
		
		
	}
	
}
