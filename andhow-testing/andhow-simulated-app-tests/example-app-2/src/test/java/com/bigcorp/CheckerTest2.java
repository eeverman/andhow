package com.bigcorp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeAllTestsExtension;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * In the test environment, the application is configured to use the 'FLOAT'
 * implementation, as configured in the {@Code checker.production.properties} on the TEST CLASSPATH.
 *
 * Resources on the test classpath override the main classpath, so this is a natural and useful
 * outcome.  Just like in production, AndHow will auto-discover its configuration during testing.
 * <p>
 * Using the test {@Code checker.production.properties} file, however, means that all the tests run with the
 * same configuration.  How do we test the application in other configurations?
 * See {@Code CalculatorTest2} for the answer...
 * <p>
 * ...and much more flexible and complex configuration is possible for production and testing -
 * be sure to look at other of the 'simulated-app-tests'.
 */
@ExtendWith(KillAndHowBeforeAllTestsExtension.class)
class CheckerTest2 {

	/**
	 * To test our app in a very specific configuration, we can override configuration from all
	 * other sources by initializing AndHow with fixed values.  The {@Code KillAndHowBeforeAllTests}
	 * annotation resets AndHow before the start of testing and restores it after all tests are
	 * complete, so all tests will share the configured state below.
	 */
	@BeforeAll
  public static void configAndHowForAllTests(){
		AndHow.findConfig()
				.addFixedValue(Checker.Config.PROTOCOL, "https")
				.addFixedValue(Checker.Config.SERVER, "imgs.xkcd.com")
				.addFixedValue(Checker.Config.PORT, "80")
				.addFixedValue(Checker.Config.PATH, "/comics/the_mother_of_all_suspicious_files.png")
				.build();
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
	 * Running this method again will use the same configuration.
	 */
	@Test
	public void verifyTestEnvironmentConfiguration2() {
		Checker v = new Checker();

		String url = v.getServiceUrl();

		assertEquals("https://imgs.xkcd.com:80/comics/the_mother_of_all_suspicious_files.png", url,
				"The url is build based on the fixed values in the 'BeforeAll' method");
	}

	/**
	 * Since the main method calls {@Code AndHow.findConfig()...build()}, forcing AndHow to build and
	 * initialize itself, we <em>must 'kill'</em> the AndHow configured state before calling main.
	 * Otherwise AndHow would throw a RuntimeException.
	 * <p></p>
	 * In production, its AndHow's job to enforce a single, stable configuration state and it complains
	 * loudly if application code tries to re-initialize it.  During testing, however, we need to
	 * 'break the rules' with things like {@Code @KillAndHowBeforeThisTest} and other AndHow test
	 * helpers.
	 * @throws Exception
	 */
	@KillAndHowBeforeThisTest
	//@Test
	public void mainMethodAlsoSeesDefaultProperties() throws Exception {

		String text = tapSystemOut(() -> {
			Checker.main(new String[]{});
		});

		assertTrue(text.contains("https://imgs.xkcd.com:80/comics/the_mother_of_all_suspicious_files.png"),
				"The url should match the configuration in checker.default.properties");
	}

	/**
	 * The main method includes {@Code AndHow.findConfig().setCmdLineArgs(args)...} to allow AndHow
	 * to process the main args.
	 * <p>
	 * This test shows how a key=value pair can be passed as an argument to main.  Looking at the
	 * code in {@Code AppInitiation}, the key matches the {@Code ANDHOW_CLASSPATH_FILE} Property we
	 * told AndHow to use to decide which file to read.  To make it easier to specify that Property,
	 * we added the alias 'AH_CLASSPATH' so we don't need to use the full class name of that Property.
	 * @throws Exception
	 */
	@KillAndHowBeforeThisTest
	@Test
	public void mainMethodWillAcceptConfigForFilePath() throws Exception {

		String text = tapSystemOut(() -> {
			//pass the alias name of ANDHOW_CLASSPATH_FILE and a value as a main arg
			Checker.main(new String[]{ "AH_CLASSPATH=/checker.test.properties"});
		});

		assertTrue(text.contains("http://localhost:8888/localValidate"),
				"The url should match the configuration in checker.test.properties");
	}
}