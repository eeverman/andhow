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


	/*
	AndHow only supports JDK8 and up and the main build (currently) must be done with
	JDK8.  Testing all the old release numbers is a bit ~weird~ but it ensures that
	newer JDKs have an ordinal number greater that 8 in the SourceVersion enum.
	Compiling w/ JDK8, it is not possible to directly test what the ordinal number
	for RELEASE_9 is, because it doesn't exist in this release!!
	 */
	@Test
	public void getMajorJavaVersionTest() {
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

}