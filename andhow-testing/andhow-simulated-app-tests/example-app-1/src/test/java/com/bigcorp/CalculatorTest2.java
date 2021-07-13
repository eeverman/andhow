package com.bigcorp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeEachTest;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeEachTestExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@Code CalculatorTest1} didn't fully test the app because only some code is executed in the FLOAT
 * configuration.  This test demos testing other configurations.
 * <p>
 * The {@Code @KillAndHowBeforeEachTest} annotation erases the configured state of AndHow before
 * each test so the test can specify its own AndHow configuration.  If a test doesn't explicitly
 * initialize AndHow, AndHow will initialize normally as soon as the first Property value is referenced.
 * <p>
 * When all tests are complete, {@Code KillAndHowBeforeEachTest} resets the AndHow state
 * back to what it was at the start of the test.
 */
@KillAndHowBeforeEachTest  // <-- Uses the JUnit extension mechanism
class CalculatorTest2 {

	/**
	 * Test the app with the DOUBLE configuration.
	 */
	@Test
	public void testAppInDoubleConfiguration() {

		//Force AndHow to see DOUBLE for the duration of this test
		AndHow.findConfig().addFixedValue(Calculator.MODE, "DOUBLE").build();

		Calculator mult = new Calculator();

		Number result = mult.doCalc(1.23D, 4.56D);

		assertEquals(Double.class, result.getClass(), "The result should now be a Double");
	}

	/**
	 * If the test method doesn't initialize AndHow, AndHow will initialize automatically as soon
	 * as a Property is accessed and load the default configuration from the test
	 * {@Code checker.production.properties} file.
	 */
	@Test
	public void revertToDefaultConfigIfAndHowIsNotInitialized() {

		Calculator mult = new Calculator();

		Number result = mult.doCalc(1.23D, 4.56D);

		assertEquals(Float.class, result.getClass(), "The result should revert to Float");
	}

}