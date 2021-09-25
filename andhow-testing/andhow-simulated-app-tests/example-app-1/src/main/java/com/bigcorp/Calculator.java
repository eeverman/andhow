package com.bigcorp;

import org.yarnandtail.andhow.property.StrProp;

/**
 * A simple calculator that can use two different modes, which an AndHow {@code StrProp} allows
 * selection of based on configuration from (in this case) a properties file.
 *
 * This isn't a complete application, but its easy to imagine {@code doCalc} being called by an
 * AWS Lambda function, part of a command line utility, or just some library part of a larger app.
 */
public class Calculator {

	//The AndHow configuration Property to select between two modes (doesn't have to be public)
	public static final StrProp MODE = StrProp.builder()
			.notNull().oneOf("DOUBLE", "FLOAT").build();

	/**
	 * Do the calculation, but choose which implementation to use based on CALC_MODE
	 * @param a
	 * @param b
	 * @return
	 */
	public Number doCalc(Number a, Number b) {

		Number result = null;

		if (MODE.getValue().equals("DOUBLE")) {
			result = doDoubleCalc(a, b);
		} else if (MODE.getValue().equals("FLOAT")) {
			result = doFloatCalc(a, b);
		} else {
			//throw new IllegalStateException("Validation on CALC_MODE ensures this never happens");
		}

		return result;
	}

	protected Number doDoubleCalc(Number a, Number b) {
		return a.doubleValue() / b.doubleValue();
	}

	protected Number doFloatCalc(Number a, Number b) {
		return a.floatValue() / b.floatValue();
	}

	/**
	 * A main method to run this app as it would be in a 'production' environment.
	 * Run it from an IDE (configure the IDE to pass two numbers as args), or via command line.
	 * <p>
	 * To run from command line, first use Maven to create a runnable jar.
	 * Here are the commands, executed from the root of the AndHow project, to build and run the jar:<br>
	 * <pre>{@code
	 * > mvn clean package -DskipTests -Dmaven.javadoc.skip=true
	 * > java -jar andhow-testing/andhow-simulated-app-tests/example-app-1/target/app.jar 1.23 4.56
	 * }</pre>
	 * The output of running this command will be:
	 * <pre>{@code
	 * Result is 0.26973684210526316 (Double)
	 * }</pre>
	 * How did it know to use the Double implementation?  AndHow finds the {@code checker.production.properties}
	 * file on the classpath and reads the configured value for CALC_MODE.  {@code CALC_MODE.getValue()}
	 * returns 'DOUBLE'.  Compare this to the unit test for this class...
	 * <p>
	 * Properties files are just one way AndHow reads configuration.  Values in the properties file
	 * could be overwritten via env. vars., JNDI, system properties, etc..
	 * Rerunning the main method with a system property like this:
	 * <pre>{@code
	 * > java -Dcom.bigcorp.Calculator.CALC_MODE=FLOAT -jar andhow-testing/andhow-simulated-app-tests/example-app-1/target/app.jar 1.23 4.56
	 * }</pre>
	 * will result in {@code Result is 0.26973686 (Float)}
	 *
	 * @param args Two arguments that are parsable to numbers.
	 */
	public static void main(String[] args) {
		Double a = Double.parseDouble(args[0]);
		Double b = Double.parseDouble(args[1]);

		Calculator mult = new Calculator();

		Number result = mult.doCalc(a, b);

		System.out.println(mult.toString(result));
	}

	public String toString(Number result) {
		return "Result is " + result + " (" + result.getClass().getSimpleName() + ")";
	}


}
