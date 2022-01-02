package org.yarnandtail.andhow.load.util;

import static org.yarnandtail.andhow.load.util.JndiContextSupplier.*;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.JndiContextWrapper;
import org.yarnandtail.andhow.junit5.EnableJndiForThisTestMethod;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.jupiter.api.Assertions.*;

class JndiContextSupplierTest {

	@Test
	@EnableJndiForThisTestMethod
	public void jndiContextWrapperWithContextHappyPath() throws NamingException {
		InitialContext jndi = new InitialContext();

		JndiContextWrapperWithContext wrap = new JndiContextWrapperWithContext(jndi);
		assertSame(jndi, wrap.getContext());
		assertNull(wrap.getException());
	}

	@Test
	public void jndiContextWrapperWithContextShouldAllowNullContext() {
		JndiContextWrapperWithContext wrap = new JndiContextWrapperWithContext(null);
		assertNull(wrap.getContext());
		assertNull(wrap.getException());
	}

	@Test
	public void jndiContextWrapperWithoutContextHappyPath() throws NamingException {
		Exception ex = new Exception();

		JndiContextWrapperWithoutContext wrap = new JndiContextWrapperWithoutContext(ex);
		assertNull(wrap.getContext());
		assertSame(ex, wrap.getException());
	}

	@Test
	public void jndiContextWrapperWithoutContextAllowsNullException() throws NamingException {
		JndiContextWrapperWithoutContext wrap = new JndiContextWrapperWithoutContext(null);
		assertNull(wrap.getContext());
		assertNull(wrap.getException());
	}

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
	public void noJndiContextSupplierShouldReturnAllNull() {
		NoJndiContextSupplier supplier = new NoJndiContextSupplier();
		assertNull(supplier.get().getContext());
		assertNull(supplier.get().getException());
	}

}