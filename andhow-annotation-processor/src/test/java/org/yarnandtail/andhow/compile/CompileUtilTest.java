package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompileUtilTest {

	@Test
	public void getGeneratedAnnotationClassNameTest() {
		assertEquals("javax.annotation.Generated", CompileUtil.getGeneratedAnnotationClassName(8));
		assertEquals("javax.annotation.processing.Generated", CompileUtil.getGeneratedAnnotationClassName(9));
	}

	@Test
	public void getMajorJavaVersionHappyTest() {

		assertEquals(8, CompileUtil.getMajorJavaVersion("1.8"));
		assertEquals(8, CompileUtil.getMajorJavaVersion("1.8.434-be"));
		assertEquals(8, CompileUtil.getMajorJavaVersion("1.8.0_152"));

		assertEquals(9, CompileUtil.getMajorJavaVersion("9"));
		assertEquals(9, CompileUtil.getMajorJavaVersion("9.0.1"));

		//Weird stuff still ok
		assertEquals(9, CompileUtil.getMajorJavaVersion("9-prerelease"));
		assertEquals(9, CompileUtil.getMajorJavaVersion("9_prerelease"));

		assertEquals(11, CompileUtil.getMajorJavaVersion("11"));
		assertEquals(11, CompileUtil.getMajorJavaVersion("11.0"));
		assertEquals(11, CompileUtil.getMajorJavaVersion("11.1"));
		assertEquals(11, CompileUtil.getMajorJavaVersion("11.0.1"));
		assertEquals(11, CompileUtil.getMajorJavaVersion("11.1-b53"));
	}

	@Test
	public void getMajorJavaVersionExceptionTest() {

		try {
			CompileUtil.getMajorJavaVersion("WeirdVersion");
			fail("This should have thrown an exception");
		} catch (Exception e) {
			//Expected
		}

	}
}