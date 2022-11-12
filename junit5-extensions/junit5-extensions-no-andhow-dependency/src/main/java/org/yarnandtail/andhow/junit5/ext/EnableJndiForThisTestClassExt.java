package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;

import javax.naming.InitialContext;
import java.util.Properties;

/**
 * JUnit 5 extension that enables JNDI for use in a test class.
 * <p>
 * <em>It is easier and safer to use the {@code @EnableJndiForThisTestClass} annotation.</em>
 * That annotation uses this class and prevents this extension from being used on a test method
 * (this extension only works correctly on a test <em>class</em>).
 */
public class EnableJndiForThisTestClassExt extends ExtensionBase
		implements BeforeAllCallback, AfterAllCallback {

	protected final static String KEY = "KEY";

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.MODIFY_ENV_ALL_TESTS;
	}

	/**
	 * Enable JNDI for use in an individual test method.
	 *
	 * Happens before {@code @BeforeAll}, so JNDI values can be bound in BeforeAll.
	 *
	 * System Properties are modified in this beforeAll method and are restored in the afterAll
	 * callback.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {

		//
		// Store sys props as they were before this method
		getStore(context).put(KEY, System.getProperties().clone());

		System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleJndiContextFactory");
		System.setProperty("org.osjava.sj.delimiter", "/");
		System.setProperty("org.osjava.sj.jndi.shared", "true");
		System.setProperty("org.osjava.sj.jndi.ignoreClose", "true");

	}

	/**
	 * Close JNDI and remove related system properties after all tests have completed in this test class.
	 *
	 * Happens after {@code @AfterAll}.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void afterAll(ExtensionContext context) throws Exception {

		System.clearProperty("org.osjava.sj.jndi.ignoreClose"); //close() will now kill JNDI
		InitialContext ctx = new InitialContext();
		ctx.close();	//Its all gone

		//
		// Restore Sys Props

		Properties p = getStore(context).remove(KEY, Properties.class);
		System.setProperties(p);
	}

}
