package org.yarnandtail.compile;

import java.io.IOException;
import java.net.URI;
import javax.tools.JavaFileObject;

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


}
