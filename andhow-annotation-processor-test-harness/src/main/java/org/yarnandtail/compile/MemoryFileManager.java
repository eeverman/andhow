package org.yarnandtail.compile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.tools.*;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
 * 
 * One weakness of this setup:  There is no distiction of Location.  Methods like
 * getFileForOutput() ignore the Location and just store the file to the same
 * memory file structure.  If a non-Java resource file is written to Location.SOURCE_OUTPUT,
 * it will not be copied over by the compiler to the /classes directory, effectively
 * removing it from the final artifact.  However, the TestClassLoader built on
 * top of this FileManager will see that resource file via getResourceAsStream().
 * This can hide bugs where the wrong Location is selected for file output.
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
		//System.out.println("isSameFile: " + a.toUri() + " to " + b.toUri() + " isSame: " + (a.toUri().compareTo(b.toUri()) == 0));
		return a.toUri().compareTo(b.toUri()) == 0;
	}

}
