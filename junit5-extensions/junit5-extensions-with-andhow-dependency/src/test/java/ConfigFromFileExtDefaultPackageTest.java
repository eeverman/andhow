import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.*;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileExt;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Duplicates some tests from org.yarnandtail.andhow.junit5.ConfigFromFileExtTest,
 * but does it using a similated Test class that is in the default package.
 * In newer JDKs it is not possible to import a class in the default package,
 * so the only way to refer to such is a class is to also be in the default
 * package, thus this test class.
 */
class ConfigFromFileExtDefaultPackageTest {

	Object andHowCoreCreatedDuringTest;

	//The expected namespace used to store values within the Extension
	ExtensionContext.Namespace expectedNamespace;

	//Store for state variables within the context
	ExtensionContext.Store store;

	//The context object that is passed to the test extension
	ExtensionContext extensionContext;


	@BeforeEach
	void setUp() {

		// Setup AndHow for the test
		assertNull(AndHowTestUtils.getAndHowCore(),
				"Just checking - no test should leave AndHow initialized");

		AndHowTestUtils.invokeAndHowInstance();	//force creation

		andHowCoreCreatedDuringTest = AndHowTestUtils.getAndHowCore();

		assertNotNull(andHowCoreCreatedDuringTest, "Should be non-null because we forced creation");

		//
		// Setup mockito for the test

		store = Mockito.mock(ExtensionContext.Store.class);
		Mockito.when(store.remove(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(andHowCoreCreatedDuringTest);

		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(MyClassWithNoPackage.class));
		Mockito.when(extensionContext.getStore(ArgumentMatchers.any())).thenReturn(store);
	}

	@AfterEach
	void tearDown() {
		AndHowTestUtils.setAndHowCore(null);
	}

	@Test
	public void expandPathShouldExpandRelativePaths() {
		ConfigFromFileExtSimple ext = new ConfigFromFileExtSimple();

		assertEquals("/myFile.props",
				ext.expandPath("myFile.props", extensionContext));

		assertEquals("/sub/myFile.props",
				ext.expandPath("sub/myFile.props", extensionContext));

		//Need to test a class at the root somehow (pkg is empty)
	}

	@Test
	public void expandPathShouldNotExpandAbsPaths() {
		ConfigFromFileExtSimple ext = new ConfigFromFileExtSimple();

		assertEquals("/myFile.props",
				ext.expandPath("/myFile.props", extensionContext));

		assertEquals("/myFile",
				ext.expandPath("/myFile", extensionContext));

		assertEquals("/sub/myFile.props",
				ext.expandPath("/sub/myFile.props", extensionContext));

		assertEquals("/sub/myFile",
				ext.expandPath("/sub/myFile", extensionContext));

		//Need to test a class at the root somehow (pkg is empty)
	}

	/* Simple subclass to test protected methods */
	public static class ConfigFromFileExtSimple extends ConfigFromFileExt {
		public ConfigFromFileExtSimple() {
			super("");
		}

		public String expandPath(String classpath, ExtensionContext context) {
			return super.expandPath(classpath, context);
		}
	}

}