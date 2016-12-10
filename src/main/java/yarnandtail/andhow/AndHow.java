package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import yarnandtail.andhow.appconfig.AppConfigCore;
import java.util.List;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AndHow implements ValueMap {
	
	/**
	 * In text formats, this is the default delimiter between a key and a value.
	 * Known usage:  The CmdLineLoader uses this value to parse values.
	 */
	public static final String KVP_DELIMITER = "=";
	
	
	private static AndHow singleInstance;
	private static final Object lock = new Object();
	
	AppConfigCore core;
	Reloader reloader;
	
	private AndHow(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues)
			throws AppFatalException {
		core = new AppConfigCore(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
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
			throw new RuntimeException("AppConfig has not been initialized.  " +
					"Possible causes:  1) There is a race condition where ConfigPoint access may happen before configuration " +
					"2) There is no configuration at the entry point to the application. " +
					"Refer to " + ReportGenerator.ANDHOW_URL + " for code examples and FAQs.");
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
			NamingStrategy naming, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues) throws AppFatalException, RuntimeException {

		synchronized (lock) {
			if (singleInstance != null) {
				throw new RuntimeException("Already constructed!");
			} else {
				singleInstance = new AndHow(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
				return singleInstance.reloader;
			}
		}

	}
	
	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return core.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return core.getPoints();
	}
	
	@Override
	public boolean isExplicitlySet(ConfigPoint<?> point) {
		return core.isExplicitlySet(point);
	}
	
	@Override
	public <T> T getExplicitValue(ConfigPoint<T> point) {
		return core.getExplicitValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return core.getEffectiveValue(point);
	}
	

	/**
	 * A builder class, which is the only supported way to construct an AndHow instance.
	 * 
	 * Once an AndHow instance is built, it can never be rebuilt or reconfigured,
	 * with the small exception of a unit testing.
	 * 
	 * Generally <em>addXXX</em> adds to a collection and  <em>setXXX</em> replaces 
	 * the value or values.
	 * 
	 * Usage always starts with AndHow.builder() and ends with build():
	 * <pre>
	 * {@code 
	 * 		AndHow.builder()
	 *			.setNamingStrategy(basicNaming)
	 *			.addLoaders(loaders)
	 *			.addGroups(configPtGroups)
	 *			.setCmdLineArgs(cmdLineArgsWExplicitName)
	 *			.build();
	 * }
	 * </pre>
	 * 
	 * build() returns an AppConfig.Reloader object.  If you do need to reload the
	 * AppConfig at some point (primarily for testing, not production), you will need
	 * to keep a reference to that Reloader.  An overloaded version of build(Reloader)
	 * expects that same instance of the reloader to allow the AppConfig to reload.
	 * 
	 * Attempting to call build() a 2nd time w/o the reloader instance will
	 * cause a RuntimeException.
	 * 
	 * @author eeverman
	 */
	public static class AndHowBuilder {
		//User config
		private final ArrayList<PointValue> forcedValues = new ArrayList();
		private final List<Loader> loaders = new ArrayList();
		private NamingStrategy namingStrategy = new BasicNamingStrategy();
		private final List<String> cmdLineArgs = new ArrayList();
		List<Class<? extends ConfigPointGroup>> groups = new ArrayList();

		public AndHowBuilder addLoader(Loader loader) {
			loaders.add(loader);
			return this;
		}

		public AndHowBuilder addLoaders(Collection<Loader> loaders) {
			this.loaders.addAll(loaders);
			return this;
		}

		public AndHowBuilder addGroup(Class<? extends ConfigPointGroup> group) {
			groups.add(group);
			return this;
		}

		public AndHowBuilder addGroups(Collection<Class<? extends ConfigPointGroup>> groups) {
			this.groups.addAll(groups);
			return this;
		}

		public AndHowBuilder addForcedValue(ConfigPoint<?> point, Object value) {
			forcedValues.add(new PointValue(point, value));
			return this;
		}

		/**
		 * Alternative to adding individual forced values if you already have them
		 * in a list
		 * @param startVals
		 * @return 
		 */
		public AndHowBuilder addForcedValues(List<PointValue> startVals) {
			this.forcedValues.addAll(startVals);
			return this;
		}

		/**
		 * Sets the command line arguments and clears out any existing cmd line values.
		 * 
		 * @param commandLineArgs
		 * @return 
		 */
		public AndHowBuilder setCmdLineArgs(String[] commandLineArgs) {
			cmdLineArgs.clear();
			cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
			return this;
		}

		/**
		 * Adds a command line argument.
		 * 
		 * Note that values added this way are overwritten if setCmdLineArgs(String[]) is called.
		 * @param key
		 * @param value
		 * @return 
		 */
		public AndHowBuilder addCmdLineArg(String key, String value) {
			cmdLineArgs.add(key + AndHow.KVP_DELIMITER + value);
			return this;
		}

		public AndHowBuilder setNamingStrategy(NamingStrategy namingStrategy) {
			this.namingStrategy = namingStrategy;
			return this;
		}
		
		/**
		 * Executes the AndHow framework startup.
		 * 
		 * There is no return value because there is no need to hold a reference
		 * to anything past framework startup.  After a successful startup, ConfigPoint
		 * values can be read directly.  For instance, for an IntConfigPoint named 'MyInt':
		 * {@code Integer value = MyInt.getValue();}
		 * 
		 * @throws AppFatalException If the startup fails.
		 */
		public void build() throws AppFatalException {
			String[] args = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
			AndHow.build(namingStrategy, loaders, groups,  args, forcedValues);
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
			String[] args = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
			return AndHow.build(namingStrategy, loaders, groups,  args, forcedValues);
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
			String[] args = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
			reloader.reload(namingStrategy, loaders, groups,  args, forcedValues);
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
				List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, 
				List<PointValue> forcedValues) 
				throws AppFatalException {
			
			synchronized (AndHow.lock) {
				instance.core = new AppConfigCore(naming, loaders, registeredGroups, cmdLineArgs, forcedValues);
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
