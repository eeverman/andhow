package org.yarnandtail.andhow;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.export.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.util.AndHowUtil;
import static org.yarnandtail.andhow.internal.InitializationProblem.*;

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
 */
public class AndHow implements PropertyConfiguration, ValidatedValues {

	//
	//A few app-wide constants
	public static final String ANDHOW_INLINE_NAME = "AndHow";
	public static final String ANDHOW_NAME = "AndHow!";
	public static final String ANDHOW_URL = "https://github.com/eeverman/andhow";
	public static final String ANDHOW_TAG_LINE = "strong.simple.valid.AppConfiguration";
	private static final String SEE_USER_GUIDE =
			"  See user guide for configuration docs & examples: https://www.andhowconfig.org/user-guide";

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
	 * See {@code core} for comparison.
	 */
	private static volatile AndHow singleInstance;

	/** Stack trace and other debug info about the startup & initiation of AndHow */
	private static volatile Initialization initialization = null;

	/* True only during instance(AndHowConfiguration) method to detect re-entrant initialization */
	private static volatile Boolean initializing = false;

	/* Config that was returned from findConfig, but has not yet been used to initialize AndHow. */
	private static volatile AndHowConfiguration<? extends AndHowConfiguration> inProcessConfig = null;

	/* This is 'final', but can be swapped out w/ Reflection for testing */
	private static UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>> configLocator = null;

	/* Flag to block reentrant calls to findConfig() and setConfig() */
	private static final ThreadLocal<Boolean> findingConfig = ThreadLocal.withInitial(() -> false);

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
					config.getLoaderEnvironment(),
					config.getRegisteredGroups());
		}
	}

	/**
	 * Prior to AndHow initialization, this method finds the configuration that will be used.
	 *
	 * On the first call to this method, a new {@code AndHowConfiguration} instance will be
	 * created.  Later calls to this method prior to AndHow initialization will return that
	 * same instance.  After AndHow is initialized this method will throw a fatal exception, since no
	 * modification to the configuration can be made after AndHow has initialized.
	 * <p>
	 * The new {@code AndHowConfiguration} is created in one of two ways:
	 * <ol>
	 * <li>If there is a class implementing the {@code AndHowInit} interface on the classpath,
	 * 	 it will be discovered and that class' {@link AndHowInit#getConfiguration()} method will be
	 * 	 called to construct the instance.  This is an easy configuration point to configure AndHow
	 * 	 for production or test.
	 * <li>Otherwise, a default implementation of {@code AndHowConfiguration} is created
	 *   and returned.
	 * </ol>
	 * <p>
	 * This method provides a way to 'grab' the configuration before AndHow initialization
	 * and make additions or customizations.  Common reasons for doing this would be:
	 * <ul>
	 * <li>In the {@code main(String[] args)} method to add the command line arguments to
	 * AndHow (see code example below)
	 * <li>To provide different configuration or modify the list Loaders based on
	 * the entry point of the application.
	 * <li>In a test class to add 'fixed' configuration values needed for a specific test
	 * (See test examples in the simulated app tests)
	 * </ul>
	 * <p>
	 * Example usage in a main method:
	 * <pre>{@code
	 * public static void main(String[] args) {
	 *   AndHow.findConfig().setCmdLineArgs(myCmdLineArgs);
	 *   //Do other stuff - AndHow initializes as soon as the first Property access happens.
	 *   //...
	 *   //AndHow.instance();	//Forces initialization, but isn't required
	 * }
	 * }</pre>
	 * <p>
	 * In the above example, AndHow will initialize itself as soon as the first Property value
	 * is accessed, e.g. {@code MyProperty.getValue()}.  In nearly all cases this is acceptable.
	 * If, however, you are worried your application has thread contention at startup or the
	 * application doesn't access property values until some later event happens, you can
	 * force initialization by calling {@code AndHow.instance()}.  Initialization will force
	 * validation of all property values and will block other attempts to initialize AndHow,
	 * perhaps helping to pinpoint startup contention.
	 * <p>
	 * Note: There was a behaviour change from v1.4.1 to v1.4.2:  Beginning with
	 * v1.4.2, this method returns the same instance each time until initialization, then it throws
	 * a fatal exception.  In v1.4.1 and earlier, a newly created instance was returned for each call.
	 * This is a large behaviour change, but its really blocking an unsafe application operation
	 * or an unexpected application 'no-op' where the caller might expect to keep working on the same
	 * configuration and actually be starting over.
	 *
	 * @return The AndHowConfiguration with continuity between calls
	 * @throws AppFatalException A fatal exception if called after AndHow has initialized.
	 */
	public static AndHowConfiguration<? extends AndHowConfiguration> findConfig()
			throws AppFatalException {

		synchronized (LOCK) { //Access to the config is sync'ed same as init code

			if (isInitialized()) {
				throw new AppFatalException(new IllegalMethodCalledAfterInitialization("findConfig"));
			}

			if (inProcessConfig == null) {	//No config exists, so need to create

				if (findingConfig.get()) {	//This thread is in a reentrant loop of calling findConfig!

					//Relax... This is normal.  Typical loop path:
					// 1) AndHow begins initialization or the application calls findConfig()
					// 2) findConfig() calls AndHowUtil.findConfiguration()
					// 3) AndHowUtil discovers and calls the user implementation of AndHowInit.getConfiguration()
					// 4) That method needs to return an AndHowConfiguration instance, so it calls AndHow.findConfig()
					//    to get an instance - its a loop!
					//This flow is supported and considered a best practice so that all access to configuration
					//takes place via findConfig().

					inProcessConfig =  StdConfig.instance();	//Break the loop by directly creating new instance

				} else {	//Not in a loop

					findingConfig.set(true);	//Block reentrant calls

					try {

						if (configLocator != null) {
							inProcessConfig = configLocator.apply(StdConfig.instance());
						} else {
							inProcessConfig = AndHowUtil.findConfiguration(StdConfig.instance());
						}

					} finally {
						findingConfig.remove();	//Remove threadlocal variable from thread
					}
				}
			}

			return inProcessConfig;

		}	//end sync
	}

	/**
	 * Prior to AndHow initialization, this method explicitly sets the configuration to be used.
	 *
	 * After AndHow is initialized this method will throw a fatal exception since no
	 * modification to configuration is possible after initialization.
	 * <p>
	 * It is easier and safer to use {@link #findConfig()}, which will a auto-discover
	 * configuration, however, during testing or some advanced use cases,
	 * it can be useful to ignore the normal configuration discovery mechanism.
	 * <p>
	 * If this method is used, configuration created by {@link AndHowInit#getConfiguration()}
	 * and possibly later modified by calls to {@link #findConfig()} up to this point
	 * will be replaced by the configuration set here.  Later calls to {@link #findConfig()} will
	 * return this new value.
	 *
	 * @param config The new configuration which must not be null.
	 * @throws AppFatalException If AndHow is already initialized or the passed config is null.
	 */
	public static void setConfig(AndHowConfiguration<? extends AndHowConfiguration> config)
			throws AppFatalException {

		if (config == null) {
			throw new AppFatalException("Cannot set a null configuration." + SEE_USER_GUIDE);
		}

		synchronized (LOCK) { //Access to the config is sync'ed same as init code

			if (isInitialized()) {
				throw new AppFatalException(new IllegalMethodCalledAfterInitialization("setConfig"));
			} else if (initializing) {
				throw new AppFatalException(new SetConfigCalledDuringInitialization());
			}

			if (findingConfig.get()) {  //This thread is in a reentrant loop of calling findConfig!
				throw new AppFatalException(new SetConfigCalledDuringFindConfig());
			} else {
				findingConfig.remove();	//Calling get creates a ThreadLocal, so remove
			}

			inProcessConfig = config;

		}	//end sync
	}

	/**
	 * Returns the singleton instance of AndHow, initializing a new instance if one doesn't exist.
	 *
	 * If AndHow is not yet initialized, a new instance is created by initializing AndHow
	 * using configuration found via findConfig().
	 * AndHow initialization configures AndHow, loads Property values from all sources (such as
	 * env. vars., System Props, etc.), then validates all values.
	 * <p>
	 * In production, use of this method is optional at startup. See {@link #findConfig()}
	 * for an example of how to access and modify configuration, and initialize AndHow.
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
					return initialize(findConfig());
				}

			}	// end sync

		}
	}

	/**
	 * Initialize AndHow with the passed configuration - This method is not normally needed or used in
	 * production and will throw a Runtime exception if called more than once, directly or indirectly.
	 *
	 * AndHow initialization configures AndHow, loads Property values from all sources (such as
	 * env. vars., System Props, etc.), then validates all values.
	 * In normal usage, this method should not be called by application code and
	 *
	 * @param config The non-null configuration to be used to build the new AndHow singleton.
	 * @return The singleton AndHow instance, newly built from the configuration.
	 * @throws AppFatalException If AndHow is already initialized, mis-configured or there are
	 * 	Property value validation errors.
	 */
	private static AndHow initialize(AndHowConfiguration config) throws AppFatalException {

		synchronized (LOCK) {

			if (isInitialized()) {
				throw new AppFatalException(new IllegalMethodCalledAfterInitialization("initialize"));
			}

			if (! initializing) {

				initializing = true;										//Block re-entrant initialization
				inProcessConfig = null;									//No more configuration changes
				initialization = new Initialization(config);	//Record initialization time & place

				if (singleInstance == null) {

					try {
						singleInstance = new AndHow(config);		//Build new instance
					} finally {
						initializing = false;	//Done w/ init regardless of possible error
					}

				} else if (singleInstance.core == null) {

					try {

						AndHowCore newCore = new AndHowCore(
								config.getNamingStrategy(),
								config.buildLoaders(),
								config.getLoaderEnvironment(),
								config.getRegisteredGroups());

						singleInstance.core = newCore;

					} finally {
						initializing = false;	//Done w/ init regardless of possible error
					}

				}	else {
					throw new IllegalStateException("This exception can never be reached.");
				}

			} else {
				//Oops, code in AndHowInit or AndHowConfiguration forced AndHow initialization
				throw new AppFatalException(
						new InitiationLoop(initialization, new Initialization(config)));
			}

			return singleInstance;

		}	//end sync
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

	/**
	 * Export Property's to a collection for use with frameworks that take configuration as
	 * key-value maps or similar.
	 * <p>
	 * Simple example usage that results in a {@code Map<String, String>} of names and values:
	 * <pre>{@code
	 * //UI_CONFIG & SERVICE_CONFIG contain AndHow Properties
	 * Map<String, String> export =
	 *   AndHow.instance().export(UI_CONFIG.class, SERVICE_CONFIG.class)
	 *   .collect(ExportCollector.stringMap());
	 * }</pre>
	 * <p>
	 * Property export is not allowed by default and is enabled by annotating a class containing
	 * Properties with {@link ManualExportAllowed}.
	 * <p>
	 * {@link ExportCollector} can collect to Maps or {@link java.util.Properties}, as well as exporting
	 * String or Object values. Many variations are possible: e.g. export object values
	 * (instead of Strings) and prepend 'aaa_' to each export name:
	 * <pre>{@code
	 * Map<String, Object> export =
	 *  AndHow.instance().export(SERVICE_CONFIG.class)
	 *   .map(p -> p.mapNames(
	 *     p.getExportNames().stream().map(n -> "aaa_" + n).collect(Collectors.toList())
	 *   ))
	 *   .collect(ExportCollector.objectMap());
	 * }</pre>
	 * <p>
	 * {@code export()} returns a {@link Stream} of {@link PropertyExport}'s, one PropertyExport per
	 * Property.  Each PropertyExport has a list of export names for the Property.  Names can include
	 * the Property's canonical name and any 'out' aliases for the Property.  Which names are included
	 * is controlled by options on the {@link ManualExportAllowed} annotation.
	 * <p>
	 * The export names and the values can be remapped via PropertyExport 'map' methods
	 * (see 'mapNames' in example above).  See {@link PropertyExport} for more mapping examples.
	 * <p>
	 * The {@link ExportCollector} creates an entry in the final collection for each name, mapping
	 * it to the Property's Object or String value.  Thus, the ExportCollector 'flattens'
	 * PropertyExports by expanding the name list and collects them into the final collection.
	 * <p>
	 * Exports via the {@code export()} method are 'Manual' - the app must invoke them.  There are
	 * also 'Auto' exports - See the {@link GroupExport} annotation.
	 * <p>
	 * @param exportClasses The classes to have their contained Properties exported.
	 * @return A Stream of {@link PropertyExport} which can be converted into a collection of
	 *   key-value pairs via one of the {@link ExportCollector}s.
	 * @throws IllegalAccessException If any of the exported classes are not annotated to allow export.
	 */
	public Stream<PropertyExport> export(Class<?>... exportClasses) throws IllegalAccessException {
		return core.export(exportClasses);
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
	public NamingStrategy getNamingStrategy() {
		return core.getNamingStrategy();
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
