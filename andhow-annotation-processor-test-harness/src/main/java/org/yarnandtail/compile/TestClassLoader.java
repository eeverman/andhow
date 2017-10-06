package org.yarnandtail.compile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author ericeverman
 */
public class TestClassLoader extends ClassLoader {

	private final MemoryFileManager memoryFileManager;
	
	public TestClassLoader(MemoryFileManager memoryFileManager) {
		super();
		this.memoryFileManager = memoryFileManager;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		TestFile ts = memoryFileManager.getTestFile(name);
		if (ts != null) {
			return new ByteArrayInputStream(ts.toByteArray());
		} else {
			return super.getResourceAsStream(name);
		}
	}
	
	

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		TestFile ts = memoryFileManager.removeTestSource(name);
		if (ts != null) {
			
			if (ts instanceof TestSource) {
				byte[] array = ts.toByteArray();
				return defineClass(name, array, 0, array.length);
			} else {
				throw new RuntimeException("Expected '" + name + "' to be a Java class source file, but it is not.");
			}
		}
		return super.findClass(name);
	}
}
