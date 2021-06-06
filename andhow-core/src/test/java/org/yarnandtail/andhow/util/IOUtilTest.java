/*
 */
package org.yarnandtail.andhow.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ericeverman
 */
public class IOUtilTest {
	private static final String LINE_SEPARATOR = System.lineSeparator();
	private static final String EXAMPLE_FILE_CONTENT = "This file contains text in UTF-8 encoding." + LINE_SEPARATOR + "Line after empty line.";

	
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
	
	
	@Test
	public void testGetUTF8ResourceAsString() {
		try {
			String result = IOUtil.getUTF8ResourceAsString("/org/yarnandtail/andhow/util/IOUtilTest_ExampleFile.txt");
			assertEquals(EXAMPLE_FILE_CONTENT, result);
		} catch (IOException e) {
			fail("Should not fail");
		}
	}
	
	
	@Test
	public void testGetResourceAsString() {
		try {
			String result = IOUtil.getResourceAsString("/org/yarnandtail/andhow/util/IOUtilTest_ExampleFile.txt", StandardCharsets.UTF_8);
			assertEquals(EXAMPLE_FILE_CONTENT, result);
		} catch (IOException e) {
			fail("Should not fail");
		}
	}
	
	
	@Test
	public void testToString() {
		try {
			String inputWithNewLines = "test" + LINE_SEPARATOR + "test2" + LINE_SEPARATOR + "test3";
			String result = IOUtil.toString(new ByteArrayInputStream(inputWithNewLines.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
			assertEquals(inputWithNewLines, result);
			
			String singleLineInput = "This is a test";
			result = IOUtil.toString(new ByteArrayInputStream(singleLineInput.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
			assertEquals(singleLineInput, result);
		} catch (IOException e) {
			fail("Should not fail");
		}
	}
	
}
