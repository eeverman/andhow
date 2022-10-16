package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.extension.*;

/**
 * Same functionality as the class in the junit5 extension module, however, that
 * module depends on andhow-core, so it cannot be used in andhow-core.
 *
 * This condensed version of the class collapses the inheritance model to be a single class.
 */
public class KillAndHowBeforeThisTestExt implements BeforeEachCallback, AfterEachCallback {

	public static final String CORE_KEY = "core_key";

	/**
	 * Store the state of AndHow before this test, then destroy the state so AndHow is unconfigured.
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		getPerTestMethodStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));
	}

	/**
	 * Restore the state of AndHow to what it was before this test.
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		Object core = getPerTestMethodStore(context).remove(CORE_KEY, AndHowTestUtils.getAndHowCoreClass());
		AndHowTestUtils.setAndHowCore(core);
	}


	/**
	 * Create or return a unique storage space, which is unique per the test method.
	 *
	 * More specifically, the store is unique per the combination of:
	 * <ul>
	 *  <li>{@code this} extension class</li>
	 *  <li>The test class instance that is invoking this extension</li>
	 *  <li>The test method that is associated with this call
	 *  	(i.e, the test method associated with a BeforeEachCallback or AfterEachCallback)</li>
	 * </ul>
	 *
	 * This method cannot be called for a non-test method related callback because
	 * {@code context.getRequiredTestMethod} will be null and will throw an exception.
	 *
	 * @param context  The ExtensionContext passed in to one of the callback methods.
	 * @return The store that can be used to store and retrieve values.
	 */
	protected ExtensionContext.Store getPerTestMethodStore(ExtensionContext context) {
		return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestInstance(), context.getRequiredTestMethod()));
	}
}
