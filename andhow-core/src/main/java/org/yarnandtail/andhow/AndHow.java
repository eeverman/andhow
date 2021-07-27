package org.yarnandtail.andhow;

import java.util.*;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Central AndHow singleton class.
 * 
 * This class is not directly constructed.  The primary way to configure an
 * instance is indirectly by creating a subclass of org.yarnandtail.andhow.AndHowInit.
 * At startup, AndHow discovers your AndHowInit implementation and uses it to configure
 * a single instance.
 * 
 * For cases where the application needs to accept command line arguments or
 * augment the configuration with fixed values, the configuration can have those
 * parameters added like this:
 * <pre>{@code
 * AndHow.findConfig().setCmdLineArgs(myCmdLineArgs).build();
 * }</pre>
 * <code>findConfig()</code> finds the <code>AndHowConfiguration</code> that
 * would be used if <code>AndHow.instance()</code> was called.  <code>build()</code>
 * then causes the AndHow instance to be built with that modified configuration.
 * The code above (or any method of AndHow initiation) can only be executed once
 * during the life of the application.
 * 
 * @author eeverman
 */
public class AndHow implements StaticPropertyConfiguration, ValidatedValues {

	//
	//A few app-wide constants
	public static final String ANDHOW_INLINE_NAME = "AndHow";
	public static final String ANDHOW_NAME = "AndHow!";
	public static final String ANDHOW_URL = "https://github.com/eeverman/andhow";
	public static final String ANDHOW_TAG_LINE = "strong.simple.valid.AppConfiguration";

	/** Dedicated object for synchronization of configuration and initialization */
	private static final Object LOCK = new Object();

	//
	//All the fields are volatile for visibility to threads.
	//All code where fields can be modified are synchronized on the LOCK object.

	/**
	 * The AndHow singleton.
	 *
	 * The singleton instance is null until AndHow is initialized, then a singleton is created that
	 * will live for the life of the application.
	 * <p>
	 * See {@Code core} for comparison.
	 */
	private static volatile AndHow singleInstance;
	
	/** Stack trace and other debug info about the startup & initiation of AndHow */
	private static volatile Initialization initialization = null;
	
	/* True only during instance(AndHowConfiguration) method to detect re-entrant initialization */
	private static volatile Boolean initializing = false;

	/* Config that was returned from findConfig, but has not yet been used to initialize AndHow. */
	private static volatile AndHowConfiguration<? extends AndHowConfiguration> inProcessConfig = null;

	/**
	 * The core contains the entire state of AndHow, including values of Properties, once
	 * AndHow has been initialized.  It is the only non-static variable of this class.
	 *
	 * Pre-initialization state such the loader list, debug info, and other flags
	 * (basically the static final vars of this class) are not held in the core.
	 * <p>
	 * References to the core are never given out, while app code can easily get a reference
	 * to the singleton AndHow, which contains the core.  This is intentional.
	 * In production, there is only ever one AndHow instance and its one Core for the life of the app.
	 * During unit testing, however, test utilities may replace the Core to allow testing with
	 * different configuration states.  This is possible while not invalidating app code which might
	 * hold a reference to the AndHow singleton.
	 */
	private volatile AndHowCore core;

	private AndHow(AndHowConfiguration<? extends AndHowConfiguration> config) throws AppFatalException {
		synchronized (LOCK) {
			core = new AndHowCore(
					config.getNamingStrategy(),
					config.buildLoaders(),
					config.getRegisteredGroups());
		}
	}
	
	/**
	 * Prior to AndHow initialization, this method finds the configuration that will be used.
	 *
	 * On the first call to this method, a new {@Code AndHowConfiguration} instance will be
	 * created.  Later calls to this method prior to AndHow initialization will return that
	 * same instance.  After AndHow is initialized this method will throw a fatal exception, since no
	 * modification to the configuration can be made after AndHow has initialized.
	 * <p>
	 * The new {@Code AndHowConfiguration} is created in one of two ways:
	 * <ol>
	 * <li>If there is a class implementing the {@Code AndHowInit} interface on the classpath,
	 * 	 that class' {@Code getConfiguration()} method will be called to construct the instance.
	 * 	 This is an easy configuration point to configure AndHow for production or test.
	 * <li>Otherwise, a default implementation of {@Code AndHowConfiguration} is created
	 *   and returned.
	 * </ol>
	 * <p>
	 * This method provides a way to 'grab' the configuration before AndHow initialization
	 * and make additions or customizations.  Common reasons for doing this would be:
	 * <ul>
	 * <li>In the {@Code main(String[] args)} method to add the command line arguments to
	 * AndHow (see code example below)
	 * <li>To provide different configuration or modify the list Loaders based on
	 * the entry point of the application.
	 * <li>In a test class to add 'fixed' configuration values needed for a specific test
	 * (See test examples in the simulated app tests)
	 * </ul>
	 * <p>
	 * Example usage:
	 * <pre>{@code
	 * public static void main(String[] args) {
	 *   AndHow.findConfig().setCmdLineArgs(myCmdLineArgs);
	 *   //Do other stuff - AndHow initializes as soon as the first Property access happens.
	 * }
	 * }</pre>
	 * <p>
	 * Note: There was a behaviour change from v1.4.1 to v1.4.2:  Beginning with
	 * v1.4.2, this method returns the same instance each time until initialization, then it throws
	 * a fatal exception.  In v1.4.1 and earlier, a newly created instance was returned for each call.
	 * This is a large behaviour change, but its really blocking an unsafe application operation
	 * or an unepected application 'no-op' where the caller might expect to keep working on the same
	 * configuration and actually be starting over.
	 *
	 * @return The AndHowConfiguration with continuity between calls
	 * @throws RuntimeException A fatal exception if called after AndHow has initialized.
	 */
	public static AndHowConfiguration<? extends AndHowConfiguration> findConfig() {
		synchronized (LOCK) { //Access to the config is sync'ed same as init code

			if (isInitialized()) {
				throwFatal("AndHow is already initialized, so access to the configuration is blocked.", null);
			}

			if (inProcessConfig == null) {
				inProcessConfig = AndHowUtil.findConfiguration(StdConfig.instance());
			}
			return inProcessConfig;

		}	//end sync
	}

	/**
	 * Returns the single instance of AndHow.
	 *
	 * If the AndHow is null, a new instance is created by initializing AndHow
	 * using configuration found via findConfig().
	 * 
	 * @return The singleton AndHow instance - the same instance for the life of the app.
	 * @throws AppFatalException If AndHow is mis-configured
	 */
	public static AndHow instance() throws AppFatalException {
		if (isInitialized()) {

			return singleInstance;

		} else {

			synchronized (LOCK) {

				if (isInitialized()) {
					return singleInstance;
				} else {
					return instance(findConfig());
				}

			}	// end sync

		}
	}

	/**
	 * Initialized AndHow with the passed configuration - This method is not normally
	 * needed or used in production.
	 *
	 * AndHow is a singleton, so this method will throw an AppFatalException if
	 * AndHow has already been initialized.
	 *
	 * @param config The AndHowConfiguration to be used to build the new instance.
	 * @return The singleton AndHow instance, newly built from the configuration.
	 * @throws AppFatalException If AndHow has already been initialized.
	 */
	public static AndHow instance(AndHowConfiguration config) throws AppFatalException {
		synchronized (LOCK) {

			if (isInitialized()) {

				throw new AppFatalException(
						"Cannot construct a new AndHow instance when there is an existing one.");

			} else {

				if (singleInstance == null) {
					
					if (! initializing) {
						
						try {
							
							initializing = true;										//Block re-entrant initialization
							inProcessConfig = null;									//No more configuration changes
							initialization = new Initialization(config);	//Record initialization time & place
							singleInstance = new AndHow(config);		//Build new instance
							
						} finally {
							initializing = false;										//Done w/ init regardless of possible error
						}
						
					} else {
						
						throw new AppFatalException(
								new ConstructionProblem.InitiationLoopException(initialization, new Initialization(config)));
					}
					
				} else if (singleInstance.core == null) {

					/*
					 In production, there is only ever one AndHow instance and its one Core for the life of
					 the app.  During unit testing, however, test utilities may replace the Core to allow
					 testing with different configuration states.  This is possible while not invalidating app
					 code which might hold a reference to the AndHow singleton.
					 This code handles this special case.
	 				*/

					if (! initializing) {
						
						try {
							
							initializing = true;										//Block re-entrant initialization
							inProcessConfig = null;									//No more configuration changes
							initialization = new Initialization(config);	//Record initialization time & place

							AndHowCore newCore = new AndHowCore(
									config.getNamingStrategy(),
									config.buildLoaders(),
									config.getRegisteredGroups());

							singleInstance.core = newCore;

						} finally {
							initializing = false;	//Done w/ init regardless of possible error
						}
						
					} else {
						
						throw new AppFatalException(
								new ConstructionProblem.InitiationLoopException(initialization, new Initialization(config)));
						
					}

				}
				
				return singleInstance;

			}

		}	//end sync
	}

	/**
	 * Determine if AndHow is initialized or not w/out forcing AndHow to load.
	 *
	 * @deprecated This method name was typod.  Please use isInitialized() instead.
	 * @return
	 */
	public static boolean isInitialize() {
		return isInitialized();
	}

	/**
	 * Determine if AndHow is initialized or not w/out forcing AndHow to load.
	 *
	 * @return
	 */
	public static boolean isInitialized() {
		return singleInstance != null && singleInstance.core != null;
	}
	
	/**
	 * Get the stacktrace of where AndHow was initialized.
	 * 
	 * This can be useful for debugging InitiationLoopException errors or
	 * errors caused by trying to initialize AndHow when it is already initialized.
	 * This stacktrace identifies the point in code that caused the initial
	 * AndHow initialization, prior to the error.  The reported exception will
	 * point to the place where AndHow entered a loop during its construction
	 * or application code attempted to re-initialize AndHow.
	 * 
	 * @return A stacktrace if it is available (some JVMs may not provide one)
	 * or an empty stacktrace array if it is not available or AndHow is not
	 * yet initialized.
	 */
	public static StackTraceElement[] getInitializationTrace() {
		if (initialization != null) {
			return initialization.getStackTrace();
		} else {
			return new StackTraceElement[0];
		}
	}

	//
	//PropertyValues Interface
	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return core.isExplicitlySet(prop);
	}

	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		return core.getExplicitValue(prop);
	}

	@Override
	public <T> T getValue(Property<T> prop) {
		return core.getValue(prop);
	}

	//
	//StaticPropertyConfiguration Interface
	@Override
	public List<EffectiveName> getAliases(Property<?> property) {
		return core.getAliases(property);
	}

	@Override
	public String getCanonicalName(Property<?> prop) {
		return core.getCanonicalName(prop);
	}

	@Override
	public GroupProxy getGroupForProperty(Property<?> prop) {
		return core.getGroupForProperty(prop);
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return core.getNamingStrategy();
	}
	
	/**
	 * Builds and throws an AppFatalException. The stack trace is edited to
	 * remove 2 method calls, which should put the stacktrace at the user code
	 * of the build.
	 *
	 * @param message
	 */
	private static void throwFatal(String message, Throwable throwable) {

		if (throwable instanceof AppFatalException) {
			throw (AppFatalException) throwable;
		} else {
			AppFatalException afe = new AppFatalException(message, throwable);
			StackTraceElement[] stes = afe.getStackTrace();
			stes = Arrays.copyOfRange(stes, 2, stes.length);
			afe.setStackTrace(stes);
			throw afe;
		}
	}
	

	/**
	 * Encapsulate when and where AndHow was initialized.
	 * 
	 * Useful for debugging re-entrant startups or uncontrolled startup conditions.
	 * There is no API access to the instance created of this class,
	 * it is intended to be viewed in an IDE debugger.
	 */
	public static class Initialization {
		private final StackTraceElement[] stackTrace;
		private final long timeStamp;
		private final AndHowConfiguration<? extends AndHowConfiguration> config;
		
		public Initialization(AndHowConfiguration<? extends AndHowConfiguration> config) {
			timeStamp = System.currentTimeMillis();
			StackTraceElement[] ste = new Exception().getStackTrace();
			stackTrace = Arrays.copyOfRange(ste, 1, ste.length - 1);
			this.config = config;
		}

		public StackTraceElement[] getStackTrace() {
			return stackTrace;
		}

		public long getTimeStamp() {
			return timeStamp;
		}

		public AndHowConfiguration<? extends AndHowConfiguration> getConfig() {
			return config;
		}
		
	}

}
