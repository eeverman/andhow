package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

	private static volatile AndHow singleInstance;
	private static final Object LOCK = new Object();

	private volatile AndHowCore core;
	
	/** Stack trace and time of startup */
	private static volatile Initialization initialization;
	
	/**
	 * True only during the instance(AndHowConfiguration) method to detect
	 * re-entrant initialization
	 */
	private static AtomicBoolean initializing = new AtomicBoolean(false);

	/**
	 * The configuration that has been returned from findConfiguration.
	 * On the first call, a new instance is returned.  Future calls return this
	 * same instance.
	 */
	private static AtomicReference<AndHowConfiguration<? extends AndHowConfiguration>>
			inProcessConfig = new AtomicReference();

	private AndHow(AndHowConfiguration config) throws AppFatalException {
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
	 * same instance.  After AndHow is initialized this method will return null, since no
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
	 * Note: There was a subtle behaviour change from v1.4.1 to v1.4.2:  Beginning with
	 * v1.4.2, this method returns the same instance each time.  In v1.4.1 and earlier, a
	 * newly created instance was returned for each call.
	 *
	 * @return 
	 */
	public static AndHowConfiguration<? extends AndHowConfiguration> findConfig() {
		if (inProcessConfig.get() == null) {
			AndHowConfiguration<? extends AndHowConfiguration> conf =
					AndHowUtil.findConfiguration(StdConfig.instance());

			//This may fail to set the value, but in that case its already been set.
			inProcessConfig.compareAndSet(null, conf);
		}
		return inProcessConfig.get();
	}

	/**
	 * Returns the single instance of AndHow.
	 *
	 * If the AndHow is null, a new instance is created by initializing AndHow
	 * using configuration found via findConfig().
	 * 
	 * @return The singleton AndHow instance
	 * @throws AppFatalException If AndHow is mis-configured
	 */
	public static AndHow instance() throws AppFatalException {
		if (singleInstance != null && singleInstance.core != null) {
			return singleInstance;
		} else {
			synchronized (LOCK) {
				if (singleInstance == null || singleInstance.core == null) {
					return instance(findConfig());
				} else {
					return singleInstance;
				}
			}

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

			if (singleInstance != null && singleInstance.core != null) {
				throw new AppFatalException("Cannot request construction of new "
						+ "AndHow instance when there is an existing instance.");
			} else {

				if (singleInstance == null) {
					
					if (! initializing.get()) {
						
						try {
							
							initializing.getAndSet(true);	//Block re-entrant initialization
							inProcessConfig.set(null);							//No more configuration changes
							initialization = new Initialization();	//Record initialization time & place
							singleInstance = new AndHow(config);		//Build new instance
							
						} finally {
							initializing.getAndSet(false);	//Done w/ init regardless of possible error
						}
						
					} else {
						
						throw new AppFatalException(
								new ConstructionProblem.InitiationLoopException(initialization, new Initialization()));
					}
					
				} else if (singleInstance.core == null) {

					/*	This is a concession for testing.  During testing the
					core is deleted to force AndHow to reload.  Its really an
					invalid state (instance and core should be null/non-null
					together, but its handled here to simplify testing.  */
					
					if (! initializing.get()) {
						
						try {
							
							initializing.getAndSet(true);	//Block re-entrant initialization
							inProcessConfig.set(null);							//No more configuration changes
							initialization = new Initialization();	//Record initialization time & place

							AndHowCore newCore = new AndHowCore(
									config.getNamingStrategy(),
									config.buildLoaders(),
									config.getRegisteredGroups());
							Field coreField = AndHow.class.getDeclaredField("core");
							coreField.setAccessible(true);
							coreField.set(singleInstance, newCore);

						} catch (Exception ex) {
							
							if (ex instanceof AppFatalException) {
								throw (AppFatalException) ex;
							} else {
								throwFatal("", ex);
							}
						} finally {
							initializing.getAndSet(false);	//Done w/ init regardless of possible error
						}
						
					} else {
						
						throw new AppFatalException(
								new ConstructionProblem.InitiationLoopException(initialization, new Initialization()));
						
					}

				}
				
				return singleInstance;

			}

		}	//end sync
	}

	/**
	 * Determine if AndHow is initialized or not w/out forcing AndHow to load.
	 *
	 * @return
	 */
	public static boolean isInitialize() {
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
	 * Encapsilate when and where AndHow was initialized.
	 * 
	 * Useful for debugging re-entrant startups or uncontrolled startup conditions.
	 */
	public static class Initialization {
		private StackTraceElement[] stackTrace;
		private long timeStamp;
		
		public Initialization() {
			timeStamp = System.currentTimeMillis();
			StackTraceElement[] ste = new Exception().getStackTrace();
			stackTrace = Arrays.copyOfRange(ste, 1, ste.length - 1);
		}

		public StackTraceElement[] getStackTrace() {
			return stackTrace;
		}

		public long getTimeStamp() {
			return timeStamp;
		}
		
	}

}
