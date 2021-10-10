package com.bigcorp;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeThisTest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;

/**
 * In the test environment, the application is configured to use the 'FLOAT'
 * implementation, as configured in the {@code andhow.properties} on the TEST CLASSPATH.
 *
 * Resources on the test classpath override the main classpath, so this is a natural and useful
 * outcome.  Just like in production, AndHow will auto-discover its configuration during testing.
 * <p>
 * Using the test {@code andhow.properties} file, however, means that all the tests run with the
 * same 'FLOAT' configuration value.  How do we test the application with the DOUBLE configuration?
 * See {@code CalculatorDoubleConfigTest} for the answer...
 * <p>
 * ...and much more flexible and complex configuration is possible for production and testing -
 * be sure to look at other of the 'simulated-app-tests'.
 */
class CalculatorDefaultTest {

	/**
	 * Verify that the app runs in FLOAT mode in the test environment.
	 */
	@Test
	public void verifyTestEnvironmentConfiguration() {
		Calculator mult = new Calculator();

		Number result = mult.doCalc(1.23D, 4.56D);

		assertEquals(Float.class, result.getClass(), "The result should be 'Float', as configured");
		assertEquals("FLOAT", Calculator.MODE.getValue());

		//System.out.println(mult.toString(result));
	}

	@Test
	public void mainMethodShouldPrintResultToSystemOut() throws Exception {

		String text = tapSystemOut(() -> {
			Calculator.main(new String[] {"4", "2"});
		});

		assertTrue(text.contains("2.0") && text.contains("Float"));
	}

	@Test
	@KillAndHowBeforeThisTest
	public void forceTemplateCreation() {
		AndHow.findConfig().addFixedValue(
				org.yarnandtail.andhow.Options.CREATE_SAMPLES, true);

		AndHow.instance();
	}
}
