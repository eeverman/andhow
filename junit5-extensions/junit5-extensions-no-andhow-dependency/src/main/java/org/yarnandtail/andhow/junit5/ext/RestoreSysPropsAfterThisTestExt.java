package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;

import java.util.Properties;

/**
 * JUnit 5 extension that restores System.Properties values after a single test is complete.
 *
 * System.Properties values are copied prior to this single test, then restored after it.
 */
public class RestoreSysPropsAfterThisTestExt extends ExtensionBase
		implements BeforeEachCallback, AfterEachCallback {

	protected final static String KEY = "KEY";

	/**
	 * Store the Sys Props as they were prior to this test.
	 *
	 * Happens after {@code @BeforeAll} and before {@code @BeforeEach}, so this stores
	 * Sys Props as they were after possible mods in {@code @BeforeAll}.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		getPerTestMethodStore(context).put(KEY, System.getProperties().clone());
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
		Properties p = getPerTestMethodStore(context).remove(KEY, Properties.class);
		System.setProperties(p);
	}

}
