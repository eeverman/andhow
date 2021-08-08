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
	 * Note:  If the Sys Props are modified prior to this, that modified state will be stored and
	 * then restored after.
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
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Properties p = getPerTestMethodStore(context).remove(KEY, Properties.class);
		System.setProperties(p);
	}

}
