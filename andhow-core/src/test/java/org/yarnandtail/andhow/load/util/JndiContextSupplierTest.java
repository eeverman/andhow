package org.yarnandtail.andhow.load.util;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.JndiContextWrapper;
import org.yarnandtail.andhow.junit5.EnableJndiForThisTestMethod;

import javax.naming.NamingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.yarnandtail.andhow.load.util.JndiContextSupplier.DefaultJndiContextSupplier;
import static org.yarnandtail.andhow.load.util.JndiContextSupplier.EmptyJndiContextSupplier;

class JndiContextSupplierTest {

	@Test
	@EnableJndiForThisTestMethod
	public void defaultJndiContextSupplierShouldReturnJndiContextWhenAvailable() {
		DefaultJndiContextSupplier supplier = new DefaultJndiContextSupplier();
		assertNotNull(supplier.get().getContext());
		assertNull(supplier.get().getException());
	}

	@Test
	public void defaultJndiContextSupplierReturnsNamingExceptionIfNoJndiContext() {
		DefaultJndiContextSupplier supplier = new DefaultJndiContextSupplier();
		JndiContextWrapper wrap = supplier.get();

		assertNull(wrap.getContext());
		assertNotNull(wrap.getException());
		assertTrue(wrap.getException() instanceof NamingException);
	}

	@Test
	@EnableJndiForThisTestMethod
	public void emptyJndiContextSupplierShouldReturnAllNull() {
		EmptyJndiContextSupplier supplier = new EmptyJndiContextSupplier();
		assertNull(supplier.get().getContext());
		assertNull(supplier.get().getException());
	}

}