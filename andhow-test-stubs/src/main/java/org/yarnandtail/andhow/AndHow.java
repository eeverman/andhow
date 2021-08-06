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

	private AndHow(AndHowConfiguration<? extends AndHowConfiguration> config) {
		core = new AndHowCore();
	}
		
	/**
	 * Fake instance method
	 * @return 
	 */
	public static AndHow instance() {
		if (singleInstance == null) {
			_fullyInitialize();
		} else if (singleInstance.core == null) {
			singleInstance.core = new AndHowCore();
		}
		return singleInstance;
	}

	//...and one inner class
	public static class Initialization {

	}

	//
	// All these methods starting with '_' are not part of the object being stubbed,
	// they are just backdoor methods to make testing setup and verification easier.

	/**
	 * Method to initialize the singleton for testing, which just destroys everything and
	 * replaces it w/ new instances.
	 */
	public static void _fullyInitialize() {
		singleInstance = new AndHow(null);
		initialization = new Initialization();
		initializing = false;
		inProcessConfig = null;
		configLocator = null;
		findingConfig.set(false);
	}
	
	/**
	 * Method to fully destroy the singleton after testing so other usages start clean.
	 */
	public static void _fullyDestroy() {
		singleInstance = null;
		initialization = null;
		initializing = false;
		inProcessConfig = null;
		configLocator = null;
		findingConfig.set(false);
	}

	/**
	 * Backdoor test getter for the AndHow singleInstance w/o no intialization / side effects.
	 *
	 * @return The current value, which may be null.
	 */
	public static AndHow _getSingleInstance() {
		return singleInstance;
	}

	/**
	 * Backdoor test getter for the AndHow initialization.
	 *
	 * @return The current value, which may be null.
	 */
	public static AndHow.Initialization _getInitialization() {
		return initialization;
	}

	/**
	 * Backdoor test getter for the AndHow initializing flag.
	 *
	 * @return The current value, which should only be true or false.
	 */
	public static Boolean _getInitializing() {
		return initializing;
	}

	/**
	 * Backdoor test getter for the AndHow inProcessConfig.
	 *
	 * @return The current value, which may be null.
	 */
	public static AndHowConfiguration<? extends AndHowConfiguration> _getInProcessConfig() {
		return inProcessConfig;
	}

	/**
	 * Backdoor test getter for the AndHow configLocator.
	 *
	 * @return The current value, which may be null.
	 */
	public static UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>>
			_getConfigLocator() {
		return configLocator;
	}

	/**
	 * Backdoor test getter for the AndHow findingConfig.
	 *
	 * @return The current value, which may be null.
	 */
	public static ThreadLocal<Boolean> _getFindingConfig() {
		return findingConfig;
	}
	
	/**
	 * Backdoor test getter for the AndHowCore instance field.
	 * 
	 * @return The current value, which may be null.
	 */
	public AndHowCore _getAndHowCore() {
		return core;
	}
	
	/**
	 * Backdoor test setter for the AndHowCore instance field.
	 * 
	 * @param newCore
	 * @return The old core.
	 */
	public AndHowCore _setAndHowCore(AndHowCore newCore) {
		AndHowCore old = core;
		core = newCore;
		return old;
	}

}
