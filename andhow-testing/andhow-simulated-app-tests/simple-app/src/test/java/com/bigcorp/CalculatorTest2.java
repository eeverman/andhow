package com.bigcorp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.KillAndHowBeforeEachTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@Code CalculatorTest1} didn't fully test the app because only some code is executed in the FLOAT
 * configuration.  This test demos testing other configurations.
 * <p>
 * The {@Code KillAndHowBeforeEachTest} Junit extension is used to test the app in different
 * configurations.  With this extension the AndHow configuration is erased prior to each test,
 * allowing each test to specify its own AndHow configuration.  If a test doesn't explicitly
 * initialize AndHow, the default AndHow configuration will be invoked.
 * <p>
 * When all tests are complete, {@Code KillAndHowBeforeEachTest} will reset the AndHow
 * configuration back to what it was at the start of the test.
 */
@ExtendWith(KillAndHowBeforeEachTest.class)	// <-- JUnit extension
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
	 * {@Code andhow.properties} file.
	 */
	@Test
	public void revertToDefaultConfigIfAndHowIsNotInitialized() {

		Calculator mult = new Calculator();

		Number result = mult.doCalc(1.23D, 4.56D);

		assertEquals(Float.class, result.getClass(), "The result should revert to Float");
	}

}