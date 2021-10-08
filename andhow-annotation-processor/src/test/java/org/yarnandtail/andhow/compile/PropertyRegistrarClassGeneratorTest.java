package org.yarnandtail.andhow.compile;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.util.*;

import static org.hamcrest.Matchers.*;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.*;

public class PropertyRegistrarClassGeneratorTest {
	
	private final static String ROOT_SIMPLE_NAME = "MyClass";
	private final static String ROOT_PKG = "org.big.comp";
	private final static String ROOT_QUAL_NAME = ROOT_PKG + "." + ROOT_SIMPLE_NAME;
	private final static String INNER1_SIMP_NAME = "Inner1";
	private final static String INNER2_SIMP_NAME = "Inner2";
	
	private final static String PROP1_NAME = "prop1";
	private final static String PROP2_NAME = "prop2";

	private final String runDateStartsWith = "2017-09-29T14:01:01.999"; //Not checking the exact tz b/c it is local dependent
	private final String runDateRegex = "2017-09-29T14:01:01\\.999[+-][01]\\d{3}"; //Regex for the timezone

	// The content (only) of the @Generated annotation
	private final String annotationContentRegex =
			"\\s*value=\"org\\.yarnandtail\\.andhow\\.compile\\.AndHowCompileProcessor\"," +
			"\\s*date=\"" + runDateRegex + "\"," +
			"\\s*comments=\"[ \\w/\\.\\-]*\"";

	private GregorianCalendar runDate;
	private PropertyRegistrarClassGenerator jdk8Generator;		//Fully JDK8
	private PropertyRegistrarClassGenerator jdk9Generator;		//Fully JDK9
	private PropertyRegistrarClassGenerator jdk9src8Generator;		//JDK reported as 9, source as 8

	@BeforeEach
	public void initEach() {
		runDate = new GregorianCalendar();
		//runDate.setTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT-6")));
		runDate.set(2017, 8, 29, 14, 1, 1);
		runDate.set(Calendar.MILLISECOND, 999);

		jdk8Generator = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, 8);

		jdk9Generator = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 9, 9);

		jdk9src8Generator = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, 9);
	}
	

	/**
	 * Test of getTemplate method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void getTemplateShouldReturnNonEmptyStringsForTheThreeTemplates() throws Exception {

		String template = jdk8Generator.getTemplate(PropertyRegistrarClassGenerator.ANNOTATION_TEMPLATE_PATH);
		assertTrue(template.length() > 20);

		template = jdk8Generator.getTemplate(PropertyRegistrarClassGenerator.COMMENTED_ANNOTATION_TEMPLATE_PATH);
		assertTrue(template.length() > 20);

		template = jdk8Generator.getTemplate(PropertyRegistrarClassGenerator.CLASS_TEMPLATE_PATH);
		assertTrue(template.length() > 20);
	}

	@Test
	public void generateAnnotationSourceForJdk8() throws Exception {

		String annotationSource = jdk8Generator.generateAnnotationSource();

		String annOnlyPattern = "(?s).*@javax\\.annotation\\.Generated\\(" +
																annotationContentRegex + "\\)\\s*";

		MatcherAssert.assertThat(annotationSource, matchesPattern(annOnlyPattern));

		String completeSource = jdk8Generator.generateSource();

		String completePattern = "(?s).*@javax\\.annotation\\.Generated\\(" +
																 annotationContentRegex + "\\)" +
												 "\\s*public class.*";

		MatcherAssert.assertThat(completeSource, matchesPattern(completePattern));
	}

	@Test
	public void generateAnnotationSourceForJdk9Src8() throws Exception {

		String annotationSource = jdk9src8Generator.generateAnnotationSource();

		String annOnlyPattern = "(?s)/\\*.*@javax\\.annotation\\.processing\\.Generated\\(" +
																annotationContentRegex + "\\)\\s*\\*/\\s*";

		MatcherAssert.assertThat(annotationSource, matchesPattern(annOnlyPattern));

		String completeSource = jdk9src8Generator.generateSource();

		String completePattern = "(?s).*" + annotationContentRegex + "\\)\\s*\\*/\\s*" +
																 "\\s*public class.*";

		MatcherAssert.assertThat(completeSource, matchesPattern(completePattern));
	}

	@Test
	public void testGeneratedAnnotationForJdk9() throws Exception {

		String source = jdk9Generator.generateSource();

		String pattern = "(?s).*@javax\\.annotation\\.processing\\.Generated\\(" +
												 annotationContentRegex + "\\)" +
												 "\\s*public class.*";

		MatcherAssert.assertThat(source, matchesPattern(pattern));
	}
	
	/**
	 * Test of buildPackageString method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildPackageString() {

		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, 8);
		assertEquals("package " + ROOT_PKG + ";", gen.buildPackageString());

		gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate, 8, 8);
		assertEquals("", gen.buildPackageString());
	}

	/**
	 * Test of buildGeneratedClassSimpleName method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildGeneratedClassSimpleName() {
		assertEquals("$" + ROOT_SIMPLE_NAME + "_AndHowProps", jdk8Generator.buildGeneratedClassSimpleName());

		// Now with the default package
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate, 8, 8);
		assertEquals("$" + ROOT_SIMPLE_NAME + "_AndHowProps", gen.buildGeneratedClassSimpleName());
	}
	
	@Test
	public void testBuildGeneratedClassFullName() {
		assertEquals(ROOT_PKG + ".$" + ROOT_SIMPLE_NAME + "_AndHowProps", jdk8Generator.buildGeneratedClassFullName());

		// Now with the default package
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate, 8, 8);
		assertEquals("$" + ROOT_SIMPLE_NAME + "_AndHowProps", gen.buildGeneratedClassFullName());
	}

	/**
	 * Test of buildRunDateString method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildRunDateString() {
		assertTrue(jdk8Generator.buildRunDateString().startsWith(runDateStartsWith));
		MatcherAssert.assertThat(jdk8Generator.buildRunDateString(), matchesPattern(runDateRegex));
	}

	/**
	 * Test of buildRegistrationAddsString method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildRegistrationAddsString_DefaultPkg() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate, 8, 8);
		String adds = gen.buildRegistrationAddsString();
		String eachAdds[] = adds.trim().split("[\\t\\n\\r\\f]+");
		assertEquals(2, eachAdds.length);
		assertEquals("list.add(\"" + PROP1_NAME + "\");", eachAdds[0]);
		assertEquals("list.add(\"" + PROP2_NAME + "\");", eachAdds[1]);
	}
	
	@Test
	public void testBuildRegistrationAddsString_WithPkg() {

		String adds = jdk9Generator.buildRegistrationAddsString();
		
		//Should all be the same as for default pkg
		String eachAdds[] = adds.trim().split("[\\t\\n\\r\\f]+");
		assertEquals(2, eachAdds.length);
		assertEquals("list.add(\"" + PROP1_NAME + "\");", eachAdds[0]);
		assertEquals("list.add(\"" + PROP2_NAME + "\");", eachAdds[1]);
	}
	
	@Test
	public void testBuildRegistrationAddsString_Complex() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				complexCompileUnit(), AndHowCompileProcessor.class, runDate, 8, 8);
		String adds = gen.buildRegistrationAddsString();
		String eachAdds[] = adds.trim().split("[\\t\\n\\r\\f]+");
		
		//These will be in sorted order
		assertEquals(5, eachAdds.length);
		assertEquals("list.add(\"" + PROP1_NAME + "\");", eachAdds[0]);
		assertEquals("list.add(\"" + PROP1_NAME + "\", \"" + INNER1_SIMP_NAME + "\");", eachAdds[1]);
		assertEquals("list.add(\"" + PROP2_NAME + "\");", eachAdds[2]);	//No inner path b/c in inherits from above
		assertEquals("list.add(\"" + PROP1_NAME + "\", \"" + INNER1_SIMP_NAME + "\", \"" + INNER2_SIMP_NAME + "\");", eachAdds[3]);
		assertEquals("list.add(\"" + PROP2_NAME + "\");", eachAdds[4]);	//No inner path b/c in inherits from above
	}

	/**
	 * Basic gross test that the generated source is compilable
	 */
	@Test
	public void testGenerate_simple() throws Exception {
		int javaVersion = getMajorJavaVersion(System.getProperty("java.version"));

		if (javaVersion < 9) {
			// This would have to be JDK8 b/c there are maven rules that don't allow less than JDK8

			Compilation compilation = compileForCurrentJdk(
					jdk8Generator.buildGeneratedClassFullName(), jdk8Generator.generateSource());
			assertThat(compilation).succeeded();

		} else {
			// Developers can run any JDK 8+, so will run for JDKs 9 and up.
			// The automated builds are expected to run JDK 8 and a newer version.
			// This gives some advanced testing w/ newer JDKs and a mix of options

			//
			// Scenario 1:  Source version and JDK set to current version (version is for sure 9 or higher)
			// This is the normal/default build on JDK 9+, if the user doesn't specify -source=8
			// or --release=8.
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, javaVersion, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForCurrentJdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());

				assertThat(compilation).succeeded();
			}

			//
			// Scenario 2:  Compile to --release=8 (source is spec'ed at 8)
			// Even though the JDK is 9+, using the --release=8 means that the source version is 8
			// AND THE JDK 8 LIBRARIES ARE ON THE CLASSPATH.
			// The code generator should comment out the {@code Generated} annotation because it cannot
			// determine the correct annotation to use.
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForRelease8Jdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());
				assertThat(compilation).succeeded();
			}

			//
			// Scenario 3:  Compile to -source=8 -target=8
			// The JDK 9 libraries will be on the classpath.
			// The code generator should comment out the {@code Generated} annotation because it cannot
			// determine the correct annotation to use.
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForTarget8Jdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());
				assertThat(compilation).succeeded();
			}
		}
	}

	/**
	 * The test is the same as testGenerate_simple w/ a different CompileUnit.
	 * See testGenerate_simple for notes on the test.
	 * @throws Exception
	 */
	@Test
	public void testGenerate_simpleCompileUnit_DefaultPkg() throws Exception {
		int javaVersion = getMajorJavaVersion(System.getProperty("java.version"));

		if (javaVersion < 9) {

			Compilation compilation = compileForCurrentJdk(
					jdk8Generator.buildGeneratedClassFullName(), jdk8Generator.generateSource());
			assertThat(compilation).succeeded();

		} else {

			// Scenario 1:  Compile to the current JDK (version is for sure 9 or higher)
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, javaVersion, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForCurrentJdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());

				assertThat(compilation).succeeded();
			}

			// Scenario 2:  Compile to --release=8
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForRelease8Jdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());
				assertThat(compilation).succeeded();
			}

			// Scenario 3:  Compile to -source=8 -target=8
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForTarget8Jdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());
				assertThat(compilation).succeeded();
			}
		}
	}


	/**
	 * The test is the same as testGenerate_simple w/ a different CompileUnit.
	 * See testGenerate_simple for notes on the test.
	 * @throws Exception
	 */
	@Test
	public void testGenerate_complex() throws Exception {
		int javaVersion = getMajorJavaVersion(System.getProperty("java.version"));

		if (javaVersion < 9) {

			Compilation compilation = compileForCurrentJdk(
					jdk8Generator.buildGeneratedClassFullName(), jdk8Generator.generateSource());
			assertThat(compilation).succeeded();

		} else {

			// Scenario 1:  Compile to the current JDK (version is for sure 9 or higher)
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, javaVersion, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForCurrentJdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());

				assertThat(compilation).succeeded();
			}

			// Scenario 2:  Compile to --release=8
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForRelease8Jdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());
				assertThat(compilation).succeeded();
			}

			// Scenario 3:  Compile to -source=8 -target=8
			{
				PropertyRegistrarClassGenerator currentJdkGenerator = new PropertyRegistrarClassGenerator(
						simpleCompileUnit(), AndHowCompileProcessor.class, runDate, 8, javaVersion);	// <-- Note Src & JDK version

				Compilation compilation = compileForTarget8Jdk(
						currentJdkGenerator.buildGeneratedClassFullName(), currentJdkGenerator.generateSource());
				assertThat(compilation).succeeded();
			}
		}
	}


	public static CompileUnit simpleCompileUnit() {
		
		CompileUnit cu = new CompileUnit(ROOT_QUAL_NAME);
		
		//root prop
		cu.addProperty(PROP1_NAME, true, true);
		cu.addProperty(PROP2_NAME, true, true);
		
		return cu;
	}
	
	public static CompileUnit simpleCompileUnit_DefaultPkg() {
		
		CompileUnit cu = new CompileUnit(ROOT_SIMPLE_NAME);
		
		//root prop
		cu.addProperty(PROP1_NAME, true, true);
		cu.addProperty(PROP2_NAME, true, true);
		
		return cu;
	}
	
	
	public static CompileUnit complexCompileUnit() {

		CompileUnit cu = new CompileUnit(ROOT_QUAL_NAME);
		
		//root prop
		cu.addProperty(PROP1_NAME, true, true);
		
		{
			//1st inner class
			cu.pushType(INNER1_SIMP_NAME, true);
			cu.addProperty(PROP1_NAME, true, true);

			{
				//2nd inner class
				cu.pushType(INNER2_SIMP_NAME, true);
				cu.addProperty(PROP1_NAME, true, true);
				cu.addProperty(PROP2_NAME, true, true);
				cu.popType();

			}

			//one more at 1st inner level
			cu.addProperty(PROP2_NAME, true, true);
			cu.popType();
		}
		
		return cu;
	}


	/**
	 * Determine the major version of the Java runtime based on a version string.
	 *
	 * <em>This method was part of CompileUtil, but the need for it has been written
	 * out.  It was used in this test class, so was just moved here.</em>
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


	/**
	 * Compile using the current JDK compiler w/ no source, release or target options.
	 * @param classFullName Full class name to generate
	 * @param classSourceCode Source code
	 * @return Compilation
	 */
	public static Compilation compileForCurrentJdk(String classFullName, String classSourceCode) {
		Compilation compilation =
				javac()
						.compile(JavaFileObjects.forSourceString(classFullName, classSourceCode));
		return compilation;
	}

	/**
	 * Compile using the current JDK compiler w/ the --release flag set to 8.
	 *
	 * When running on a JDK 9+, this places the JDK 8 libraries on the classpath.  Thus, code must
	 * ensure it refers to JDK 8 classes.
	 *
	 * @param classFullName Full class name to generate
	 * @param classSourceCode Source code
	 * @return Compilation
	 */
	public static Compilation compileForRelease8Jdk(String classFullName, String classSourceCode) {
		Compilation compilation =
				javac().withOptions("--release=8")
						.compile(JavaFileObjects.forSourceString(classFullName, classSourceCode));
		return compilation;
	}

	/**
	 * Compile using the current JDK compiler w/ -source and -target flags set to 8.
	 *
	 * When running on a JDK 9+, this means that the language constructs must be 8 and the generated
	 * byte code will be 8.  HOWEVER, the system libraries on the classpath will still be that of
	 * the current JDK.
	 *
	 * @param classFullName Full class name to generate
	 * @param classSourceCode Source code
	 * @return Compilation
	 */
	public static Compilation compileForTarget8Jdk(String classFullName, String classSourceCode) {
		Compilation compilation =
				javac().withOptions("-source", "8", "-target", "8") /* This is the only way to spec these */
						.compile(JavaFileObjects.forSourceString(classFullName, classSourceCode));
		return compilation;
	}


}
