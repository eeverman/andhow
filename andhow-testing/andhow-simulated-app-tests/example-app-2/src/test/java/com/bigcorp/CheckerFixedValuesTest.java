package com.bigcorp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeAllTests;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lets say there is a very specific configuration state we need to test our app in.
 * We could do that with a separate properties file, but another way is to configure AndHow with
 * 'fixed values' right in a test.
 * <p>
 * The {@code @KillAndHowBeforeAllTests} annotation resets AndHow before the start of testing and
 * restores it after all tests are complete.  The setup method uses
 * {@code AndHow.findConfig()...build()} to initialize AndHow with several 'fixed values' for the
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
				.addFixedValue(Checker.Config.PORT, 80)
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
	 * One of AndHow's main jobs is to enforce a single, stable configuration state and complain
	 * loudly (with an AppFatalException) if application code tries to change that state by
	 * re-initializing AndHow.  This test verifies that.
	 * <p>
	 * The main() method explicitly initializes AndHow and so does this test class.  Thus, the main
	 * method will throw a {@code AppFatalException} because AndHow detects the attempt to
	 * reinitialize it.
	 * <p>
	 * If we did want to call the main method here, @{Code @KillAndHowBeforeThisTest} could be added
	 * to this test method to start over with an unconfigured state for just this test.
	 * @throws Exception
	 */
	@Test
	public void mainMethodShouldThrowAnErrorBecauseAndHowIsAlreadyInitialized() throws Exception {

		assertThrows(AppFatalException.class, () ->
				Checker.main(new String[]{})
		);

	}

}
