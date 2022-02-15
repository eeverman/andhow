package org.yarnandtail.andhow.load.util;

import org.yarnandtail.andhow.api.JndiContextWrapper;

import javax.naming.InitialContext;
import java.util.function.Supplier;

/**
 * Dedicated {@link java.util.function.Supplier} implementations to return a JndiContextWrapper
 * implementation.
 */
public abstract class JndiContextSupplier implements Supplier<JndiContextWrapper> {
	/**
	 * JndiContextWrapper Supplier that attempts to initialize a standard {@link InitialContext}.
	 *
	 * A new JndiContextWrapper instance is returned for each call to get.
	 */
	public static class DefaultJndiContextSupplier extends JndiContextSupplier {

		@Override
		public JndiContextWrapper get() {

			JndiContextWrapper wrap;

			try {
				InitialContext ctx = new InitialContext();  //Normally doesn't throw exception, even if no JNDI
				ctx.getEnvironment();  //Should throw error if JNDI is unavailable
				wrap = new JndiContextWrapperImpl(ctx);
			} catch (Exception e) {
				wrap = new JndiContextWrapperImpl(e);
			}

			return wrap;
		}
	}

	/**
	 * JndiContextWrapper Supplier that does not provide a Jndi Context
	 */
	public static class EmptyJndiContextSupplier extends JndiContextSupplier {
		@Override
		public JndiContextWrapper get() { return new JndiContextWrapperImpl(); }
	}

}
