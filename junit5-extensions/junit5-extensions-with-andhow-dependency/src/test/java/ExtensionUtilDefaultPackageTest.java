import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.*;
import org.yarnandtail.andhow.junit5.ext.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Duplicates some tests from org.yarnandtail.andhow.junit5.ConfigFromFileExtTest,
 * but does it using a similated Test class that is in the default package.
 * In newer JDKs it is not possible to import a class in the default package,
 * so the only way to refer to such is a class is to also be in the default
 * package, thus this test class.
 */
class ExtensionUtilDefaultPackageTest {

	//The context object that is passed to the test extension
	ExtensionContext extensionContext;


	@BeforeEach
	void setUp() {

		//
		// Setup mockito for the test
		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(MyClassWithNoPackage.class));
	}

	@Test
	public void expandPathShouldExpandRelativePaths() {

		assertEquals("/myFile.props",
				ExtensionUtil.expandPath("myFile.props", extensionContext));

		assertEquals("/sub/myFile.props",
				ExtensionUtil.expandPath("sub/myFile.props", extensionContext));

		//Need to test a class at the root somehow (pkg is empty)
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

		//Need to test a class at the root somehow (pkg is empty)
	}

}