package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ExtensionBaseTest {

	ExtensionBase extBase;

	ExtensionContext.Store store;

	//The context object that is passed to the test extension
	ExtensionContext extensionContext;

	ExtensionContext.Namespace testInstanceNamespace;

	ExtensionContext.Namespace testMethodNamespace;

	@BeforeEach
	public void setUp() throws NoSuchMethodException {

		testInstanceNamespace = ExtensionContext.Namespace.create(
				ExtensionBaseOTHERType.class, this.getClass()
		);

		testMethodNamespace = ExtensionContext.Namespace.create(
				ExtensionBaseOTHERType.class, this, this.getClass().getMethod("setUp", null)
		);

		//
		// Setup mockito for the test

		extBase = new ExtensionBaseOTHERType();

		store = Mockito.mock(ExtensionContext.Store.class);

		extensionContext = Mockito.mock(ExtensionContext.class);
		Mockito.when(extensionContext.getRequiredTestClass()).thenReturn((Class)(this.getClass()));
		Mockito.when(extensionContext.getRequiredTestInstance()).thenReturn(this);
		Mockito.when(extensionContext.getRequiredTestMethod()).thenReturn(this.getClass().getMethod("setUp", null));
		Mockito.when(extensionContext.getStore(ArgumentMatchers.any())).thenReturn(store);
	}

	@Test
	void getPerTestClassStore() {
		ExtensionContext.Store myStore = extBase.getPerTestClassStore(extensionContext);
		assertNotNull(myStore);
		Mockito.verify(extensionContext).getStore(ArgumentMatchers.eq(testInstanceNamespace));
	}

	/**
	 * This test is VERIFYING THE WRONG BEHAVIOR!!
	 * There is a separate ticket to fix it: https://github.com/eeverman/andhow/issues/744
	 */
	@Test
	void getPerTestNamespace() {
		ExtensionContext.Namespace sut = extBase.getPerTestNamespace(extensionContext);

		assertEquals(testInstanceNamespace, sut);
	}

	@Test
	void getPerTestMethodStore() {
		ExtensionContext.Store myStore = extBase.getPerTestMethodStore(extensionContext);
		assertNotNull(myStore);
		Mockito.verify(extensionContext).getStore(ArgumentMatchers.eq(testMethodNamespace));
	}

	@Test
	void getPerTestMethodNamespace() throws NoSuchMethodException {
		ExtensionContext.Namespace sut = extBase.getPerTestMethodNamespace(extensionContext);

		assertEquals(testMethodNamespace, sut);
	}

	@Test
	void getStoreForOTHERTypeShouldThrowException() {
		extBase = new ExtensionBaseOTHERType();

		assertThrows(IllegalStateException.class, () -> extBase.getStore(extensionContext));
	}

	@Test
	void getStoreForALLTypeShouldUseClassLevelStorage() {

		testInstanceNamespace = ExtensionContext.Namespace.create(
				ExtensionBaseALLType.class, this.getClass()
		);

		extBase = new ExtensionBaseALLType();
		ExtensionContext.Store myStore = extBase.getStore(extensionContext);
		assertNotNull(myStore);
		Mockito.verify(extensionContext).getStore(ArgumentMatchers.eq(testInstanceNamespace));
	}

	@Test
	void getStoreForEACHTypeShouldUseMethodLevelStorage() throws NoSuchMethodException {

		testMethodNamespace = ExtensionContext.Namespace.create(
				ExtensionBaseEACHType.class, this, this.getClass().getMethod("setUp", null)
		);

		extBase = new ExtensionBaseEACHType();
		ExtensionContext.Store myStore = extBase.getStore(extensionContext);
		assertNotNull(myStore);
		Mockito.verify(extensionContext).getStore(ArgumentMatchers.eq(testMethodNamespace));
	}

	@Test
	void getStoreForTHISTypeShouldUseClassLevelStorage() throws NoSuchMethodException {

		testMethodNamespace = ExtensionContext.Namespace.create(
				ExtensionBaseTHISType.class, this, this.getClass().getMethod("setUp", null)
		);

		extBase = new ExtensionBaseTHISType();
		ExtensionContext.Store myStore = extBase.getStore(extensionContext);
		assertNotNull(myStore);
		Mockito.verify(extensionContext).getStore(ArgumentMatchers.eq(testMethodNamespace));
	}

	// Simple implementation to create a real subclass of ExtensionBase.
	// It returns an 'OTHER' type, so the getStore() method will throw an Exception
	static class ExtensionBaseOTHERType extends ExtensionBase {
		@Override
		public ExtensionType getExtensionType() {
			return ExtensionType.OTHER;
		}
	}

	// Simple implementation to create a real subclass of ExtensionBase.
	// It returns an 'ALL' type, so getStore() should return test-level storage
	static class ExtensionBaseALLType extends ExtensionBase {
		@Override
		public ExtensionType getExtensionType() {
			return ExtensionType.OTHER_ALL_TESTS;
		}
	}

	// Simple implementation to create a real subclass of ExtensionBase.
	// It returns a 'THIS' type, so getStore() should return method-level storage
	static class ExtensionBaseEACHType extends ExtensionBase {
		@Override
		public ExtensionType getExtensionType() {
			return ExtensionType.OTHER_EACH_TEST;
		}
	}

	// Simple implementation to create a real subclass of ExtensionBase.
	// It returns a 'THIS' type, so getStore() should return method-level storage
	static class ExtensionBaseTHISType extends ExtensionBase {
		@Override
		public ExtensionType getExtensionType() {
			return ExtensionType.OTHER_THIS_TEST;
		}
	}
}