package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.load.StringArgumentLoader;
import org.yarnandtail.andhow.api.BasePropertyGroup;

/**
 *
 * @author eeverman
 */
public class AndHow implements ConstructionDefinition, ValueMap {
	
	//
	//A few app-wide constants
	public static final String ANDHOW_INLINE_NAME = "AndHow";
	public static final String ANDHOW_NAME = "AndHow!";
	public static final String ANDHOW_URL = "https://github.com/eeverman/andhow";
	public static final String ANDHOW_TAG_LINE = "strong.simple.valid.AppConfiguration";
	
	
	
	private static AndHow singleInstance;
	private static final Object lock = new Object();
	
	AndHowCore core;
	Reloader reloader;
	
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
			List<Class<? extends BasePropertyGroup>> registeredGroups)
			throws AppFatalException {
		core = new AndHowCore(naming, loaders, registeredGroups);
		reloader = new Reloader(this);
	}
	
	/**
	 * Returns a builder that can be used one time to build the AndHow instance.
	 * @return A builder instance
	 */
	public static AndHowBuilder builder() {
		return new AndHowBuilder();
	}
	
	public static AndHow instance() {
		if (singleInstance != null && singleInstance.core != null) {
			return singleInstance;
		} else {
			throw new RuntimeException(ANDHOW_INLINE_NAME + " has not been initialized.  " +
					"Possible causes:  1) There is a race condition where Property access may happen before configuration " +
					"2) There is no configuration at the entry point to the application. " +
					"Refer to " + ANDHOW_URL + " for code examples and FAQs.");
		}
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
	 * @param cmdLineArgs
	 * @return
	 * @throws AppFatalException 
	 */
	private static Reloader build(
			NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends BasePropertyGroup>> registeredGroups)
			throws AppFatalException, RuntimeException {

		synchronized (lock) {
			if (singleInstance != null) {
				throw new RuntimeException("Already constructed!");
			} else {
				singleInstance = new AndHow(naming, loaders, registeredGroups);
				return singleInstance.reloader;
			}
		}

	}
	
	//
	//ValueMap Interface
	
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
	public Class<? extends BasePropertyGroup> getGroupForProperty(Property<?> prop) {
		return core.getGroupForProperty(prop);
	}

	@Override
	public List<Property<?>> getPropertiesForGroup(Class<? extends BasePropertyGroup> group) {
		return core.getPropertiesForGroup(group);
	}

	@Override
	public Property<?> getProperty(String name) {
		return core.getProperty(name);
	}

	@Override
	public List<Class<? extends BasePropertyGroup>> getPropertyGroups() {
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
	
	@Override
	public Map<String, String> getSystemEnvironment() {
		return core.getSystemEnvironment();
	}

	/**
	 * A builder class, which is the only supported way to construct an AndHow instance.
	 * 
	 * Once an AndHow instance is built, it can never be rebuilt or reconfigured**.,
	 * 
	 * The builder code is intended to be concise and readable in use.  Thus,
	 * 'set' or 'add' is dropped from method name prefixes where possible.
	 * 
	 * For single valued properties, like namingStrategy, calling namingStrategy(new ANamingStrategy())
	 * sets the value.  For attributes that takes lists of of values, there are
	 * methods that add single values or multiple values, e.g. for PropertyGroups, 
	 * calling <code>builder.group(SomeGroup.class)</code> would add a single group,
	 * <code>builder.groups(aListOfGroups)</code> would add an entire list.
	 * 
	 * Usage always starts with AndHow.builder() and ends with build():
	 * <pre>
	 * {@code 
 		AndHow.builder()
			.loader(new PropFileLoader)
			.group(SomeGroup.class)
			.group(SomeOtherGroup.class)
			.cmdLineArgs([Array of cmd line arguments])
			.build();
 }
	 * </pre>
	 * 
	 * There is no return value because there is no need to hold a reference
	 * to anything past framework startup.  After a successful startup, Property
	 * values can be read directly.  For instance, for a Property named 'MyInt':
	 * {@code Integer value = MyInt.getValue();}

 Attempting to call build() a 2nd time will throw a RuntimeException, so
 it is important that a single entry and configuration loading point to
 your application is well defined.
 
 **See buildForNonPropduction() for a small backdoor for unit testing, which
 provides a non-production, unsupported way to force the AndHow framework
 to reload its state.
	 * 
	 * @author eeverman
	 */
	public static class AndHowBuilder {
		//User config
		private final List<Loader> _loaders = new ArrayList();
		private NamingStrategy _namingStrategy = null;
		private final List<String> _cmdLineArgs = new ArrayList();
		List<Class<? extends BasePropertyGroup>> _groups = new ArrayList();
		
		//
		//Internal state
		
		//The position at which the cmd line loader should be added.
		//May be null if not needed.
		private Integer addCmdLineLoaderAtPosition = null;

		/**
		 * Add a loader to the list of loaders.  Loaders are used in the order added.
		 * @param loader
		 * @return 
		 */
		public AndHowBuilder loader(Loader loader) {
			_loaders.add(loader);
			return this;
		}

		/**
		 * Add a list of loaders to the list being built.  Loaders are used in the order added.
		 * @param loaders
		 * @return 
		 */
		public AndHowBuilder loaders(Collection<Loader> loaders) {
			this._loaders.addAll(loaders);
			return this;
		}

		/**
		 * Add a group to the list of groups being built.
		 * 
		 * Group order makes no difference, but for error reports and sample
		 * configuration files, the order is preserved.
		 * 
		 * @param group
		 * @return 
		 */
		public AndHowBuilder group(Class<? extends BasePropertyGroup> group) {
			_groups.add(group);
			return this;
		}

		/**
		 * Add a list of groups to the list of groups being built.
		 * 
		 * Group order makes no difference, but for error reports and sample
		 * configuration files, the order is preserved.
		 * 
		 * @param groups
		 * @return 
		 */
		public AndHowBuilder groups(Collection<Class<? extends BasePropertyGroup>> groups) {
			this._groups.addAll(groups);
			return this;
		}

		/**
		 * Adds the command line arguments, keeping any previously added.
		 * 
		 * Note that adding cmd line args implicitly add a StringArgumentLoader
		 * at the point in code where the first cmd line argument is added, thus
		 * determining the load order of cmd line arguments in relation to other
		 * loaders.
		 * 
		 * @param commandLineArgs
		 * @return 
		 */
		public AndHowBuilder cmdLineArgs(String[] commandLineArgs) {
			_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
			
			//Record where the cmd line loader should go, if not already determined
			if (addCmdLineLoaderAtPosition == null) {
				addCmdLineLoaderAtPosition = _loaders.size();
			}
			
			return this;
		}

		/**
		 * Adds a command line argument in key=value form.
		 * 
		 * If the value is null, only the key is added (ie its a flag).
		 * 
		 * @param key
		 * @param value
		 * @return 
		 */
		public AndHowBuilder cmdLineArg(String key, String value) {
			
			if (value != null) {
				_cmdLineArgs.add(key + StringArgumentLoader.KVP_DELIMITER + value);
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
		 * Sets the naming strategy, which determines how the property names
		 * are realized when used in config files, JNDI and cmd line arguments.
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
		 * Builds and throws an AppFatalException.
		 * The stack trace is edited to remove 2 method calls, which should put
		 * the stacktrace at the user code of the build.
		 * @param message 
		 */
		private void throwFatal(String message) {
			AppFatalException afe = new AppFatalException(message);
			StackTraceElement[] stes = afe.getStackTrace();
			stes = Arrays.copyOfRange(stes, 2, stes.length);
			afe.setStackTrace(stes);
			throw afe;
		}
		
		/**
		 * Executes the AndHow framework startup.
		 * 
		 * There is no return value because there is no need to hold a reference
		 * to anything past framework startup.  After a successful startup, Property
		 * values can be read directly.  For instance, for a property named 'MyInt':
		 * {@code Integer value = MyInt.getValue();}
		 * 
		 * @throws AppFatalException If the startup fails.
		 */
		public void build() throws AppFatalException {

			//If the user added cmd line args, add a loader for them at the correct
			//position wrt other loaders.
			if (addCmdLineLoaderAtPosition != null) {
				_loaders.add(addCmdLineLoaderAtPosition, new StringArgumentLoader(_cmdLineArgs));
			}
			
			AndHow.build(_namingStrategy, _loaders, _groups);
		}

		/**
		 * Bootstraps the AndHow framework and returns a Reloader that can be used
		 * to destroy or reload the AndHow framework.
		 * 
		 * Destroying or reloading is strictly for testing - Support for
		 * reloading, including dealing with the issue of inconsistent reads of
		 * the data, is not in place.
		 * 
		 * Don't use this method in production, just use build(), which doesn't
		 * return the reloader.
		 * 
		 * This method may be removed in a future version.  You have been warned.
		 * 
		 * @return
		 * @throws AppFatalException 
		 */
		public AndHow.Reloader buildForNonPropduction() throws AppFatalException {
			
			//If the user added cmd line args, add a loader for them at the correct
			//position wrt other loaders.
			if (addCmdLineLoaderAtPosition != null) {
				_loaders.add(addCmdLineLoaderAtPosition, new StringArgumentLoader(_cmdLineArgs));
			}
			
			return AndHow.build(_namingStrategy, _loaders, _groups);
		}
		
		/**
		 * After initial construction with buildForNonPropduction() this method 
		 * forces a reload using the reloader instance.
		 * 
		 * Not for production, @See buildForNonPropduction
		 * 
		 * @param reloader Must be the same instance given out by buildForNonPropduction.
		 * @throws AppFatalException 
		 */
		public void reloadForNonPropduction(AndHow.Reloader reloader) throws AppFatalException {
			
			//If the user added cmd line args, add a loader for them at the correct
			//position wrt other loaders.
			if (addCmdLineLoaderAtPosition != null) {
				_loaders.add(addCmdLineLoaderAtPosition, new StringArgumentLoader(_cmdLineArgs));
			}
			
			reloader.reload(_namingStrategy, _loaders, _groups);
		}

	}


	public static class Reloader {
		private final AndHow instance;
		
		private Reloader(AndHow instance) {
			this.instance = instance;
		}
		/**
		 * Forces a reload of the AndHow state.
		 * 
		 * This may someday support production reloading of values, but for now
		 * it is really just a means for testing w/o having to deal with new
		 * classloaders for the singleton instancing.
		 * 
		 * @param naming
		 * @param loaders
		 * @param registeredGroups
		 * @param cmdLineArgs
		 * @throws AppFatalException 
		 */
		public void reload(NamingStrategy naming, List<Loader> loaders, 
				List<Class<? extends BasePropertyGroup>> registeredGroups) 
				throws AppFatalException {
			
			synchronized (AndHow.lock) {
				instance.core = new AndHowCore(naming, loaders, registeredGroups);
			}
		}
		
		/**
		 * For shutdown or testing.
		 * 
		 * Flushes the internal state, making the AndHow appear unconfigured.
		 */
		public void destroy() {
			
			synchronized (AndHow.lock) {
				if (instance != null) {
					instance.core = null;
				}
			}
		}
	}

}
