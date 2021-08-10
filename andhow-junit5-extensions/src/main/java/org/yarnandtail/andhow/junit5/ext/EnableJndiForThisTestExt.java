package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.naming.InitialContext;
import java.util.Properties;

/**
 * JUnit 5 extension that enables JNDI for use in an individual test method.
 *
 */
public class EnableJndiForThisTestExt extends ExtensionBase
		implements BeforeEachCallback, AfterEachCallback {

	protected final static String KEY = "KEY";

	/**
	 * Enable JNDI for use in an individual test method.
	 *
	 * Happens after {@code @BeforeAll} and before {@code @BeforeEach}, however, JNDI will only be
	 * enabled in BeforeEach during the invocation prior to test methods annotated with this
	 * extension.
	 *
	 * System Properties are modified in this beforeEach method and are restored in the afterEach
	 * callback.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {

		//
		// Store sys props as they were before this method
		getPerTestMethodStore(context).put(KEY, System.getProperties().clone());

		System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleJndiContextFactory");
		System.setProperty("org.osjava.sj.jndi.shared", "true");
		System.setProperty("org.osjava.sj.jndi.ignoreClose", "true");

	}

	/**
	 * Restore after this test method and remove from the store.
	 *
	 * Happens before {@code @AfterEach}, so {@code @AfterEach} will see the state left
	 * by {@code @BeforeEach} unless it was modified by the test.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void afterEach(ExtensionContext context) throws Exception {

		System.clearProperty("org.osjava.sj.jndi.ignoreClose"); //close() will now kill JNDI
		InitialContext ctx = new InitialContext();
		ctx.close();	//Its all gone

		//
		// Restore Sys Props

		Properties p = getPerTestMethodStore(context).remove(KEY, Properties.class);
		System.setProperties(p);
	}

}
