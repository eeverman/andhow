package com.bigcorp;

import org.junit.jupiter.api.Test;

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
class CalculatorTest1 {

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
}