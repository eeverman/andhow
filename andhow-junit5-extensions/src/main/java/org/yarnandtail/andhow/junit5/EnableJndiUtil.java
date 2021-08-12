package org.yarnandtail.andhow.junit5;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

/**
 * Utility for working with JNDI and the EnableJndiForThisXXX JUnit annotations
 */
public class EnableJndiUtil {

	/**
	 * Recursively create subcontexts and don't complain if parts of those contexts are already
	 * created.
	 *
	 * @param ctx The context to create the subconexts in
	 * @param contextName The name, using '/' to delimit, eg:  "java:comp/env/org/me"
	 * @throws NamingException If JNDI is not configured or available
	 */
	public static void createSubcontexts(Context ctx, String contextName)  throws NamingException {
		String[] names = contextName.split("/");

		Context current = ctx;
		for (String name : names) {
			try {
				current = current.createSubcontext(name);
			} catch (NameAlreadyBoundException e) {
				//thats ok - just keep going
			}
		}
	}
}
