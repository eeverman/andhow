package org.yarnandtail.andhow.compile;

import javax.lang.model.SourceVersion;

/**
 * Utilities for the AndHow AnnotationProcessor.
 */
public class CompileUtil {

	public static final String JDK8_GEN_CLASS = "javax.annotation.Generated";
	public static final String JDK9UP_GEN_CLASS = "javax.annotation.processing.Generated";

	/**
	 * Determine the correct 'Generated' annotation class name based on the Java major version.
	 * Java 8 uses the <code>@javax.annotation.Generated</code> annotation to mark a generated class.
	 * Java 9 and beyond uses <code>@javax.annotation.processing.Generated</code>
	 * <p>
	 * Both annotations are SOURCE level retention, so they are only present in the source code and no
	 * record of them is compiled into the binary.  Thus, the determination of which one to use is
	 * based only on the Java version used to compile, not the -source or -target settings.
	 * <p>
	 * @param jdkMajorVersion The Java version in integer form.  Use '8' for 1.8.
	 * @return The fully qualified class name of the Generated annotation.
	 */
	public static String getGeneratedAnnotationClassName(int jdkMajorVersion) {
		if (jdkMajorVersion < 9) {
			return JDK8_GEN_CLASS;
		} else {
			return JDK9UP_GEN_CLASS;
		}
	}

	/**
	 * Do we know for sure which JDK version to generate for?
	 * <p>
	 * There is one case that cannot be determined:  When
	 * {@code ProcessingEnvironment.getSourceVersion()} reports version 8* and the current JDK is
	 * greater than 8.  See https://github.com/eeverman/andhow/issues/630
	 * <p>
	 * *Source versions less than 8 are not supported by AndHow.
	 * <p>
	 * @param sourceVersion
	 * @param jdkMajorVersion
	 * @return
	 */
	public static boolean isGeneratedVersionDeterministic(
			int sourceVersion, int jdkMajorVersion) {

		return !(sourceVersion <= 8 && jdkMajorVersion > 8);
	}

	/**
	 * Determine the major version from the SourceVersion.
	 * <p>
	 * SourceVersion is available at compile time to the AnnotationProcessor and
	 * it marks the Java version that the source code needs to be compatible with.
	 * This may be different than the current JDK.
	 * <p>
	 * @param srcVersion As given to the AnnotationProcessor in the ProcessingEnvironment.
	 * @return The major version number
	 */
	public static int getMajorJavaVersion(final SourceVersion srcVersion) {
		return srcVersion.ordinal();
	}

	/**
	 * The major version number of the current runtime.
	 * <p>
	 * This uses the {@code System.getProperty("java.version")} and the
	 * {@code getMajorJavaVersion(String)} method.
	 * <p>
	 * @return The major version number
	 */
	public static int getMajorJavaVersion() {
		return getMajorJavaVersion(System.getProperty("java.version"));
	}

	/**
	 * Determine the major version from the java.version System property String.
	 * <p>
	 * The java.version string is parsed and major version returned.
	 * All versions are integers, thus version `1.8` returns `8`, JDK '10' return 10.
	 * A RuntimeException is thrown if the version cannot be parsed.
	 * <p>
	 * Java 10 introduces the Runtime.version(), which would remove the need for this method,
	 * however, its not available in JDK 8.
	 * <p>
	 * @param versionString as returned from {@code System.getProperty("java.version")}
	 * @return The major version number
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
