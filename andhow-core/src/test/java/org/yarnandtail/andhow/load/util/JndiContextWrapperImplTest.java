package org.yarnandtail.andhow.load.util;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.EnableJndiForThisTestMethod;

import javax.naming.*;

import static org.junit.jupiter.api.Assertions.*;

class JndiContextWrapperImplTest {


	@Test
	@EnableJndiForThisTestMethod
	public void jndiContextWrapperImplHappyPath() throws NamingException {
		InitialContext jndi = new InitialContext();

		JndiContextWrapperImpl wrap = new JndiContextWrapperImpl(jndi);
		assertSame(jndi, wrap.getContext());
		assertNull(wrap.getException());
	}

	@Test
	public void jndiContextWrapperImplShouldAllowNullContext() {
		JndiContextWrapperImpl wrap = new JndiContextWrapperImpl((Context)null);
		assertNull(wrap.getContext());
		assertNull(wrap.getException());
	}

	@Test
	public void jndiContextWrapperImplHappyWithException() throws NamingException {
		Exception ex = new Exception();

		JndiContextWrapperImpl wrap = new JndiContextWrapperImpl(ex);
		assertNull(wrap.getContext());
		assertSame(ex, wrap.getException());
	}

	@Test
	public void jndiContextWrapperImplAllowsNullException() throws NamingException {
		JndiContextWrapperImpl wrap = new JndiContextWrapperImpl((Exception)null);
		assertNull(wrap.getContext());
		assertNull(wrap.getException());
	}

	@Test
	public void jndiContextWrapperImplAllowsEmptyConstructor() throws NamingException {
		JndiContextWrapperImpl wrap = new JndiContextWrapperImpl();
		assertNull(wrap.getContext());
		assertNull(wrap.getException());
	}

}