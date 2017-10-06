package org.yarnandtail.compile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.tools.*;

/**
 *
 * @author ericeverman
 */
public class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	public final Map<String, TestFile> map = new HashMap();

	public MemoryFileManager(JavaCompiler compiler) {
		super(compiler.getStandardFileManager(null, null, null));
	}
	
	/**
	 * Used by the class loader to remove the source from the map and convert it
	 * to an actual loaded class.
	 * 
	 * @param name
	 * @return 
	 */
	protected TestFile removeTestSource(String name) {
		return map.remove(name);
	}
	
	protected TestFile getTestFile(String name) {
		return map.get(name);
	}

	@Override
	public TestFile getJavaFileForOutput(JavaFileManager.Location location, String name, JavaFileObject.Kind kind, FileObject source) throws IOException {
		TestSource mc = new TestSource(name, kind);
		map.put(name, mc);
		return mc;
	}

	@Override
	public TestFile getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
		
		String fullName = relativeName;
		
		if (packageName != null && packageName.length() > 0) {
			fullName = "/" + packageName.replace('.', '/') + "/" + fullName;
		} else if (! fullName.startsWith("/")) {
			fullName = "/" + fullName;
		}
		
		TestResource tr = new TestResource(fullName, JavaFileObject.Kind.OTHER);
		
		map.put(fullName, tr);

		return tr;
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		System.out.println("isSameFile: " + a.toUri() + " to " + b.toUri() + " isSame: " + (a.toUri().compareTo(b.toUri()) == 0));
		return a.toUri().compareTo(b.toUri()) == 0;
	}

}
