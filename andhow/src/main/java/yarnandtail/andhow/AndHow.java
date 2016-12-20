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
	public static final String ANDHOW_TAG_LINE = "strong.simple.valid.AppConfiguration";
	
	/**
	 * In text formats, this is the default delimiter between a key and a value.
	 * Known usage:  The CmdLineLoader uses this value to parse values.
	 */
	public static final String KVP_DELIMITER = "=";
	
	
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
			List<Class<? extends PropertyGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PropertyValue> forcedValues, 
			List<PropertyValue> defaultValues)
			throws AppFatalException {
		core = new AndHowCore(naming, loaders, registeredGroups, cmdLineArgs, 
				forcedValues, defaultValues);
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
	 * during unit test (not for production).
	 * 
	 * @param naming
	 * @param loaders
	 * @param registeredGroups
	 * @param cmdLineArgs
	 * @param forcedValues
	 * @return
	 * @throws AppFatalException 
	 */
	private static Reloader build(
			NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends PropertyGroup>> registeredGroups, String[] cmdLineArgs, 
			List<PropertyValue> forcedValues, List<PropertyValue> defaultValues) throws AppFatalException, RuntimeException {

		synchronized (lock) {
			if (singleInstance != null) {
				throw new RuntimeException("Already constructed!");
			} else {
				singleInstance = new AndHow(naming, loaders, registeredGroups, 
						cmdLineArgs, forcedValues, defaultValues);
				return singleInstance.reloader;
			}
		}

	}
	
	public List<Class<? extends PropertyGroup>> getGroups() {
		return core.getPropertyGroups();
	}

	public List<Property<?>> getProperties() {
		return core.getProperties();
	}
	
	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return core.isExplicitlySet(prop);
	}
	
	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		return core.getExplicitValue(prop);
	}
	
	@Override
	public <T> T getEffectiveValue(Property<T> prop) {
		return core.getEffectiveValue(prop);
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
		private final ArrayList<PropertyValue> _forcedValues = new ArrayList();
		private final ArrayList<PropertyValue> _defaultValues = new ArrayList();
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
		 * Avoid using the forceValue(s) methods - consider using the defaultValue(s)
		 * methods instead.  If a value is forced, all other configuration for it
		 * will be ignored and it essentially becomes a constant.
		 * This is the sledgehammer of setting values.
		 * 
		 * Why you might use this:  If a particular entry point to an application
		 * requires some guaranteed configuration unique to the entry point.
		 * For instance, a calculation module might be runnable as part of a
		 * cluster of servers that communicate together to process tasks, or it
		 * may run stand-alone from command-line startup.  For the command-line
		 * entry point to the application, you might force a property that turns
		 * off communication to other instances running in the cluster.
		 * 
		 * This is implemented by including an implicit fixed-value loader that
		 * loads values before the other loaders run.  The first loaded value wins,
		 * so this effectively forces values.
		 * 
		 * @param property The property to force
		 * @param value The value to set
		 * @return 
		 * @throws AppFatalException if there is already a default value assigned to this property
		 */
		public <T> AndHowBuilder forceValue(Property<T> property, T value) {

			if (checkForExistingProperty(_forcedValues, property)) {
				throwFatal("Cannot assign two forced values to a Property");
			}

			_forcedValues.add(new PropertyValue(property, value));
			return this;
		}

		/**
		 * Force a list of Properties to have a specific values.
		 * 
		 * Avoid using the forceValue(s) methods - consider using the defaultValue(s)
		 * methods instead.  If a value is forced, all other configuration for it
		 * will be ignored and it essentially becomes a constant.
		 * This is the sledgehammer of setting values.
		 * 
		 * Why you might use this:  If a particular entry point to an application
		 * requires some guaranteed configuration unique to the entry point.
		 * For instance, a calculation module might be runnable as part of a
		 * cluster of servers that communicate together to process tasks, or it
		 * may run stand-alone from command-line startup.  For the command-line
		 * entry point to the application, you might force a property that turns
		 * off communication to other instances running in the cluster.
		 * 
		 * This is implemented by including an implicit fixed-value loader that
		 * loads values before the other loaders run.  The first loaded value wins,
		 * so this effectively forces values.
		 * 
		 * @param forcedValues A list w/ Properties and values bundled into a PropertyValue.
		 * @return 
		 * @throws AppFatalException if there is already a default value assigned to any of these properties
		 */
		public AndHowBuilder forceValues(List<PropertyValue> forcedValues) {
			
			for (PropertyValue pv : forcedValues) {
				if (checkForExistingProperty(_forcedValues, pv.getProperty())) {
					throwFatal("Cannot assign two forced values to a Property");
				}
				
				_forcedValues.add(pv);
			}
			
			return this;
		}
		
		/**
		 * Sets a default value for the Property, overriding the default defined
		 * in the Property itself, if any.
		 * 
		 * Why you might use this:  There may be defaults that make sense for
		 * particular entry points of an application.  A module might default its
		 * output format to html when started as a web application and pdf when
		 * started from command-line.
		 * 
		 * Since Loaders like the PropFileLoader take parameters to configure them,
		 * you can easily set a default Property value here that would direct a
		 * loader to look for a specific config file for this entry point, such
		 * as cmd-line-startup.properties.
		 * 
		 * This is implemented by including an implicit fixed-value loader that
		 * loads values after the other loaders run.
		 * 
		 * @param property
		 * @param value
		 * @return 
		 * @throws AppFatalException if there is already a default value assigned to this property
		 */
		public <T> AndHowBuilder defaultValue(Property<T> property, T value) {

			if (checkForExistingProperty(_defaultValues, property)) {
				throwFatal("Cannot assign two default values to a Property");
			}
			_defaultValues.add(new PropertyValue(property, value));
			return this;
		}
		
		/**
		 * Sets default values for a list of Properties, overriding the defaults
		 * defined in the Property themselves, if any.
		 * 
		 * Why you might use this:  There may be defaults that make sense for
		 * particular entry points of an application.  A module might default its
		 * output format to html when started as a web application and pdf when
		 * started from command-line.
		 * 
		 * Since Loaders like the PropFileLoader take parameters to configure them,
		 * you can easily set a default Property value here that would direct a
		 * loader to look for a specific config file for this entry point, such
		 * as cmd-line-startup.properties.
		 * 
		 * This is implemented by including an implicit fixed-value loader that
		 * loads values after the other loaders run.
		 * 
		 * @param defaultVals A list w/ Properties and values bundled into a PropertyValue.
		 * @return 
		 * @throws AppFatalException if there is already a default value assigned to any of these properties
		 */
		public AndHowBuilder defaultValues(List<PropertyValue> defaultVals) {
			
			for (PropertyValue pv : defaultVals) {
				if (checkForExistingProperty(_defaultValues, pv.getProperty())) {
					throwFatal("Cannot assign two default values to a Property");
				}
				
				_defaultValues.add(pv);
			}
			
			return this;
		}
		
		private boolean checkForExistingProperty(List<PropertyValue> propList, Property toBeFound) {
			for (PropertyValue pv : propList) {
				if (pv.getProperty() ==  toBeFound) return true;
			}
			
			return false;
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
			String[] args = _cmdLineArgs.toArray(new String[_cmdLineArgs.size()]);
			AndHow.build(_namingStrategy, _loaders, _groups,  args, _forcedValues, _defaultValues);
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
			String[] args = _cmdLineArgs.toArray(new String[_cmdLineArgs.size()]);
			return AndHow.build(_namingStrategy, _loaders, _groups,  args, _forcedValues, _defaultValues);
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
			String[] args = _cmdLineArgs.toArray(new String[_cmdLineArgs.size()]);
			reloader.reload(_namingStrategy, _loaders, _groups,  args, _forcedValues, _defaultValues);
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
		 * @param defaultValues
		 * @throws AppFatalException 
		 */
		public void reload(NamingStrategy naming, List<Loader> loaders, 
				List<Class<? extends PropertyGroup>> registeredGroups, String[] cmdLineArgs, 
				List<PropertyValue> forcedValues, List<PropertyValue> defaultValues) 
				throws AppFatalException {
			
			synchronized (AndHow.lock) {
				instance.core = new AndHowCore(naming, loaders, registeredGroups, cmdLineArgs, forcedValues, defaultValues);
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
