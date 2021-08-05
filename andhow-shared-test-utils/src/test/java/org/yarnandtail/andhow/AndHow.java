package org.yarnandtail.andhow;

import org.yarnandtail.andhow.internal.AndHowCore;

import java.util.function.UnaryOperator;

/**
 * A class to mock the real class of the same name and package.
 *
 * The classes in this module interact w/ the real AndHow classes via reflection so there is no
 * dependency.  There is no way to test that without having classes present with the same
 * name and classpath, thus, this and other classes exist in the test directory.
 */
public class AndHow {


	//
	// Static fields matching the real AndHow
	private static volatile AndHow singleInstance;
	private static volatile AndHow.Initialization initialization = null;
	private static volatile Boolean initializing = false;
	private static volatile AndHowConfiguration<? extends AndHowConfiguration> inProcessConfig = null;
	private static UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator = null;
	private static final ThreadLocal<Boolean> findingConfig = ThreadLocal.withInitial(() -> false);

	//...and one instance field
	private volatile AndHowCore core;


	//...and one inner class
	public static class Initialization {

	}

	/**
	 * Method to initialize the singleton for testing
	 */
	public static void fullyInitialize() {
		singleInstance = new AndHow();
		singleInstance.core = new AndHowCore();
		initialization = new Initialization();
		initializing = false;
		inProcessConfig = null;
		configLocator = null;
		findingConfig.set(false);
	}
}
