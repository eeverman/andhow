package org.yarnandtail.compile;

import java.io.IOException;
import java.net.URI;
import javax.tools.JavaFileObject;
import org.yarnandtail.andhow.util.IOUtil;

/**
 *
 * @author ericeverman
 */
public class TestSource extends TestFile {


	public TestSource(String name, JavaFileObject.Kind kind) throws IOException {
		this(name, kind, null);
	}

	public TestSource(String name, Kind kind, String content) throws IOException {
		super(URI.create("file:///" + name.replace('.', '/') + kind.extension), kind, content);
	}
	
	/**
	 * Directly use a class somewhere on the source path.
	 * 
	 * Cannot be used with inner classes.
	 * 
	 * @param cannonicalClassName of a .java file in the <i>resources</i> of your project. 
	 */
	public TestSource(String cannonicalClassName) throws IOException {
		this(
				cannonicalClassName,
				JavaFileObject.Kind.SOURCE,
				IOUtil.getUTF8ResourceAsString("/" + cannonicalClassName.replace(".", "/") + ".java"));
	}


}
