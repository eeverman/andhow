package com.bigcorp;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;

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
class CheckerTest1 {

	/**
	 * Verify the the app sees its default configuration.
	 * <p>
	 * In the {@Code AppInitialtion.ANDHOW_CLASSPATH_FILE} Property, the default value is
	 * 'checker.default.properties'.  In that same class we also tell AndHow to use the value of that
	 * Property to decide which property file to use, thus property values are read from
	 * 'checker.default.properties'.
	 */
	@Test
	public void verifyTestEnvironmentConfiguration() {
		Checker v = new Checker();

		String url = v.getServiceUrl();

		assertEquals("https://default.bigcorp.com:80/validate", url,
				"The url should match the configuration in checker.default.properties");
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
	@Test
	public void mainMethodAlsoSeesDefaultProperties() throws Exception {

		String text = tapSystemOut(() -> {
			Checker.main(new String[]{});
		});

		assertTrue(text.contains("https://default.bigcorp.com:80/validate"),
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