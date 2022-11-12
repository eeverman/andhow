package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;

import java.util.Properties;

/**
 * JUnit 5 extension that restores System.Properties values after each test.
 *
 * System.Properties values are copied prior to any testing in the Test class.  Then, after each
 * test completes, the values are restored.
 */
public class RestoreSysPropsAfterEachTestExt extends ExtensionBase
		implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

	protected final static String KEY = "KEY";

	@Override
	public ExtensionType getExtensionType() {
		return ExtensionType.OTHER;
	}

	/**
	 * Store the original Sys Props prior to any testing.
	 *
	 * Happens before {@code @BeforeAll}, so this stores Sys Props as they were
	 * prior to any mods in {@code @BeforeAll}, i.e., these are sys Props prior to
	 * any affects of the test class.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		getPerTestClassStore(context).put(KEY, System.getProperties().clone());
	}

	/**
	 * Store the original Sys Props as they were prior to this test run.
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
	 * Restore Sys Props to their values as l
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

	/**
	 * One final restore after all tests are done, restoring to what the Sys Props were
	 * prior the the test class and any BeforeAll modifications.
	 *
	 * Happens after {@code @AfterAll}, so {@code @AfterAll} will see the state left
	 * by {@code @BeforeAll} unless it was modified by the test.
	 *
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		Properties p = getPerTestClassStore(context).remove(KEY, Properties.class);
		System.setProperties(p);
	}



}
