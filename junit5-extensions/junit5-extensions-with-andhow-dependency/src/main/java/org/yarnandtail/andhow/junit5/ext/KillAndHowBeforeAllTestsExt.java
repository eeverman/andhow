package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.*;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

/**
 * JUnit Extension that can be placed on a <em>test class</em> to reset AndHow to its unconfigured
 * state (only) before the first test runs.  When all tests in the class are done, the
 * original AndHow configured state is restored, which may be unconfigured.
 * <p>
 * With this extension, all tests in the class share the same AndHow configuration, which
 * can be set in a {@code @BeforeAll} setup method.
 * <p>
 * <em>It is easier and safer to use the {@code @KillAndHowBeforeAllTests} annotation.</em>
 * That annotation uses this class and prevents this extension from being used on a test method
 * (this extension only works correctly on a test <em>class</em>).
 * <p>
 * <b>Usage example:</b>
 * <pre>{@code
 * @ExtendWith(KillAndHowBeforeAllTestsExtension.class)
 * public class MyJunit5TestClass {
 *
 *   @BeforeAll
 *   public static void configAndHowForAllTests(){
 * 		AndHow.findConfig()
 * 				.addFixedValue([AndHowProperty reference or name], [Value for that Property])
 * 				.addFixedValue(...);
 *    }
 *
 *   ...tests that will all share the same configuration...
 *
 * }
 * }</pre>
 */
public class KillAndHowBeforeAllTestsExt extends ExtensionBase
		implements BeforeAllCallback, AfterAllCallback {

	protected static final String CORE_KEY = "core_key";

	/**
	 * Store the state of AndHow before any test is run, then destroy the state
	 * so AndHow is unconfigured.
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		getPerTestClassStore(context).put(CORE_KEY, AndHowTestUtils.setAndHowCore(null));
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		Object core = getPerTestClassStore(context).remove(CORE_KEY, AndHowTestUtils.getAndHowCoreClass());
		AndHowTestUtils.setAndHowCore(core);
	}

}
