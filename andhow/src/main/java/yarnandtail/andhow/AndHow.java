package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import yarnandtail.andhow.internal.AndHowCore;
import java.util.List;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AndHow implements ValueMap {
	
	//
	//A few app-wide constants
	public static final String ANDHOW_INLINE_NAME = "AndHow";
	public static final String ANDHOW_NAME = ANDHOW_INLINE_NAME + "!";
	public static final String ANDHOW_URL = "https://github.com/eeverman/andhow";
	public static final String ANDHOW_TAG_LINE = "strong.valid.simple.AppConfiguration";
	
	/**
	 * In text formats, this is the default delimiter between a key and a value.
	 * Known usage:  The CmdLineLoader uses this value to parse values.
	 */
	public static final String KVP_DELIMITER = "=";
	
	
	private static AndHow singleInstance;
	private static final Object lock = new Object();
	
	AndHowCore core;
	Reloader reloader;
	
	private AndHow(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends PropertyGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PropertyValue> startingValues)
			throws AppFatalException {
		core = new AndHowCore(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
		reloader = new Reloader(this);
	}
	
	/**
	 * Returns a builder that can be used one time to build the AndHow instance.
	 * @return 
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
	 * durint unit test (not for production).
	 * 
	 * @param naming
	 * @param loaders
	 * @param registeredGroups
	 * @param cmdLineArgs
	 * @param startingValues
	 * @return
	 * @throws AppFatalException 
	 */
	private static Reloader build(
			NamingStrategy naming, List<Loader> loaders, List<Class<? extends PropertyGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PropertyValue> startingValues) throws AppFatalException, RuntimeException {

		synchronized (lock) {
			if (singleInstance != null) {
				throw new RuntimeException("Already constructed!");
			} else {
				singleInstance = new AndHow(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
				return singleInstance.reloader;
			}
		}

	}
	
	public List<Class<? extends PropertyGroup>> getGroups() {
		return core.getPropertyGroups();
	}

	public List<Property<?>> getPoints() {
		return core.getProperties();
	}
	
	@Override
	public boolean isExplicitlySet(Property<?> point) {
		return core.isExplicitlySet(point);
	}
	
	@Override
	public <T> T getExplicitValue(Property<T> point) {
		return core.getExplicitValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(Property<T> point) {
		return core.getEffectiveValue(point);
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
	 * sets the value.  For list of values, like groups, calling group(SomeGroup.class)
	 * would add the group to the list.
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
	 * values can be read directly.  For instance, for an IntConfigPoint named 'MyInt':
	 * {@code Integer value = MyInt.getValue();}

	 * Attempting to call build() a 2nd time will throw a RuntimeException, so
	 * it is important that a single entry and configuration loading point to
	 * your application is well defined.
	 * 
	 * **See buildForUnitTesting() for a small backdoor for unit testing, which
	 * provides a non-production, unsupported way to force the AndHow framework
	 * to reload its state.
	 * 
	 * @author eeverman
	 */
	public static class AndHowBuilder {
		//User config
		private final ArrayList<PropertyValue> _forcedValues = new ArrayList();
		private final List<Loader> _loaders = new ArrayList();
		private NamingStrategy _namingStrategy = new BasicNamingStrategy();
		private final List<String> _cmdLineArgs = new ArrayList();
		List<Class<? extends PropertyGroup>> _groups = new ArrayList();

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
		public AndHowBuilder group(Class<? extends PropertyGroup> group) {
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
		public AndHowBuilder groups(Collection<Class<? extends PropertyGroup>> groups) {
			this._groups.addAll(groups);
			return this;
		}

		/**
		 * Force a Property to have a specific value.
		 * 
		 * This is implemented by including an implicit fixed value loader that
		 * loads these fixed values before the other loaders run.  First loaded
		 * value wins, so this effectively forces the value to be as configured here.
		 * 
		 * @param property
		 * @param value
		 * @return 
		 */
		public AndHowBuilder forceValue(Property<?> property, Object value) {
			_forcedValues.add(new PropertyValue(property, value));
			return this;
		}

		/**
		 * Force a list of Properties to have specific values.
		 * 
		 * This is implemented by including an implicit fixed value loader that
		 * loads these fixed values before the other loaders run.  First loaded
		 * value wins, so this effectively forces the value to be as configured here.
		 * 
		 * @param startVals A list w/ Properties and values bundled into a PropertyValue.
		 * @return 
		 */
		public AndHowBuilder forceValues(List<PropertyValue> startVals) {
			this._forcedValues.addAll(startVals);
			return this;
		}

		/**
		 * Adds the command line arguments to the list being build of cmd line args.
		 * 
		 * @param commandLineArgs
		 * @return 
		 */
		public AndHowBuilder cmdLineArgs(String[] commandLineArgs) {
			_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
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
				_cmdLineArgs.add(key + AndHow.KVP_DELIMITER + value);
			} else {
				_cmdLineArgs.add(key);
			}
			return this;
		}

		/**
		 * Sets the naming strategy, which determines how the property names
		 * are realized when used in config files, JNDI and cmd line arguments.
		 * 
		 * If unspecified, BasicNamingStrategy is used.
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
		 * to anything past framework startup.  After a successful startup, Property
		 * values can be read directly.  For instance, for an IntConfigPoint named 'MyInt':
		 * {@code Integer value = MyInt.getValue();}
		 * 
		 * @throws AppFatalException If the startup fails.
		 */
		public void build() throws AppFatalException {
			String[] args = _cmdLineArgs.toArray(new String[_cmdLineArgs.size()]);
			AndHow.build(_namingStrategy, _loaders, _groups,  args, _forcedValues);
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
		 * @deprecated Don't use in production code
		 */
		public AndHow.Reloader buildForUnitTesting() throws AppFatalException {
			String[] args = _cmdLineArgs.toArray(new String[_cmdLineArgs.size()]);
			return AndHow.build(_namingStrategy, _loaders, _groups,  args, _forcedValues);
		}
		
		/**
		 * After initial construction with buildForUnitTesting() this method 
		 * forces a reload using the reloader instance.
		 * 
		 * Not for production, @See buildForUnitTesting
		 * 
		 * @param reloader Must be the same instance given out by buildForUnitTesting.
		 * @throws AppFatalException 
		 */
		public void reloadForUnitTesting(AndHow.Reloader reloader) throws AppFatalException {
			String[] args = _cmdLineArgs.toArray(new String[_cmdLineArgs.size()]);
			reloader.reload(_namingStrategy, _loaders, _groups,  args, _forcedValues);
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
		 * @param forcedValues
		 * @throws AppFatalException 
		 */
		public void reload(NamingStrategy naming, List<Loader> loaders, 
				List<Class<? extends PropertyGroup>> registeredGroups, String[] cmdLineArgs, 
				List<PropertyValue> forcedValues) 
				throws AppFatalException {
			
			synchronized (AndHow.lock) {
				instance.core = new AndHowCore(naming, loaders, registeredGroups, cmdLineArgs, forcedValues);
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
