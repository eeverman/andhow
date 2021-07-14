package com.bigcorp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeAllTests;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Lets say there is a very specific configuration state we need to test our app in.
 * We could do that with a separate properties file, but another way is to configure AndHow with
 * 'fixed values' right in a test.
 * <p>
 * The {@Code @KillAndHowBeforeAllTests} annotation resets AndHow before the start of testing and
 * restores it after all tests are complete.  The setup method uses
 * {@Code AndHow.findConfig()...build()} to initialize AndHow with several 'fixed values' for the
 * test scenario.  The AndHow state is not modified between tests, so all tests share the same
 * AndHow configuration state.
 */
@KillAndHowBeforeAllTests
public class CheckerFixedValuesTest {

	/**
	 * AndHow was 'killed' at the start of the test class, so this setup method can create a new
	 * AndHow configuration state for all the tests in this class.  When all the tests are complete,
	 * the original state is restored.
	 */
	@BeforeAll
  public static void setup() {
		AndHow.findConfig()
				.addFixedValue(Checker.Config.PROTOCOL, "https")
				.addFixedValue(Checker.Config.SERVER, "imgs.xkcd.com")
				.addFixedValue(Checker.Config.PORT, "80")
				.addFixedValue(Checker.Config.PATH, "/comics/the_mother_of_all_suspicious_files.png")
				.build();  //build() initializes AndHow, otherwise it would wait for the 1st Property access.
	}

	/**
	 * Verify the the app only sees the 'fixed' values from above.
	 */
	@Test
	public void verifyTestEnvironmentConfiguration() {
		Checker v = new Checker();

		String url = v.getServiceUrl();

		assertEquals("https://imgs.xkcd.com:80/comics/the_mother_of_all_suspicious_files.png", url,
				"The url is build based on the fixed values in the 'BeforeAll' method");
	}

	/**
	 * All the tests in this class will see the same AndHow configuration...
	 */
	@Test
	public void verifyTestEnvironmentConfiguration2() {
		assertEquals("imgs.xkcd.com", Checker.Config.SERVER.getValue());
	}

	/**
	 * Since the main method calls {@Code AndHow.findConfig()...build()}, forcing AndHow to
	 * initialize itself, we <em>must 'kill'</em> the AndHow configured state before calling main.
	 * Otherwise AndHow would throw a RuntimeException.
	 * <p>
	 * Resetting AndHow before this test means that this test will see the default configuration.
	 * <p>
	 * In production, its AndHow's job to enforce a single, stable configuration state and it complains
	 * loudly if application code tries to re-initialize it.  During testing, however, we need to
	 * 'break the rules' with things like {@Code @KillAndHowBeforeThisTest} and other AndHow test
	 * helpers.
	 * @throws Exception
	 */
	@KillAndHowBeforeThisTest
	@Test
	public void mainMethodAlsoSeesDefaultProperties() throws Exception {

		String text = tapSystemOut(() -> {
			Checker.main(new String[]{});
		});

		assertTrue(text.contains("https://default.bigcorp.com:80/validate"),
				"The url should match the configuration in checker.default.properties");
	}

}