package com.bigcorp;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.AndHowJunit5TestBase;
import org.yarnandtail.andhow.NonProductionConfig;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatorTest2 extends AndHowJunit5TestBase {

	/**
	 * {@Code CalculatorTest1} didn't fully test the app because different code is
	 * executed based on the configuration.  This test demos testing other configurations.
	 * <p>
	 * Here the {@Code AndHowJunit5TestBase} is used to safely test the app in different configurations.
	 * The state of AndHow is saved and restored before and after each test.  Using the
	 * NonProductionConfig class we can specify a fixed value for a parameter, overriding the
	 * value configured in the properties file, then force AndHow to reload.
	 */
	@Test
	public void testAppInFloatConfiguration() {

		//Force AndHow to see DOUBLE for the duration of this test
		NonProductionConfig.instance().addFixedValue(Calculator.MODE, "DOUBLE").forceBuild();

		Calculator mult = new Calculator();

		Number result = mult.doCalc(1.23D, 4.56D);

		assertEquals(Double.class, result.getClass(), "The result should now be a Double");
		//System.out.println(mult.toString(result));
	}

}