package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.load.KeyValuePairLoader;
import org.yarnandtail.andhow.load.*;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;

/**
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

	private static AndHow singleInstance;
	private static final Object LOCK = new Object();

	private final AndHowCore core;

	/**
	 * Private constructor - Use the AndHowBuilder to build instances.
	 *
	 * @param naming
	 * @param loaders
	 * @param registeredGroups
	 * @param cmdLineArgs
	 * @param forcedValues
	 * @param defaultValues
	 * @throws AppFatalException
	 */
	private AndHow(NamingStrategy naming, List<Loader> loaders,
			List<GroupProxy> registeredGroups)
			throws AppFatalException {
		
		synchronized (LOCK) {
			core = new AndHowCore(naming, loaders, registeredGroups);
		}
	}

	/**
	 * Returns a builder that can be used one time to build the AndHow instance.
	 *
	 * @return A builder instance
	 */
	public static AndHowBuilder builder() {
		return new AndHowBuilder();
	}
	
	private static final String PARTIAL_CONSTRUCT_MSG =
			"AndHow is in an invalid state, trying to recover from partially initialization. "
			+ "This most likely happens during multi-thread testing "
			+ "using the AndHowNonProduction utility, which partially "
			+ "destroys the AndHow framework so it can be re-initialized "
			+ "for each test.  Alternately, AndHow could be partially "
			+ "destructed via reflection.";

	public static AndHow instance() {
		if (singleInstance != null && singleInstance.core != null) {
			return singleInstance;
		} else {
			synchronized (LOCK) {
				if (singleInstance == null) {
					build(null, null, null);
				} else if (singleInstance.core == null) {
					
				/*	This is a concession for testing.  During testing the
					core is deleted to force AndHow to reload.  Its really an
					invalid state (instance and core should be null/non-null
					together, but its handled here to simplify testing.
				*/
					try {
						AndHowCore newCore = new AndHowCore(null, getDefaultLoaders(),
								new PropertyRegistrarLoader().getGroups());
						Field coreField = AndHow.class.getDeclaredField("core");
						coreField.setAccessible(true);
						coreField.set(singleInstance, newCore);
					} catch (Exception ex) {
						if (ex instanceof AppFatalException) {
							throw (AppFatalException)ex;
						} else {
							throwFatal(PARTIAL_CONSTRUCT_MSG, ex);
						}
					}

				}
				return singleInstance;
			}

		}
	}

	/**
	 * Returns a list of new loaders that are used for default configuration.
	 *
	 * @param cmdLineArgs Optional command line arguments to be passed to the
 KeyValuePairLoader. These would be the Stringp[] args passed to the
 main method at startup.
	 * @return
	 */
	public static List<Loader> getDefaultLoaders(String... cmdLineArgs) {
		
		CommandLineArgumentLoader clal = new CommandLineArgumentLoader();
		clal.setKeyValuePairs(cmdLineArgs);
		
		List<Loader> loaders = new ArrayList();
		loaders.add(clal);
		loaders.add(new SystemPropertyLoader());		//TODO:  This needs to be set w/ assigned values
		loaders.add(new JndiLoader());
		loaders.add(new EnviromentVariableLoader());
		loaders.add(new StdPropertyFileOnClasspathLoader());
		return loaders;
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
	 * Private build method, invoked only by the inner AndHowBuilder class.
	 *
	 * It will throw a RunTimeException if the singleton instance has already
	 * been constructed.
	 *
	 * It returns a reference to the reloader, which can be used for reloading
	 * during unit test (not for production).
	 *
	 * @param naming
	 * @param loaders
	 * @param registeredGroups
	 * @return
	 * @throws AppFatalException
	 */
	private static AndHow build(
			NamingStrategy naming, List<Loader> loaders,
			List<GroupProxy> registeredGroups)
			throws AppFatalException, RuntimeException {

		synchronized (LOCK) {
			if (singleInstance != null) {
				throwFatal("AndHow is already constructed!", null);
				return null;
			} else {
				
				if (loaders == null || loaders.isEmpty()) {
					loaders = getDefaultLoaders();
				}
				
				if (registeredGroups == null || registeredGroups.isEmpty()) {
					PropertyRegistrarLoader registrar = new PropertyRegistrarLoader();
					registeredGroups = registrar.getGroups();
				}
				
				singleInstance = new AndHow(naming, loaders, registeredGroups);
				return singleInstance;
			}
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
	//ConstructionDefinition Interface
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
	public List<Property<?>> getPropertiesForGroup(GroupProxy group) {
		return core.getPropertiesForGroup(group);
	}

	@Override
	public Property<?> getProperty(String name) {
		return core.getProperty(name);
	}

	@Override
	public List<GroupProxy> getPropertyGroups() {
		return core.getPropertyGroups();
	}

	@Override
	public List<Property<?>> getProperties() {
		return core.getProperties();
	}

	@Override
	public List<ExportGroup> getExportGroups() {
		return core.getExportGroups();
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return core.getNamingStrategy();
	}
	
	/**
	 * Builds and throws an AppFatalException. The stack trace is edited to
	 * remove 2 method calls, which should put the stacktrace at the user
	 * code of the build.
	 *
	 * @param message
	 */
	/**
	 * Builds and throws an AppFatalException. The stack trace is edited to
	 * remove 2 method calls, which should put the stacktrace at the user
	 * code of the build.
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
	 * A builder class, which is the only supported way to construct an AndHow
	 * instance.
	 *
	 * Once an AndHow instance is built, it can never be rebuilt or
	 * reconfigured**.,
	 *
	 * The builder code is intended to be concise and readable in use. Thus,
	 * 'set' or 'add' is dropped from method name prefixes where possible.
	 *
	 * For single valued properties, like namingStrategy, calling
	 * namingStrategy(new ANamingStrategy()) sets the value. For attributes that
	 * takes lists of of values, there are methods that add single values or
	 * multiple values, e.g. for PropertyGroups, calling
	 * <code>builder.group(SomeGroup.class)</code> would add a single group,
	 * <code>builder.groups(aListOfGroups)</code> would add an entire list.
	 *
	 * Usage always starts with AndHow.builder() and ends with build():
	 * <pre>
	 * {@code
	 * AndHow.builder()
	 * .loader(new PropFileLoader)
	 * .group(SomeGroup.class)
	 * .group(SomeOtherGroup.class)
	 * .addCmdLineArgs([Array of cmd line arguments])
	 * .build();
	 * }
	 * </pre>
	 *
	 * There is no return value because there is no need to hold a reference to
	 * anything past framework startup. After a successful startup, Property
	 * values can be read directly. For instance, for a Property named 'MyInt':
	 * {@code Integer value = MyInt.getValue();}
	 *
	 * Attempting to call build() a 2nd time will throw a RuntimeException, so
	 * it is important that a single entry and configuration loading point to
	 * your application is well defined.
	 *
	 **See buildForNonPropduction() for a small backdoor for unit testing,
	 * which provides a non-production, unsupported way to force the AndHow
	 * framework to reload its state.
	 *
	 * @author eeverman
	 */
	public static class AndHowBuilder {

		//User config
		private final List<Loader> _loaders = new ArrayList();
		private NamingStrategy _namingStrategy = null;
		private final List<String> _cmdLineArgs = new ArrayList();

		//
		//The position at which the cmd line loader should be added.
		//May be null if not needed.
		private Integer addCmdLineLoaderAtPosition = null;

		/**
		 * Add a loader to the list of loaders. Loaders are used in the order
		 * added with the first found value 'winning'.
		 *
		 * Adding a loader in the way wipes out the list of default loaders.
		 *
		 * @param loader
		 * @return
		 */
		public AndHowBuilder loader(Loader loader) {

			if (loader instanceof CommandLineArgumentLoader) {
				throw new RuntimeException("The ComandLineArgLoader cannot be "
						+ "directly added to the list of loaders. "
						+ "Use cmdLineArgs() to add arguments instead.");
			}
			_loaders.add(loader);
			return this;
		}

		/**
		 * Add a list of loaders to the list being built. Loaders are used in
		 * the order added.
		 *
		 * @param loaders
		 * @return
		 */
		public AndHowBuilder loaders(Collection<Loader> loaders) {

			for (Loader ldr : loaders) {
				loader(ldr);
			}

			return this;
		}


		/**
		 * Adds the command line arguments, keeping any previously added.
		 *
		 * If loaders are never explicitly added, the commandline loader will
		 * be kept in its standard location in the default loader list.
		 * If loader were explicitly added, the command line loader will be
		 * implicitly added at the point at which addCmdLineArg is called in
		 * relation to the addition of the other loaders.
		 *
		 * @param commandLineArgs
		 * @return
		 */
		public AndHowBuilder addCmdLineArgs(String[] commandLineArgs) {
			
			if (commandLineArgs != null && commandLineArgs.length > 0) {
				_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));

				//Record where the cmd line loader should go, if not already determined
				if (addCmdLineLoaderAtPosition == null) {
					addCmdLineLoaderAtPosition = _loaders.size();
				}
			}

			return this;
		}

		/**
		 * Adds a command line argument in key=value form.
		 *
		 * If the value is null, only the key is added (ie its a flag).
		 * 
		 * If loaders are never explicitly added, the commandline loader will
		 * be kept in its standard location in the default loader list.
		 * If loader were explicitly added, the command line loader will be
		 * implicitly added at the point at which addCmdLineArg is called in
		 * relation to the addition of the other loaders.
		 *
		 * @param key
		 * @param value
		 * @return
		 */
		public AndHowBuilder addCmdLineArg(String key, String value) {

			if (value != null) {
				_cmdLineArgs.add(key + KeyValuePairLoader.KVP_DELIMITER + value);
			} else {
				_cmdLineArgs.add(key);
			}

			//Record where the cmd line loader should go, if not already determined
			if (addCmdLineLoaderAtPosition == null) {
				addCmdLineLoaderAtPosition = _loaders.size();
			}

			return this;
		}

		/**
		 * Sets the naming strategy, which determines how the property names are
		 * realized when used in config files, JNDI and cmd line arguments.
		 *
		 * If unspecified, CaseInsensitiveNaming is used.
		 *
		 * @param namingStrategy
		 * @return
		 */
		public AndHowBuilder namingStrategy(NamingStrategy namingStrategy) {
			this._namingStrategy = namingStrategy;
			return this;
		}

		/**
		 * Executes the AndHow framework startup.
		 *
		 * There is no return value because there is no need to hold a reference
		 * to anything past framework startup. After a successful startup,
		 * Property values can be read directly. For instance, for a property
		 * named 'MyInt': {@code Integer value = MyInt.getValue();}
		 *
		 * @throws AppFatalException If the startup fails.
		 */
		public void build() throws AppFatalException {
			populateLoaderList();
			AndHow.build(_namingStrategy, _loaders, null);
		}

		
		private void populateLoaderList() {
			if (_loaders.isEmpty()) {
				_loaders.addAll(AndHow.getDefaultLoaders(_cmdLineArgs.toArray(new String[_cmdLineArgs.size()])));
			}
			
			//Find or add a cmdLineArgLoader, populate it w/ the cmdLineArgs
			int existingCmdLoader = -1;
			for (int i = 0; i < _loaders.size(); i++) {
				if (_loaders.get(i) instanceof CommandLineArgumentLoader) {
					((CommandLineArgumentLoader)_loaders.get(i)).setKeyValuePairs(_cmdLineArgs);
					existingCmdLoader = i;
					break;
				}
			}

			if (existingCmdLoader == -1 && addCmdLineLoaderAtPosition != null) {
				CommandLineArgumentLoader cl = new CommandLineArgumentLoader();
				cl.setKeyValuePairs(_cmdLineArgs);
				_loaders.add(addCmdLineLoaderAtPosition, cl);
			}
		}
	}

}
