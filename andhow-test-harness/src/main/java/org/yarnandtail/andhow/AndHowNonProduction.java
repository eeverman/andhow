package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.load.CommandLineArgumentLoader;
import org.yarnandtail.andhow.load.KeyValuePairLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 *
 * @author ericeverman
 */
public class AndHowNonProduction {
	
	/**
	 * Returns a builder that can be used one time to build the AndHow instance.
	 *
	 * @return A builder instance
	 */
	public static AndHowBuilder builder() {
		return new AndHowBuilder();
	}
	
	private static List<GroupProxy> convertClassesToGroups(Collection<Class<?>> registeredGroups)
			throws AppFatalException, RuntimeException {

		final ProblemList<Problem> problems = new ProblemList();
		final List<GroupProxy> groupProxies = new ArrayList();

		for (Class<?> clazz : registeredGroups) {

			try {
				GroupProxy gp = AndHowUtil.buildGroupProxy(clazz);
				groupProxies.add(gp);
			} catch (Exception ex) {
				problems.add(new ConstructionProblem.SecurityException(ex, Options.class));
			}

		}

		if (problems.isEmpty()) {
			return groupProxies;
		} else {
			AppFatalException afe = new AppFatalException(
					"There is a problem converting the AndHow Properties contained in the registered "
					+ "groups - likely this is a security issue.",
					problems);
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
	 * .cmdLineArgs([Array of cmd line arguments])
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
		private final List<Class<?>> _groups = new ArrayList();

		//
		//Internal state
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
		 * Add a group to the list of groups being built.
		 *
		 * Group order makes no difference, but for error reports and sample
		 * configuration files, the order is preserved.
		 *
		 * @param group
		 * @return
		 */
		public AndHowBuilder group(Class<?> group) {
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
		public AndHowBuilder groups(Collection<Class<?>> groups) {
			this._groups.addAll(groups);
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
			_cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
			
			if (getCmdLineLoaderIndex() < 0) {
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

			if (getCmdLineLoaderIndex() < 0) {
				//Record where the cmd line loader should go, if not already determined
				if (addCmdLineLoaderAtPosition == null) {
					addCmdLineLoaderAtPosition = _loaders.size();
				}
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
		 * Builds and throws an AppFatalException. The stack trace is edited to
		 * remove 2 method calls, which should put the stacktrace at the user
		 * code of the build.
		 *
		 * @param message
		 */
		private void throwFatal(String message, Throwable throwable) {
			
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
		 * Executes the AndHow framework startup.
		 *
		 * There is no return value because there is no need to hold a reference
		 * to anything past framework startup. After a successful startup,
		 * Property values can be read directly. For instance, for a property
		 * named 'MyInt': {@code Integer value = MyInt.getValue();}
		 *
		 * @return 
		 * @throws AppFatalException If the startup fails.
		 */
		public AndHowBuilder build() {
			
			populateLoaderList();
			//destroy();	//kill the 'core' of the existing AndHow instance if it is initialized
			
			List<GroupProxy> registeredGroups = null;
			
			if (_groups == null || _groups.isEmpty()) {
				PropertyRegistrarLoader registrar = new PropertyRegistrarLoader();
				registeredGroups = registrar.getGroups();
			} else {
				registeredGroups = AndHowUtil.buildGroupProxies(_groups);
			}
			
			if (_namingStrategy == null) {
				_namingStrategy = new CaseInsensitiveNaming();
			}
			
			AndHowNonProductionUtil.forceRebuild(_namingStrategy, _loaders, registeredGroups);
			
			return this;
		}
		

		/**
		 * Forces a reload using all the same naming, loaders and registered
		 * groups.
		 *
		 * Values will be reread, including the command line arguments that are
		 * passed. If the cmdLineArgs are nonNull, a new ComandLineArgLoader
		 * will be constructed with those argument and it will replace the
		 * previous ComandLineArgLoader. If there is no ComandLineArgLoader
		 * configured, this will have no effect (ie. the loader will not be
		 * added if it was not already existing in the list of loaders.
		 *
		 * @param cmdLineArgs
		 * @return 
		 * @throws java.lang.Exception 
		 * @throws AppFatalException
		 */
		public AndHowBuilder reloadValues(String[] cmdLineArgs) throws Exception {

			_cmdLineArgs.clear();
			this.addCmdLineArgs(cmdLineArgs);
			
			return build();
		}
		
		/**
		 * Reloads AndHow using the default loading strategy.
		 * 
		 * @param cmdLineArgs
		 * @throws AppFatalException 
		 */
		public AndHowBuilder buildDefaultInstance(String[] cmdLineArgs)
				throws Exception {
			
			//Clear out the builder config
			_loaders.clear();
			_namingStrategy = null;
			_groups.clear();
			
			if (cmdLineArgs != null) {
				_cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
			} else {
				_cmdLineArgs.clear();
			}
					
			build();
			
			return this;
		}

		/**
		 * For shutdown or testing.
		 *
		 * Flushes the internal state, making the AndHow appear unconfigured.
		 */
		public void destroyAndHow() {
			AndHowNonProductionUtil.destroyAndHow();
		}
		
		/**
		 * Returns the index of cmdLineLoader in the current list of loaders, if
		 * it is in the list.  If it is not in the list, -1 is returned.
		 * 
		 * @return 
		 */
		private int getCmdLineLoaderIndex() {
			for (int i = 0; i < _loaders.size(); i++) {
				if (_loaders.get(i) instanceof CommandLineArgumentLoader) {
					return i;
				}
			}

			return -1;
		}
		
		private void populateLoaderList() {
			if (_loaders.isEmpty()) {
				_loaders.addAll(AndHow.getDefaultLoaders(_cmdLineArgs.toArray(new String[_cmdLineArgs.size()])));
			} else {
				
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

}
