package com.bigcorp;

import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

	/**
	 * Testing that in the test environment, the application is configured to use the 'FLOAT'
	 * implementation, as configured in the {@Code andhow.properties} on the TEST CLASSPATH.
	 *
	 * Resources on the test classpath override the main classpath, so this is a natural and useful
	 * result.  Just like in production, AndHow will auto-discover its configuration during testing.
	 * <p>
	 * Much more flexible and complex configuration is possible for production and testing -
	 * be sure to look at other of the 'simulated-app-tests'.
	 */
	@Test
	public void verifyTestEnvironmentConfiguration() {
		Calculator mult = new Calculator();

		Number result = mult.doCalc(1.23D, 4.56D);

		assertEquals(Float.class, result.getClass(), "The result should be 'Float', as configured");
		System.out.println(mult.toString(result));
	}

	@Test
	public void verifyMode() {
		assertEquals("FLOAT", Calculator.MODE.getValue());
	}

	@Test
	public void directlyTestDoubleImplementation() {
		Calculator mult = new Calculator();
		assertEquals(2, mult.doDoubleCalc(4, 2).intValue());
	}

	@Test
	public void mainMethodShouldPrintResultToSystemOut() throws Exception {

		String text = tapSystemOut(() -> {
			Calculator.main(new String[] {"4", "2"});
		});

		assertTrue(text.contains("2.0") && text.contains("Float"));
	}
}