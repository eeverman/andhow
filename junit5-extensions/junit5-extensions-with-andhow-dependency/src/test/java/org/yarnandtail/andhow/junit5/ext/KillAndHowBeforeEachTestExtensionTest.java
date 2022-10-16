package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests of this JUnit Extension.
 * Beyond unit testing, the extension is used in many of the simulated app tests
 * (see that module for usage examples).
 */
class KillAndHowBeforeEachTestExtensionTest {

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
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		Mockito.when(extensionContext.getStore(ArgumentMatchers.any())).thenReturn(store);
	}

	@AfterEach
	void tearDown() {
		AndHowTestUtils.setAndHowCore(null);
	}

	@Test
	void completeWorkflow() throws Exception {

		KillAndHowBeforeEachTestExt theExt = new KillAndHowBeforeEachTestExt();

		// The initial event called on extension by JUnit
		theExt.beforeAll(extensionContext);

		assertNull(AndHowTestUtils.getAndHowCore(),
				"Extension should have killed the core");

		AndHowTestUtils.invokeAndHowInstance();	//force creation again!

		theExt.beforeEach(extensionContext);

		assertNull(AndHowTestUtils.getAndHowCore(),
				"Extension should have killed the core... again!");

		//Note:  after each is not implemented b/c its not needed

		// The final event called on the extension by Junit
		theExt.afterAll(extensionContext);

		//
		// Verify the overall outcome
		assertEquals(andHowCoreCreatedDuringTest, AndHowTestUtils.getAndHowCore(),
				"Extension should have reinstated the same core instance created in setup");


		//
		// Verify actions on the store
		ArgumentCaptor<Object> keyForPut = ArgumentCaptor.forClass(Object.class);
		ArgumentCaptor<Object> keyForRemove = ArgumentCaptor.forClass(Object.class);

		InOrder orderedStoreCalls = Mockito.inOrder(store);
		orderedStoreCalls.verify(store).put(keyForPut.capture(), ArgumentMatchers.eq(andHowCoreCreatedDuringTest));
		orderedStoreCalls.verify(store).remove(keyForRemove.capture(), ArgumentMatchers.eq(AndHowTestUtils.getAndHowCoreClass()));
		Mockito.verifyNoMoreInteractions(store);	//Really don't want any other interaction w/ the store

		assertEquals(keyForPut.getValue(), keyForRemove.getValue(),
				"The keys used for put & remove should be the same");

		//
		// Verify actions on the ExtensionContext
		ArgumentCaptor<ExtensionContext.Namespace> namespace =
				ArgumentCaptor.forClass(ExtensionContext.Namespace.class);

		//Each method is called once in beforeAll and afterAll
		Mockito.verify(extensionContext, Mockito.times(2)).getRequiredTestClass();	//Must be called to figure out the Test class
		Mockito.verify(extensionContext, Mockito.times(2)).getStore(namespace.capture());

		//Verify the namespace used
		// The namespace is an implementation detail, but must include the Extension class and the
		// Tested class (this class in this case).  There isn't an easy way to test that minimum spec,
		// so here just check for a specific namespace.
		expectedNamespace = ExtensionContext.Namespace.create(
				KillAndHowBeforeEachTestExt.class,
				(Class)(this.getClass()));
		assertEquals(expectedNamespace, namespace.getValue());
	}
}