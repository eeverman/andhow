package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class ConfigFromFileExtUnitTest {

	//Store for state variables within the context
	ExtensionContext.Store store;

	//The context object that is passed to the test extension
	ExtensionContext extensionContext;


	@BeforeEach
	public void setUp() throws NoSuchMethodException {

		Method thisMethod = getClass().getMethod("setUp", null);

		//
		// Setup mockito for the test

		store = Mockito.mock(ExtensionContext.Store.class);

		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		Mockito.when(extensionContext.getRequiredTestInstance()).thenReturn(this);
		Mockito.when(extensionContext.getRequiredTestMethod()).thenReturn(thisMethod);
		Mockito.when(extensionContext.getStore(ArgumentMatchers.any())).thenReturn(store);
	}

	@AfterEach
	void tearDown() {
		AndHowTestUtils.setAndHowCore(null);
	}

	@Test
	public void expandPathShouldExpandRelativePaths() {
		ConfigFromFileBeforeAllExtSimple ext = new ConfigFromFileBeforeAllExtSimple();

		assertEquals("/org/yarnandtail/andhow/junit5/ext/myFile.props",
				ext.expandPath("myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/sub/myFile.props",
				ext.expandPath("sub/myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/../myFile.props",
				ext.expandPath("../myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/../sub/myFile.props",
				ext.expandPath("../sub/myFile.props", extensionContext));


		//Need to test a class at the root somehow (pkg is empty)
	}

	@Test
	public void expandPathShouldNotExpandAbsPaths() {
		ConfigFromFileBeforeAllExtSimple ext = new ConfigFromFileBeforeAllExtSimple();

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

	/** Is this really testing an inner class path?? */
	@Test
	public void expandPathShouldReturnPackageOfContainingClassForInnerClasses() {
		//Set mock test class to an inner class
		Mockito.when(extensionContext.getRequiredTestClass())
				.thenReturn((Class)(ConfigFromFileBeforeAllExtSimple.class));

		ConfigFromFileBeforeAllExtSimple ext = new ConfigFromFileBeforeAllExtSimple();

		assertEquals("/org/yarnandtail/andhow/junit5/ext/myFile.props",
				ext.expandPath("myFile.props", extensionContext));

		assertEquals("/org/yarnandtail/andhow/junit5/ext/subpkg/myFile.props",
				ext.expandPath("subpkg/myFile.props", extensionContext));
	}

	/* NOTE:  Testing building correct paths with the default package are handled
		 by a separate test in the default package. */

	/**
	 * This test scenario (non-null core and null inProcessConfig) is the case
	 * when AndHow has been initialized.
	 * @throws Exception
	 */
	@Test
	void completeWorkflowWithNonNullCoreAndNullConfig() throws Exception {

		Object andHowCoreCreatedDuringTest;

		/// Setup Mocks ///
		/// AndHow will be initialized (non-null core), the locator will be null
		assertNull(AndHowTestUtils.getAndHowCore(),
				"Just checking - no test should leave AndHow initialized");

		AndHowTestUtils.invokeAndHowInstance();	//force creation

		andHowCoreCreatedDuringTest = AndHowTestUtils.getAndHowCore();

		assertNotNull(andHowCoreCreatedDuringTest, "Should be non-null because we forced creation");

		//
		// Setup mockito for the test

		Mockito.when(store.remove(ArgumentMatchers.matches(
				ConfigFromFileBeforeAllExtSimple.getCoreKey()), ArgumentMatchers.any())
		).thenReturn(andHowCoreCreatedDuringTest);

		Mockito.when(store.remove(ArgumentMatchers.matches(
				ConfigFromFileBeforeAllExtSimple.getConfigKey()), ArgumentMatchers.any())
		).thenReturn(null);

		/// /// /// ///

		String cp = "MyPropFile.properties";

		ConfigFromFileBeforeAllExtSimple theExt = new ConfigFromFileBeforeAllExtSimple(cp, new Class<?>[] {this.getClass()});

		// The initial event called on extension by JUnit
		theExt.beforeAllOrEach(extensionContext);

		assertNull(AndHowTestUtils.getAndHowCore(),
				"Extension should have killed the core");

		assertSame(theExt.getCreatedConfig(), AndHow.findConfig());

		// TODO:  This test doesn't actually test that the created Core is as we want it.

		// The final event called on the extension by Junit
		theExt.afterAllOrEach(extensionContext);

		//
		// Verify the overall outcome
		assertEquals(andHowCoreCreatedDuringTest, AndHowTestUtils.getAndHowCore(),
				"Extension should have reinstated the same core instance created in setup");


		//
		// Verify actions on the store
		ArgumentCaptor<Object> keyForCorePut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForConfigPut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForCoreRemove = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForConfigRemove = ArgumentCaptor.forClass(Object.class);

		// In order, there should be 2x puts and 2x removes - can't be more specific on order than that.
		InOrder orderedStoreCalls = Mockito.inOrder(store);
		orderedStoreCalls.verify(store, times(2)).put(ArgumentMatchers.any(), ArgumentMatchers.any());
		orderedStoreCalls.verify(store, times(2)).remove(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verifyNoMoreInteractions(store);	//Really don't want any other interaction w/ the store


		// Now verify actual arguments to the store (can't assume in the order above)
		Mockito.verify(store).put(keyForCorePut.capture(), ArgumentMatchers.eq(andHowCoreCreatedDuringTest));
		Mockito.verify(store).put(keyForConfigPut.capture(), ArgumentMatchers.isNull());	// Initial config was null, so getting it back here
		Mockito.verify(store).remove(keyForCoreRemove.capture(), ArgumentMatchers.eq(AndHowTestUtils.getAndHowCoreClass()));
		Mockito.verify(store).remove(keyForConfigRemove.capture(), ArgumentMatchers.eq(AndHowConfiguration.class));

		assertEquals(keyForCorePut.getValue(), keyForCoreRemove.getValue(),
				"The keys used for put & remove the core should be the same");

		assertEquals(keyForConfigPut.getValue(), keyForConfigRemove.getValue(),
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
		//The expected namespace used to store values within the Extension
		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileBeforeAllExtSimple.class,
				(Class)(this.getClass()));
		assertEquals(expectedNamespace, namespace.getValue());

	}


	/**
	 * This scenario (null core and non-null config) is the case when findConfig() has been called
	 * but AndHow has not yet been initialized.
	 * @throws Exception
	 */
	@Test
	void completeWorkflowWithNullCoreAndNonNullConfig() throws Exception {

		/// Setup Mocks ///
		/// AndHow will be un-initialized (null core), the locator will be non-null
		assertNull(AndHowTestUtils.getAndHowCore(),
				"Just checking - no test should leave AndHow initialized");

		AndHowConfiguration<? extends AndHowConfiguration> existingConfig = AndHow.findConfig();
		assertNotNull(existingConfig);

		//
		// Setup mockito for the test
		AndHowConfiguration<? extends AndHowConfiguration> newConfig = StdConfig.instance();

		Mockito.when(store.remove(ArgumentMatchers.matches(
				ConfigFromFileBeforeAllExtSimple.getCoreKey()), ArgumentMatchers.any())
		).thenReturn(null);

		Mockito.when(store.remove(ArgumentMatchers.matches(
				ConfigFromFileBeforeAllExtSimple.getConfigKey()), ArgumentMatchers.any())
		).thenReturn(existingConfig);

		/// /// /// ///


		String cp = "MyPropFile.properties";

		ConfigFromFileBeforeEachExtSimple theExt = new ConfigFromFileBeforeEachExtSimple(cp, new Class<?>[] {this.getClass()});

		// The initial event called on extension by JUnit
		theExt.beforeAllOrEach(extensionContext);

		assertNull(AndHowTestUtils.getAndHowCore(),
				"Should still be uninitialized");

		assertSame(theExt.getCreatedConfig(), AndHow.findConfig());


		// The final event called on the extension by Junit
		theExt.afterAllOrEach(extensionContext);

		//
		// Verify the overall outcome
		assertNull(AndHowTestUtils.getAndHowCore(),
				"Extension should be null after the test");


		//
		// Verify actions on the store
		ArgumentCaptor<Object> keyForCorePut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForConfigPut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForCoreRemove = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForConfigRemove = ArgumentCaptor.forClass(Object.class);

		// In order, there should be 2x puts and 2x removes - can't be more specific on order than that.
		InOrder orderedStoreCalls = Mockito.inOrder(store);
		orderedStoreCalls.verify(store, times(2)).put(ArgumentMatchers.any(), ArgumentMatchers.any());
		orderedStoreCalls.verify(store, times(2)).remove(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verifyNoMoreInteractions(store);	//Really don't want any other interaction w/ the store


		// Now verify actual arguments to the store (can't assume in the order above)
		Mockito.verify(store).put(keyForCorePut.capture(), ArgumentMatchers.isNull());
		Mockito.verify(store).put(keyForConfigPut.capture(), ArgumentMatchers.eq(existingConfig));	// Initial config AndHow had
		Mockito.verify(store).remove(keyForCoreRemove.capture(), ArgumentMatchers.eq(AndHowTestUtils.getAndHowCoreClass()));
		Mockito.verify(store).remove(keyForConfigRemove.capture(), ArgumentMatchers.eq(AndHowConfiguration.class));

		assertEquals(keyForCorePut.getValue(), keyForCoreRemove.getValue(),
				"The keys used for put & remove the core should be the same");

		assertEquals(keyForConfigPut.getValue(), keyForConfigRemove.getValue(),
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
		ExtensionContext.Namespace expectedNamespace = ExtensionContext.Namespace.create(
				ConfigFromFileBeforeEachExtSimple.class,
				this, getClass().getMethod("setUp", null));
		assertEquals(expectedNamespace, namespace.getValue());

	}

	/* Simple subclass to test protected methods */
	public static class ConfigFromFileBeforeAllExtSimple extends ConfigFromFileBaseExt {

		// Only works in limited contexts where the full class is not being invoked
		public ConfigFromFileBeforeAllExtSimple() {
			super("");
		}

		public ConfigFromFileBeforeAllExtSimple(String classpathFile, Class<?>[] classesInScope) {
			super(classpathFile, classesInScope);
		}

		@Override
		protected String getFilePathFromAnnotation(final ExtensionContext context) { return null; }

		@Override
		protected Class<?>[] getClassesInScopeFromAnnotation(final ExtensionContext context) {
			return new Class[0];
		}

		public static String getCoreKey() {
			return ConfigFromFileBaseExt.CORE_KEY;
		}

		public static String getConfigKey() {
			return ConfigFromFileBaseExt.CONFIG_KEY;
		}

		public String expandPath(String classpath, ExtensionContext context) {
			return super.expandPath(classpath, context);
		}

		public AndHowConfiguration<? extends AndHowConfiguration> getCreatedConfig() {
			return _config;
		}


		@Override
		protected ExtensionType getExtensionType() {
			return ExtensionType.CONFIG_ALL_TESTS;
		}
	}

	public static class ConfigFromFileBeforeEachExtSimple extends ConfigFromFileBeforeAllExtSimple {

		public ConfigFromFileBeforeEachExtSimple() {
			super();
		}

		public ConfigFromFileBeforeEachExtSimple(String classpathFile, Class<?>[] classesInScope) {
			super(classpathFile, classesInScope);
		}

		@Override
		protected ExtensionType getExtensionType() {
			return ExtensionType.CONFIG_EACH_TEST;
		}
	}


}