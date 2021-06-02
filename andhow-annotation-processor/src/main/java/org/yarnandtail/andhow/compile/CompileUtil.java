package org.yarnandtail.andhow.compile;

/**
 * Utilities for the AndHow AnnotationProcessor.
 */
public class CompileUtil {

	/**
	 * Determine the correct 'Generated' annotation class name based on the current Java runtime.
	 *
	 * This method fetches the version of the current runtime via getMajorJavaVersion() and
	 * uses that to call getGeneratedAnnotationClassName(int).
	 *
	 * @return A the fully qualified class name of the Generated annotation.
	 */
	public static String getGeneratedAnnotationClassName() {
		return getGeneratedAnnotationClassName(getMajorJavaVersion(System.getProperty("java.version")));
	}

	/**
	 * Determine the correct 'Generated' annotation class name based on the Java major version.
	 * Java 8 uses the <code>@javax.annotation.Generated</code> annotation to mark a generated class.
	 * Java 9 and beyond uses <code>@javax.annotation.processing.Generated</code>
	 *
	 * Both annotations are SOURCE level retention, so they are only present in the source code and no
	 * record of them is compiled into the binary.  Thus, the determination of which one to use is
	 * based only on the Java version used to compile, not the -source or -target settings.
	 *
	 * @param javaMajorVersion The Java version in integer form.  Use '8' for 1.8.
	 * @return A the fully qualified class name of the Generated annotation.
	 */
	public static String getGeneratedAnnotationClassName(int javaMajorVersion) {
		if (javaMajorVersion < 9) {
			return "javax.annotation.Generated";
		} else {
			return "javax.annotation.processing.Generated";
		}
	}

	/**
	 * Determine the major version of the Java runtime based on a version string.
	 *
	 * All versions are integers, thus version `1.8` returns `8`.
	 * Java 10 introduces the Runtime.version(), which would remove the need for this method,
	 * however, at the moment the code is still Java 8 compatable.
	 *
	 * @param versionString As returned from SystemProperties.getProperty("java.version")
	 * @return
	 */
	public static int getMajorJavaVersion(String versionString) {
		String[] versionParts = versionString.split("[\\.\\-_]", 3);

		try {
			if ("1".equals(versionParts[0])) {
				//Old style 1.x format
				return Integer.parseInt(versionParts[1]);
			} else {
				return Integer.parseInt(versionParts[0]);
			}

		} catch (NumberFormatException e) {
			throw new RuntimeException(
					"AndHow couldn't parse '" + versionString + "' as a 'java.version' string in System.properties. " +
							"Is this a non-standard JDK? ", e
			);
		}


	}
}
