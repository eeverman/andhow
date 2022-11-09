package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ExtensionBaseTest {

	SimpleExtensionBase extBase;

	ExtensionContext.Store store;

	//The context object that is passed to the test extension
	ExtensionContext extensionContext;

	@BeforeEach
	public void setUp() throws NoSuchMethodException {


		//
		// Setup mockito for the test

		extBase = new SimpleExtensionBase();

		store = Mockito.mock(ExtensionContext.Store.class);

		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		Mockito.when(extensionContext.getRequiredTestInstance()).thenReturn(this);
		Mockito.when(extensionContext.getRequiredTestMethod()).thenReturn(this.getClass().getMethod("setUp", null));
		Mockito.when(extensionContext.getStore(ArgumentMatchers.any())).thenReturn(store);
	}

	@Test
	void getPerTestClassStore() {
	}

	/**
	 * This test is VERIFYING THE WRONG BEHAVIOR!!
	 * There is a separate ticket to fix it: https://github.com/eeverman/andhow/issues/744
	 */
	@Test
	void getPerTestNamespace() {
		ExtensionContext.Namespace sut = extBase.getPerTestNamespace(extensionContext);

		ExtensionContext.Namespace ref = ExtensionContext.Namespace.create(
				SimpleExtensionBase.class, this.getClass()
		);

		assertEquals(ref, sut);
	}

	@Test
	void getPerTestMethodStore() {
	}

	@Test
	void getPerTestMethodNamespace() throws NoSuchMethodException {
		ExtensionContext.Namespace sut = extBase.getPerTestMethodNamespace(extensionContext);

		ExtensionContext.Namespace ref = ExtensionContext.Namespace.create(
				SimpleExtensionBase.class, this, this.getClass().getMethod("setUp", null)
		);

		assertEquals(ref, sut);
	}

	// Simple implementation to create a real subclass of ExtensionBase.
	static class SimpleExtensionBase extends ExtensionBase {

	}
}