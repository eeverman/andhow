package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ExtensionUtilTest {


	//The context object that is passed to the test extension
	ExtensionContext extensionContext;


	@BeforeEach
	public void setUp() throws NoSuchMethodException {

		Method thisMethod = getClass().getMethod("setUp", null);

		//
		// Setup mockito for the test

		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		Mockito.when(extensionContext.getRequiredTestInstance()).thenReturn(this);
		Mockito.when(extensionContext.getRequiredTestMethod()).thenReturn(thisMethod);
	}

	@Test
	void findAnnotation() {
	}

	@Test
	void isInnerClass() {
	}

	@Test
	void isExistingResource() {
	}

	@Test
	void stem() {
	}

	@Test
	void testFindAnnotation() {
	}


	@Test
	public void expandPathShouldExpandRelativePaths() {

		assertEquals("/org/yarnandtail/andhow/junit5/ext/myFile.props",
				ExtensionUtil.expandPath("myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/sub/myFile.props",
				ExtensionUtil.expandPath("sub/myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/../myFile.props",
				ExtensionUtil.expandPath("../myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/../sub/myFile.props",
				ExtensionUtil.expandPath("../sub/myFile.props", extensionContext));


		//Testing w/ no package (default pkg) is in a different test class
	}

	@Test
	public void expandPathShouldNotExpandAbsPaths() {

		assertEquals("/myFile.props",
				ExtensionUtil.expandPath("/myFile.props", extensionContext));

		assertEquals("/myFile",
				ExtensionUtil.expandPath("/myFile", extensionContext));

		assertEquals("/sub/myFile.props",
				ExtensionUtil.expandPath("/sub/myFile.props", extensionContext));

		assertEquals("/sub/myFile",
				ExtensionUtil.expandPath("/sub/myFile", extensionContext));

		//Testing w/ no package (default pkg) is in a different test class
	}

	@Test
	public void expandPathShouldUsePackageOfTopLevelClassForInnerClasses() {
		//Set mock test class to an inner class
		Mockito.when(extensionContext.getRequiredTestClass())
				.thenReturn((Class)(ConfigFromFileExtUnitTest.ConfigFromFileBeforeAllExtSimple.class));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/myFile.props",
				ExtensionUtil.expandPath("myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/subpkg/myFile.props",
				ExtensionUtil.expandPath("subpkg/myFile.props", extensionContext));
	}
}