/*
 */
package org.yarnandtail.compile;

import java.io.IOException;
import java.io.Writer;
import javax.tools.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class MemoryFileManagerTest {

	private JavaCompiler compiler;
	private MemoryFileManager manager;
	private TestClassLoader loader;

	public MemoryFileManagerTest() {
	}

	@BeforeEach
	public void setUp() {
		compiler = ToolProvider.getSystemJavaCompiler();
		manager = new MemoryFileManager(compiler);
		loader = new TestClassLoader(manager);
	}
	
	@Test
	public void generalTest() throws IOException {
		TestFile tf_packaged = manager.getFileForOutput(null, "org.test", "config.properties", null);
		try (Writer writer = tf_packaged.openWriter()) {
			writer.write("Hello World 1");
		}
		
		TestFile tf_unpackaged = manager.getFileForOutput(null, "", "config.properties", null);
		try (Writer writer = tf_unpackaged.openWriter()) {
			writer.write("Hello World 2");
		}
		
		
		
		//
		TestFile tf_packaged_found = manager.getTestFile("/org/test/config.properties");
		assertNotNull(tf_packaged_found);
		assertEquals("Hello World 1", tf_packaged_found.getCharContent(true).toString());
		//
		TestFile tf_unpackaged_found = manager.getTestFile("/config.properties");
		assertNotNull(tf_unpackaged_found);
		assertEquals("Hello World 2", tf_unpackaged_found.getCharContent(true).toString());
	}

	/**
	 * Test of removeTestSource method, of class MemoryFileManager.
	 */
	@Test
	public void testRemoveTestSource() {
	}

	/**
	 * Test of getTestFile method, of class MemoryFileManager.
	 */
	@Test
	public void testGetTestSource() {
	}

	/**
	 * Test of getJavaFileForOutput method, of class MemoryFileManager.
	 */
	@Test
	public void testGetJavaFileForOutput() throws Exception {
	}

	/**
	 * Test of getFileForOutput method, of class MemoryFileManager.
	 */
	@Test
	public void testGetFileForOutput() throws Exception {
	}

	/**
	 * Test of isSameFile method, of class MemoryFileManager.
	 */
	@Test
	public void testIsSameFile() {
	}

}
