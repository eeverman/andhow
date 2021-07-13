package com.bigcorp;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

/**
 * A hypothetical class that checks if a external service is running.  The url
 * of the external service is configured by a few AndHow properties.
 *
 * Of course, this isn't a complete application, but its easy to imagine {@Code doCheck} being
 * called by an AWS Lambda function, as a command line utility, or just some library part of a larger app.
 */
public class Checker {

	//All the config Properties - Its a best practice to group AndHow Properties into an interface.
	static interface Config {
		public static final StrProp PROTOCOL = StrProp.builder().mustBeNonNull().
				mustEqual("http", "https").build();
		public static final StrProp SERVER = StrProp.builder().mustBeNonNull().build();
		public static final IntProp PORT = IntProp.builder().mustBeNonNull().
				mustBeGreaterThanOrEqualTo(80).mustBeLessThanOrEqualTo(8888).build();
		public static final StrProp PATH = StrProp.builder().mustStartWith("/").build();
	}

	/**
	 * Builds a url using the AndHow Properties.
	 * @return
	 */
	public String getServiceUrl() {
		return Config.PROTOCOL.getValue() + "://" + Config.SERVER.getValue() + ":" +
				Config.PORT.getValue() + Config.PATH.getValue();
	}

	/**
	 * Hypothetical validation method that would use the service url...
	 * @param value
	 * @return
	 */
	public boolean doCheck(String value) {
		return true;	//In theory we called the service url to verify this thing.  yep.
	}

	/**
	 * A main method to run this app as it might be in a real environment.
	 * <p>
	 * In this main method the {@Code AndHow.findConfig()} method is used to append the cmd line
	 * arguments to the AndHow configuration.  This allows Property values to be configured from
	 * these arguments, in addition to all the other way they could be configured.
	 * <p>>
	 * Run it from an IDE , or via command line.
	 * <p>
	 * To run from command line, first use Maven to create a runnable jar.
	 * Here are the commands, executed from the root of the AndHow project, to build and run the jar:<br>
	 * <pre>{@Code
	 * > mvn clean package -DskipTests -Dmaven.javadoc.skip=true
	 * > java -jar andhow-testing/andhow-simulated-app-tests/example-app-2/target/app.jar
	 * }</pre>
	 * The output of this command will match the config in {@Code checker.default.properties}:
	 * <pre>{@Code
	 * Service url: https://default.bigcorp.com:80/validate
	 * }</pre>
	 *
	 * <pre>{@Code
	 * > java -Dcom.bigcorp.Calculator.CALC_MODE=FLOAT -jar andhow-testing/andhow-simulated-app-tests/example-app-2/target/app.jar 1.23 4.56
	 * }</pre>
	 * will result in {@Code Result is 0.26973686 (Float)}
	 *
	 * @param args Two arguments that are parsable to numbers.
	 */
	public static void main(String[] args) {

		//Find the AndHow Configuration and add to it the commandline args so they can be used for
		//configuration as well.
		AndHow.findConfig().setCmdLineArgs(args).build();	//Have to call build here or it doesn't work!!

		Checker v = new Checker();

		System.out.println("Service url: " + v.getServiceUrl());	//  <--display the configured url
	}



}
