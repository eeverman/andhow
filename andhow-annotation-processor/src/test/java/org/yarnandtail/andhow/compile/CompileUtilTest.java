package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.Test;

import javax.lang.model.SourceVersion;

import static org.junit.jupiter.api.Assertions.*;

public class CompileUtilTest {

	@Test
	public void getGeneratedAnnotationClassNameTestForVersionNumbers() {
		assertEquals("javax.annotation.Generated",
				CompileUtil.getGeneratedAnnotationClassName(8));
		assertEquals("javax.annotation.processing.Generated",
				CompileUtil.getGeneratedAnnotationClassName(9));

		// Never happens, but it should be the old class
		assertEquals("javax.annotation.Generated",
				CompileUtil.getGeneratedAnnotationClassName(7));
	}

	@Test
	public void isGeneratedVersionDeterministicTest() {
		// All these are OK
		assertTrue(CompileUtil.isGeneratedVersionDeterministic(9, 9));
		assertTrue(CompileUtil.isGeneratedVersionDeterministic(9, 16));
		assertTrue(CompileUtil.isGeneratedVersionDeterministic(8, 8));

		// Shouldn't happen b/c JDK 7 isn't allowed, but the logic is the same
		assertTrue(CompileUtil.isGeneratedVersionDeterministic(7, 8));

		// Not deterministic
		assertFalse(CompileUtil.isGeneratedVersionDeterministic(8, 9));
		assertFalse(CompileUtil.isGeneratedVersionDeterministic(7, 16));
	}


	/*
	AndHow only supports JDK8 and up and the main build (currently) must be done with
	JDK8.  Testing all the old release numbers is a bit ~weird~ but it ensures that
	newer JDKs have an ordinal number greater that 8 in the SourceVersion enum.
	Compiling w/ JDK8, it is not possible to directly test what the ordinal number
	for RELEASE_9 is, because it doesn't exist in this release!!
	 */
	@Test
	public void getMajorJavaVersionFromSourceVersionEnumTest() {
		assertEquals(0, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_0));
		assertEquals(1, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_1));
		assertEquals(2, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_2));
		assertEquals(3, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_3));
		assertEquals(4, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_4));
		assertEquals(5, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_5));
		assertEquals(6, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_6));
		assertEquals(7, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_7));
		assertEquals(8, CompileUtil.getMajorJavaVersion(SourceVersion.RELEASE_8));
	}

	@Test
	public void getMajorJavaVersionFromCurrentJvmSystemProperty() {

		// This method will get its own testing...
		int currentMajorVersion = CompileUtil.getMajorJavaVersion(System.getProperty("java.version"));

		assertEquals(currentMajorVersion, CompileUtil.getMajorJavaVersion());
	}

	@Test
	public void getMajorJavaVersionFromString() {

		// These were real strings taken from common JDKs
		assertEquals(16, CompileUtil.getMajorJavaVersion("16.0.1"));
		assertEquals(15, CompileUtil.getMajorJavaVersion("15.0.2"));
		assertEquals(11, CompileUtil.getMajorJavaVersion("11"));
		assertEquals(11, CompileUtil.getMajorJavaVersion("11.0.11"));
		assertEquals(8, CompileUtil.getMajorJavaVersion("1.8.0_152"));

		// Hypothetical ones
		assertEquals(16, CompileUtil.getMajorJavaVersion("16.0.1_pre"));
		assertEquals(16, CompileUtil.getMajorJavaVersion("16.0.1-alpha"));
		assertEquals(16, CompileUtil.getMajorJavaVersion("16.0.1.4.5.2"));
		assertEquals(4, CompileUtil.getMajorJavaVersion("1.4.0_03-ea-b01"));
		assertEquals(3, CompileUtil.getMajorJavaVersion("1.3.1_05-ea"));

		// Errors ones
		RuntimeException e = assertThrows(RuntimeException.class, () -> CompileUtil.getMajorJavaVersion("a1.3.1_05-ea"));
		assertTrue(e.getCause() instanceof NumberFormatException);
		assertThrows(RuntimeException.class, () -> CompileUtil.getMajorJavaVersion("1.a3.1_05-ea"));
	}

}