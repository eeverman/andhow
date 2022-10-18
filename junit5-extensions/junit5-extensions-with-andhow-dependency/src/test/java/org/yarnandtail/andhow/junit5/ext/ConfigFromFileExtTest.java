package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.execution.NamespaceAwareStore;
import org.mockito.*;
import org.yarnandtail.andhow.AndHowConfiguration;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class ConfigFromFileExtTest {

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

		Mockito.when(store.remove(ArgumentMatchers.matches(
				ConfigFromFileExtSimple.getCoreKey()), ArgumentMatchers.any())
		).thenReturn(andHowCoreCreatedDuringTest);

		// NEED TO ALSO COVER CASES WHEN THIS IS NOT NULL (SAME FOR ABOVE BY THE REVERSE)
		Mockito.when(store.remove(ArgumentMatchers.matches(
				ConfigFromFileExtSimple.getLocatorKey()), ArgumentMatchers.any())
		).thenReturn(null);

		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		Mockito.when(extensionContext.getStore(ArgumentMatchers.any())).thenReturn(store);
	}

	@AfterEach
	void tearDown() {
		AndHowTestUtils.setAndHowCore(null);
	}

	@Test
	public void expandPathShouldExpandRelativePaths() {
		ConfigFromFileExtSimple ext = new ConfigFromFileExtSimple();

		assertEquals("/org/yarnandtail/andhow/junit5/ext/myFile.props",
				ext.expandPath("myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/sub/myFile.props",
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

	@Test
	public void expandPathShouldReturnPackageOfContainingClassForInnerClasses() {
		//Set mock test class to an inner class
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(ConfigFromFileExtSimple.class));

		ConfigFromFileExtSimple ext = new ConfigFromFileExtSimple();

		assertEquals("/org/yarnandtail/andhow/junit5/ext/myFile.props",
				ext.expandPath("myFile.props", extensionContext));
	}

	/* NOTE:  Testing building correct paths with the default package are handled
		 by a separate test in the default package. */

	@Test
	void completeWorkflow() throws Exception {

		String cp = "my_file.properties";

		ConfigFromFileExt theExt = new ConfigFromFileExt(cp);

		// The initial event called on extension by JUnit
		theExt.beforeAll(extensionContext);

		assertNull(AndHowTestUtils.getAndHowCore(),
				"Extension should have killed the core");

		// The final event called on the extension by Junit
		theExt.afterAll(extensionContext);

		//
		// Verify the overall outcome
		assertEquals(andHowCoreCreatedDuringTest, AndHowTestUtils.getAndHowCore(),
				"Extension should have reinstated the same core instance created in setup");


		//
		// Verify actions on the store
		ArgumentCaptor<Object> keyForCorePut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForLocatorPut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForCoreRemove = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForLocatorRemove = ArgumentCaptor.forClass(Object.class);

		// In order, there should be 2x puts and 2x removes - can't be more specific on order than that.
		InOrder orderedStoreCalls = Mockito.inOrder(store);
		orderedStoreCalls.verify(store, times(2)).put(ArgumentMatchers.any(), ArgumentMatchers.any());
		orderedStoreCalls.verify(store, times(2)).remove(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verifyNoMoreInteractions(store);	//Really don't want any other interaction w/ the store


		// Now verify actual arguments to the store (can't assume in the order above)
		Mockito.verify(store).put(keyForCorePut.capture(), ArgumentMatchers.eq(andHowCoreCreatedDuringTest));
		Mockito.verify(store).put(keyForLocatorPut.capture(), ArgumentMatchers.isNull());	// Initial locator was null, so getting it back here
		Mockito.verify(store).remove(keyForCoreRemove.capture(), ArgumentMatchers.eq(AndHowTestUtils.getAndHowCoreClass()));
		Mockito.verify(store).remove(keyForLocatorRemove.capture(), ArgumentMatchers.eq(UnaryOperator.class));

		assertEquals(keyForCorePut.getValue(), keyForCoreRemove.getValue(),
				"The keys used for put & remove the core should be the same");

		assertEquals(keyForLocatorPut.getValue(), keyForLocatorRemove.getValue(),
				"The keys used for put & remove the locator should be the same");

		//
		// Verify actions on the ExtensionContext
		ArgumentCaptor<ExtensionContext.Namespace> namespace =
				ArgumentCaptor.forClass(ExtensionContext.Namespace.class);
		
		Mockito.verify(extensionContext, Mockito.atLeast(2)).getStore(namespace.capture());

		//Verify the namespace used
		// The namespace is an implementation detail, but must include the Extension class and the
		// Tested class (this class in this case).  There isn't an easy way to test that minimum spec,
		// so here just check for a specific namespace.
		expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileExt.class,
				(Class)(this.getClass()));
		assertEquals(expectedNamespace, namespace.getValue());

	}

	/* Simple subclass to test protected methods */
	public static class ConfigFromFileExtSimple extends ConfigFromFileExt {
		public ConfigFromFileExtSimple() {
			super("");
		}

		public String expandPath(String classpath, ExtensionContext context) {
			return super.expandPath(classpath, context);
		}

		public static String getCoreKey() {
			return ConfigFromFileExt.CORE_KEY;
		}

		public static String getLocatorKey() {
			return ConfigFromFileExt.LOCATOR_KEY;
		}
	}


}