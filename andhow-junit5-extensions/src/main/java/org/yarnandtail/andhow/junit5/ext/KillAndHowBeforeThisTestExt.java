package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

/**
 * JUnit Extension that can be placed on an <em>individual test method</em> to reset AndHow to its
 * unconfigured state before the test runs.  When the test is done, the original AndHow configured
 * state is restored, which may be unconfigured.
 * <p>
 * <em>It is easier and safer to use the {@code @KillAndHowBeforeThisTest} annotation.</em>
 * That annotation uses this class and prevents this extension from being used on a test class
 * (this extension only works correctly on a test <em>method</em>).
 * <p>
 * <b>Usage example:</b>
 * <pre>{@code
 * public class MyJunit5TestClass {
 *
 *   @Test
 *   @ExtendWith(KillAndHowBeforeThisTestExtension.class)
 *   public void aTest() { .... }
 *
 * }
 * }</pre>
 */
public class KillAndHowBeforeThisTestExt extends ExtensionBase
		implements BeforeEachCallback, AfterEachCallback {

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

}
