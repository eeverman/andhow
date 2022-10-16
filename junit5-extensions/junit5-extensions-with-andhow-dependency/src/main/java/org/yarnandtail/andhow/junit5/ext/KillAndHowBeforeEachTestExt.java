package org.yarnandtail.andhow.junit5.ext;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;


/**
 * JUnit Extension that can be placed on a <em>test class</em> to reset AndHow to its
 * unconfigured state before each test runs.  When all tests in the class are done, the
 * original AndHow configured state is restored, which may be unconfigured.
 * <p>
 * <em>It is easier and safer to use the {@code @KillAndHowBeforeEachTest} annotation.</em>
 * That annotation uses this class and prevents this extension from being used on a test method
 * (this extension only works correctly on a test <em>class</em>).
 * <p>
 * <b>Usage example:</b>
 * <pre>{@code
 * @ExtendWith(KillAndHowBeforeEachTestExtension.class)
 * public class MyJunit5TestClass {
 *
 *   @Test
 *   public void doATest(){
 * 		AndHow.findConfig()
 * 				.addFixedValue([AndHowProperty reference or name], [Value for that Property])
 * 				.addFixedValue(...);
 *
 * 		  ...	code for this test...
 *    }
 *
 *   ...other tests that can each configure AndHow...
 *
 * }
 * }</pre>
 */
public class KillAndHowBeforeEachTestExt extends KillAndHowBeforeAllTestsExt
		implements BeforeEachCallback {

	/**
	 * Destroy the AndHow state before each test so that each starts with AndHow unconfigured.
	 * @param context
	 * @throws Exception
	 */
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		AndHowTestUtils.setAndHowCore(null);
	}

}
