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
class KillAndHowBeforeThisTestExtensionTest {

	Object andHowCoreCreatedDuringTest;

	//The expected namespace used to store values within the Extension
	ExtensionContext.Namespace expectedNamespace;

	//Store for state variables within the context
	ExtensionContext.Store store;

	//The context object that is passed to the test extension
	ExtensionContext extensionContext;


	@BeforeEach
	void setUp() throws NoSuchMethodException {

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
		when(extensionContext.getRequiredTestInstance()).thenReturn(this);
		when(extensionContext.getRequiredTestMethod()).thenReturn(this.getClass().getMethod("completeWorkflow"));
		when(extensionContext.getStore(any())).thenReturn(store);
	}

	@AfterEach
	void tearDown() {
		AndHowTestUtils.setAndHowCore(null);
	}

	@Test
	public void completeWorkflow() throws Exception {

		KillAndHowBeforeThisTestExtension theExt = new KillAndHowBeforeThisTestExtension();

		// The initial event called on extension by JUnit
		theExt.beforeEach(extensionContext);

		assertNull(AndHowTestUtils.getAndHowCore(),
				"Extension should have killed the core");

		// The final event called on the extension by Junit
		theExt.afterEach(extensionContext);

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
		verify(extensionContext, times(2)).getRequiredTestInstance();	//Must be called to figure out the Test class
		verify(extensionContext, times(2)).getStore(namespace.capture());

		//Verify the namespace used
		// The namespace is an implementation detail, but must include the Extension class, the
		// instance of the test (this instance)*, and since this extension is specific to the actual
		// test method called, the Method of the test (this method).
		// There isn't an easy way to test that minimum spec, so here just check for a specific namespace.
		// * This extension is different from the other in needing the test instance rather than the
		// test class.  Since this extension stores values at the instance method level, it is unique
		// to that level rather than to the class level.
		expectedNamespace = ExtensionContext.Namespace.create(
				KillAndHowBeforeThisTestExtension.class,
				this,
				this.getClass().getMethod("completeWorkflow"));
		assertEquals(expectedNamespace, namespace.getValue());

	}


}