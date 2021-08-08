package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;

import java.util.Properties;

/**
 * JUnit 5 extension that restores System.Properties values all the tests in a Test class have
 * completed.
 *
 * System.Properties values are copied prior to any testing in the Test class.  Then, after all
 * tests in the test class are complete, the values are restored.
 */
public class RestoreSysPropsAfterAllTestsExt extends ExtensionBase
		implements BeforeAllCallback, AfterAllCallback {

	private final static String KEY = "KEY";

	/**
	 * Store the original Sys Props prior to any testing
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		getPerTestClassStore(context).put(KEY, System.getProperties().clone());
	}

	/**
	 * Restore and remove the stored values.
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		Properties p = getPerTestClassStore(context).remove(KEY, Properties.class);
		System.setProperties(p);
	}

}
