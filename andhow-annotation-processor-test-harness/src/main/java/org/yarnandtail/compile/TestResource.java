package org.yarnandtail.compile;

import java.io.IOException;
import java.net.URI;
import javax.tools.JavaFileObject;

/**
 *
 * @author ericeverman
 */
public class TestResource extends TestFile {

	
	public TestResource(String name, JavaFileObject.Kind kind) throws IOException {
		this(name, kind, null);
	}

	public TestResource(String name, Kind kind, String content) throws IOException {
		super(URI.create("file:///" + name), kind, content);
	}
	
	

}
