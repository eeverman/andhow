package org.yarnandtail.andhow.load.util;

import javax.naming.Context;

/**
 * Simple wrapper around a JNDI Context and an Exception that might have been thrown while trying
 * to retrieve it.
 *
 * If the context is null, there may be an Exception - It is possible for both to be null.
 */
public class JndiContextWrapper {
	public Context context;
	public Exception exception;
}
