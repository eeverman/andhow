package org.yarnandtail.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 *
 * @author ericeverman
 */
public abstract class TestFile extends SimpleJavaFileObject {

	protected final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	protected TestFile(URI uri, Kind kind) {
		super(uri, kind);
	}

	TestFile(URI uri, Kind kind, String content) throws IOException {
		super(uri, kind);
		
		if (content != null) {
			outputStream.write(content.getBytes());
		}
	}
	
	byte[] toByteArray() {
		return this.outputStream.toByteArray();
	}

	public ByteArrayOutputStream openOutputStream() {
		return this.outputStream;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return new String(toByteArray());
	}

}
