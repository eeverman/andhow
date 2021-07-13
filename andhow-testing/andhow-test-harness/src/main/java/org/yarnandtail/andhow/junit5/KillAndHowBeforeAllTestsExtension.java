package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.yarnandtail.andhow.AndHowNonProductionUtil;
import org.yarnandtail.andhow.internal.AndHowCore;

/**
 * JUnit Extension that can be placed on a <em>test class</em> to reset AndHow to its unconfigured
 * state (only) before the first test runs.  When all tests in the class are done, the
 * original AndHow configured state is restored, which may be unconfigured.
 * <p>
 * With this extension, all tests in the class share the same AndHow configuration, which
 * can be set in a {@Code @BeforeAll} setup method.
 * <p>
 * <em>It is easier and safer to use the @{Code @KillAndHowBeforeAllTests} annotation.</em>
 * That annotation uses this class and prevents this extension from being used on a test method
 * (this extension only works correctly on a test <em>class</em>).
 * <p>
 * <b>Usage example:</b>
 * <pre>{@Code
 * @ExtendWith(KillAndHowBeforeAllTestsExtension.class)
 * public class MyJunit5TestClass {
 *
 *   @BeforeAll
 *   public static void configAndHowForAllTests(){
 * 		AndHow.findConfig()
 * 				.addFixedValue([AndHowProperty reference or name], [Value for that Property])
 * 				.addFixedValue(...)
 * 				.build();
 *    }
 *
 *   ...tests that will all share the same configuration...
 *
 * }
 * }</pre>
 */
public class KillAndHowBeforeAllTestsExtension implements BeforeAllCallback, AfterAllCallback {

	protected static final String CORE_KEY = "core_key";

	/**
	 * Store the state of AndHow before any test is run, then destroy the state
	 * so AndHow is unconfigured.
	 * @param extensionContext
	 * @throws Exception
	 */
	@Override
	public void beforeAll(ExtensionContext extensionContext) throws Exception {
		AndHowCore core = AndHowNonProductionUtil.getAndHowCore();
		getStore(extensionContext).put(CORE_KEY, core);
		AndHowNonProductionUtil.destroyAndHowCore();
	}

	@Override
	public void afterAll(ExtensionContext extensionContext) throws Exception {
		AndHowCore core = getStore(extensionContext).remove(CORE_KEY, AndHowCore.class);
		AndHowNonProductionUtil.setAndHowCore(core);
	}

	/**
	 * Create or return the unique storage space for this test class for this extension.
	 * @param context
	 * @return
	 */
	protected ExtensionContext.Store getStore(ExtensionContext context) {
		return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestClass()));
	}
}
