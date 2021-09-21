package com.bigcorp;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

/**
 * A hypothetical class that checks if a configured service url is 'live'.
 * The url of the external service is configured by AndHow properties.
 * <p>
 * Of course, this isn't a complete application, but its easy to imagine {@code doCheck} being
 * called by an AWS Lambda function, as a command line utility, or as just a library in a larger app.
 */
public class Checker {

	//All the config Properties for this class
	// - Its a best practice to group AndHow Properties into an interface.
	interface Config {
		StrProp PROTOCOL = StrProp.builder().mustBeNonNull().
				mustEqual("http", "https").build();
		StrProp SERVER = StrProp.builder().mustBeNonNull().build();
		IntProp PORT = IntProp.builder().mustBeNonNull().
				greaterThanOrEqualTo(80).lessThanOrEqualTo(8888).build();
		StrProp PATH = StrProp.builder().mustStartWith("/").build();
	}

	/**
	 * Builds a url using the AndHow Properties.
	 * @return
	 */
	public String getServiceUrl() {
		return Config.PROTOCOL.getValue() + "://" + Config.SERVER.getValue() + ":" +
				Config.PORT.getValue() + Config.PATH.getValue();
	}

	/*
	 * In the real world, this method would verify the configured url works...
	 */
//	public boolean checkTheUrl() {
//		... verify the url return a http 200 code or something ...
//	}

	/**
	 * A main method to run this app as it might be in a real environment.
	 * <p>
	 * In this main method the {@code AndHow.findConfig()} method is used to append the cmd line
	 * arguments to the AndHow configuration.  This allows Property values to be configured from
	 * these arguments, in addition to all the other way they could be configured.
	 * <p>
	 * Run it from an IDE , or via command line.
	 * <p>
	 * To run from command line, first use Maven to create a runnable jar.
	 * Here are the commands, executed from the root of the AndHow project, to build and run the jar:<br>
	 * <pre>{@code
	 * > mvn clean package -DskipTests -Dmaven.javadoc.skip=true
	 * > java -jar andhow-testing/andhow-simulated-app-tests/example-app-2/target/app.jar
	 * }</pre>
	 * The output of this command will match the config in {@code checker.default.properties}:
	 * <pre>{@code
	 * Service url: https://default.bigcorp.com:80/validate
	 * }</pre>
	 *
	 * <pre>{@code
	 * > java -Dcom.bigcorp.Calculator.CALC_MODE=FLOAT -jar andhow-testing/andhow-simulated-app-tests/example-app-2/target/app.jar 1.23 4.56
	 * }</pre>
	 * will result in {@code Result is 0.26973686 (Float)}
	 *
	 * @param args Two arguments that are parsable to numbers.
	 */
	public static void main(String[] args) {

		//Find the AndHow Configuration and add to it the commandline args so they can be used for
		//configuration as well.
		AndHow.findConfig().setCmdLineArgs(args);


		//AndHow init happens implicitly as soon as the first Property value is accessed
		Checker v = new Checker();
		System.out.println("Service url: " + v.getServiceUrl());	//  <--display the configured url
	}



}
