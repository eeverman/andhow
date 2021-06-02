package org.yarnandtail.andhow.compile;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import java.util.*;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.Before;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrarClassGeneratorTest {
	
	private final String ROOT_SIMPLE_NAME = "MyClass";
	private final String ROOT_PKG = "org.big.comp";
	private final String ROOT_QUAL_NAME = ROOT_PKG + "." + ROOT_SIMPLE_NAME;
	private final String INNER1_SIMP_NAME = "Inner1";
	private final String INNER2_SIMP_NAME = "Inner2";
	
	private final String PROP1_NAME = "prop1";
	private final String PROP2_NAME = "prop2";
	
	private GregorianCalendar runDate;
	
	@Before
	public void initEach() {
		runDate = new GregorianCalendar();
		//runDate.setTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT-6")));
		runDate.set(2017, 8, 29, 14, 1, 1);
		runDate.set(Calendar.MILLISECOND, 999);
	}
	

	/**
	 * Test of getTemplate method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testGetTemplate() throws Exception {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		String template = gen.getTemplate();
		
		assertTrue(template.length() > 20);
	}
	
	/**
	 * Test of buildPackageString method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildPackageString() {
		
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		assertEquals("package " + ROOT_PKG + ";", gen.buildPackageString());
		
		gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate);
		assertEquals("", gen.buildPackageString());
	}

	/**
	 * Test of buildGeneratedClassSimpleName method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildGeneratedClassSimpleName() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		assertEquals("$" + ROOT_SIMPLE_NAME + "_AndHowProps", gen.buildGeneratedClassSimpleName());
		
		gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate);
		assertEquals("$" + ROOT_SIMPLE_NAME + "_AndHowProps", gen.buildGeneratedClassSimpleName());
	}
	
	@Test
	public void testBuildGeneratedClassFullName() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		assertEquals(ROOT_PKG + ".$" + ROOT_SIMPLE_NAME + "_AndHowProps", gen.buildGeneratedClassFullName());
		
		gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate);
		assertEquals("$" + ROOT_SIMPLE_NAME + "_AndHowProps", gen.buildGeneratedClassFullName());
	}

	/**
	 * Test of buildRunDateString method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildRunDateString() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		assertTrue(gen.buildRunDateString().startsWith("2017-09-29T14:01:01.999")); //Not checking the exact timezone b/c it is local dependent
		assertTrue(gen.buildRunDateString().matches("2017-09-29T14:01:01\\.999[+-][01]\\d{3}")); //Regex for the timezone
	}

	/**
	 * Test of buildRegistrationAddsString method, of class PropertyRegistrarClassGenerator.
	 */
	@Test
	public void testBuildRegistrationAddsString_DefaultPkg() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate);
		String adds = gen.buildRegistrationAddsString();
		String eachAdds[] = adds.trim().split("[\\t\\n\\r\\f]+");
		assertEquals(2, eachAdds.length);
		assertEquals("list.add(\"" + PROP1_NAME + "\");", eachAdds[0]);
		assertEquals("list.add(\"" + PROP2_NAME + "\");", eachAdds[1]);
	}
	
	@Test
	public void testBuildRegistrationAddsString_WithPkg() {

		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		String adds = gen.buildRegistrationAddsString();
		
		//Should all be the same as for default pkg
		String eachAdds[] = adds.trim().split("[\\t\\n\\r\\f]+");
		assertEquals(2, eachAdds.length);
		assertEquals("list.add(\"" + PROP1_NAME + "\");", eachAdds[0]);
		assertEquals("list.add(\"" + PROP2_NAME + "\");", eachAdds[1]);
	}
	
	@Test
	public void testBuildRegistrationAddsString_Complex() {
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				complexCompileUnit(), AndHowCompileProcessor.class, runDate);
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
		
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit(), AndHowCompileProcessor.class, runDate);
		String sourceStr = gen.generateSource();
		
		Compilation compilation
				= javac()
						.compile(JavaFileObjects.forSourceString(gen.buildGeneratedClassFullName(), sourceStr));
		
		assertThat(compilation).succeeded();

		//Check the source file

		//This method is separately tested, so lets trust it works correctly
		int javaVersion = CompileUtil.getMajorJavaVersion(System.getProperty("java.version"));

		if (javaVersion < 9) {
			assertTrue(sourceStr.contains("@javax.annotation.Generated("));
		} else {
			assertTrue(sourceStr.contains("@javax.annotation.processing.Generated("));
		}

	}
	
	/**
	 * Basic gross test that the generated source is compilable
	 */
	@Test
	public void testGenerate_simpleCompileUnit_DefaultPkg() throws Exception {
		
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				simpleCompileUnit_DefaultPkg(), AndHowCompileProcessor.class, runDate);
		String sourceStr = gen.generateSource();
		
		Compilation compilation
				= javac()
						.compile(JavaFileObjects.forSourceString(gen.buildGeneratedClassFullName(), sourceStr));
		
		assertThat(compilation).succeeded();

	}

	/**
	 * Basic gross test that the generated source is compilable
	 */
	@Test
	public void testGenerate_complex() throws Exception {
		
		PropertyRegistrarClassGenerator gen = new PropertyRegistrarClassGenerator(
				complexCompileUnit(), AndHowCompileProcessor.class, runDate);
		String sourceStr = gen.generateSource();
		
		Compilation compilation
				= javac()
						.compile(JavaFileObjects.forSourceString(gen.buildGeneratedClassFullName(), sourceStr));
		
		assertThat(compilation).succeeded();

	}
	
	
	public CompileUnit simpleCompileUnit() {
		
		CompileUnit cu = new CompileUnit(ROOT_QUAL_NAME);
		
		//root prop
		cu.addProperty(PROP1_NAME, true, true);
		cu.addProperty(PROP2_NAME, true, true);
		
		return cu;
	}
	
	public CompileUnit simpleCompileUnit_DefaultPkg() {
		
		CompileUnit cu = new CompileUnit(ROOT_SIMPLE_NAME);
		
		//root prop
		cu.addProperty(PROP1_NAME, true, true);
		cu.addProperty(PROP2_NAME, true, true);
		
		return cu;
	}
	
	
	public CompileUnit complexCompileUnit() {
		
		
		
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


}
