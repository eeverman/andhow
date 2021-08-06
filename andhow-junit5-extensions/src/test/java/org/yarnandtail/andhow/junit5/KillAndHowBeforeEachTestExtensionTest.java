package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

		store = mock(ExtensionContext.Store.class);
		when(store.remove(any(), any())).thenReturn(andHowCoreCreatedDuringTest);

		extensionContext = mock(ExtensionContext.class);
		when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		when(extensionContext.getStore(any())).thenReturn(store);
	}

	@AfterEach
	void tearDown() {
		AndHowTestUtils.setAndHowCore(null);
	}

	@Test
	void completeWorkflow() throws Exception {

		KillAndHowBeforeEachTestExtension theExt = new KillAndHowBeforeEachTestExtension();

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

		InOrder orderedStoreCalls = inOrder(store);
		orderedStoreCalls.verify(store).put(keyForPut.capture(), eq(andHowCoreCreatedDuringTest));
		orderedStoreCalls.verify(store).remove(keyForRemove.capture(), eq(AndHowTestUtils.getAndHowCoreClass()));
		verifyNoMoreInteractions(store);	//Really don't want any other interaction w/ the store

		assertEquals(keyForPut.getValue(), keyForRemove.getValue(),
				"The keys used for put & remove should be the same");

		//
		// Verify actions on the ExtensionContext
		ArgumentCaptor<ExtensionContext.Namespace> namespace =
				ArgumentCaptor.forClass(ExtensionContext.Namespace.class);

		//Each method is called once in beforeAll and afterAll
		verify(extensionContext, times(2)).getRequiredTestClass();	//Must be called to figure out the Test class
		verify(extensionContext, times(2)).getStore(namespace.capture());

		//Verify the namespace used
		// The namespace is an implementation detail, but must include the Extension class and the
		// Tested class (this class in this case).  There isn't an easy way to test that minimum spec,
		// so here just check for a specific namespace.
		expectedNamespace = ExtensionContext.Namespace.create(
				KillAndHowBeforeEachTestExtension.class,
				(Class)(this.getClass()));
		assertEquals(expectedNamespace, namespace.getValue());
	}
}