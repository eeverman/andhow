package org.yarnandtail.andhow.load.util;

import org.yarnandtail.andhow.api.JndiContextWrapper;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.function.Supplier;

/**
 * Dedicated {@link java.util.function.Supplier} implementations to return a JndiContextWrapper
 * implementation.
 */
public class JndiContextSupplier {
	/**
	 * JndiContextWrapper Supplier that attempts to initialize a standard {@link InitialContext}.
	 */
	public static class DefaultJndiContextSupplier implements Supplier<JndiContextWrapper> {

		@Override
		public JndiContextWrapper get() {

			JndiContextWrapper wrap;

			try {
				InitialContext ctx = new InitialContext();  //Normally doesn't throw exception, even if no JNDI
				ctx.getEnvironment();  //Should throw error if JNDI is unavailable
				wrap = new JndiContextWrapperWithContext(ctx);
			} catch (Exception e) {
				wrap = new JndiContextWrapperWithoutContext(e);
			}

			return wrap;
		}
	}

	/**
	 * JndiContextWrapper Supplier that does not provide a Jndi Context
	 */
	public static class NoJndiContextSupplier implements Supplier<JndiContextWrapper> {
		@Override
		public JndiContextWrapper get() { return new JndiContextWrapperWithoutContext(null); }
	}

	/**
	 * JndiContextWrapper which may have a non-null JNDI context, but no exception.
	 */
	public static class JndiContextWrapperWithContext implements JndiContextWrapper {
		public Context _context;

		public JndiContextWrapperWithContext(final Context context) {
			_context = context;
		}

		@Override
		public Context getContext() {
			return _context;
		}

		@Override
		public Exception getException() {
			return null;
		}
	}

	/**
	 * JndiContextWrapper which has no JNDI context but may have an exception.
	 */
	public static class JndiContextWrapperWithoutContext implements JndiContextWrapper {
		public Exception _exception;

		public JndiContextWrapperWithoutContext(final Exception exception) {
			_exception = exception;
		}

		@Override
		public Context getContext() {
			return null;
		}

		@Override
		public Exception getException() {
			return _exception;
		}
	}
}
