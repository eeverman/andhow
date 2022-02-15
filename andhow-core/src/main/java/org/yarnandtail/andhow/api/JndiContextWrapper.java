package org.yarnandtail.andhow.api;

import javax.naming.Context;

/**
 * Simple wrapper around a JNDI Context and an Exception that might have been thrown while trying
 * to retrieve it.
 *
 * If the context is null, there may be an Exception - It is possible for both to be null.
 */
public interface JndiContextWrapper {

	/**
	 * Get the JNDI Context.
	 *
	 * @return May be null.  If null, the getException() method may contain an exception
	 * encountered while attempting to retrieve the context, or verify that it is valid.
	 */
	public Context getContext();

	/**
	 * If getContext() returns null, may contain an Exception encountered while attempting
	 * to retrieve the context, or verify that it is valid.
	 * @return
	 */
	public Exception getException();
}
