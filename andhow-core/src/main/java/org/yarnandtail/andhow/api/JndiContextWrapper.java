package org.yarnandtail.andhow.api;

import javax.naming.Context;

/**
 * Simple wrapper around a JNDI Context and an Exception that might have been thrown while trying
 * to retrieve it.
 *
 * If the context is null, there may be an Exception - It is possible for both to be null.
 */
public interface JndiContextWrapper {
	public Context getContext();
	public Exception getException();
}
