package org.yarnandtail.andhow;

import java.util.*;
import java.util.function.Supplier;

import org.yarnandtail.andhow.api.NamingStrategy;
import org.yarnandtail.andhow.StdConfig.StdConfigAbstract;
import org.yarnandtail.andhow.api.Loader;

/**
 * This class is an AndHowConfiguration implementation that lets you do things
 * you shouldn't be able to do, such as provide your own list of parameters rather
 * than letting AndHow self-discover them.
 * <p>
 * This class is intentionally placed in the test directory because it is
 * never intended to be distributed, not even for use by others in their testing.
 * <p>
 * <em>THIS CLASS DOES DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em>
 * <p>
 * For utilities to test your application code with AndHow, see the AndHow
 * Test Harness module.  For examples using the test harness, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 *
 */
public class AndHowTestConfig {
	
	public static AndHowTestConfigImpl instance() {
		return new AndHowTestConfigImpl();
	}
	
	public static final class AndHowTestConfigImpl extends AndHowTestConfigAbstract<AndHowTestConfigImpl> {
		
	}
	
	public static abstract class AndHowTestConfigAbstract<N extends StdConfigAbstract<N>> extends StdConfigAbstract<N> {

		//
		// If loaders and the related methods change, be sure to
		// update NonProductionConfig as well.  Unfortunately must be duplicate code. :-(
		
		//If non-null, it overrides the default list of loaders
		protected List<Loader> _loaders = null;

		// //

		/* A callback to simulate weird loops and contention during initializtion */
		private Supplier<Object> namingStrategyCallback;

		@Override
		public NamingStrategy getNamingStrategy() {

			if (namingStrategyCallback != null) {
				namingStrategyCallback.get();
			}

			return super.getNamingStrategy();
		}

		/**
		 * Adds a command line argument in key=value form.
		 *
		 * If the value is null, only the key is added (ie its a flag).
		 * This method is added as a convenience to the NonProductionConfig
		 * because during testing command line arguments are often simulated
		 * rather than passed in as a set.
		 *
		 * @param key
		 * @param value
		 * @return This configuration instance for fluent configuration.
		 */
		public N addCmdLineArg(String key, String value) {

			if (key == null) {
				throw new RuntimeException("The key cannot be null");
			}

			if (value != null) {
				loadEnvBuilder.getCmdLineArgs().add(key + "=" + value);
			} else {
				loadEnvBuilder.getCmdLineArgs().add(key);
			}

			return (N) this;
		}

		public N setEnvironmentVariables(Map<String, String> envVars) {
			loadEnvBuilder.setEnvVars(envVars);

			return (N)this;
		}

		public N setSystemProperties(Map<String, String> envVars) {
			loadEnvBuilder.setSysProps(envVars);

			return (N)this;
		}
		

		//
		// Update NonProductionConfig if there are any changes to Groups or Loaders....
		//

		/**
		 * Add a group to a custom list of 'Groups' (classes or interfaces containing AndHow Properties)
		 * to use instead of allowing the auto-discovery to find the Groups.
		 *
		 * Adding groups is optional - if no groups are added, auto-discovery will find them all.
		 * For testing, however, it can be useful to test with a subset of configuration groups.
		 *
		 * Group order makes no difference.
		 *
		 * @see AndHowConfiguration#getRegisteredGroups()
		 *
		 * @param group A group (a class) to add to those known to AndHow.
		 * @return This configuration instance for fluent configuration.
		 */
		public N addOverrideGroup(Class<?> group) {
			if (overrideGroups == null) overrideGroups = new ArrayList();

			overrideGroups.add(group);
			return (N) this;
		}

		/**
		 * Add a collection of groups to a custom list of 'Groups' (classes or interfaces containing
		 * AndHow Properties) to use instead of allowing the auto-discovery to find the Groups.
		 *
		 * Adding groups is optional - if no groups are added, auto-discovery will find them all.
		 * For testing, however, it can be useful to test with a subset of configuration groups.
		 *
		 * Group order makes no difference.
		 *
		 * @see AndHowConfiguration#getRegisteredGroups()
		 *
		 * @param groups A collection of groups (classes) to add to those known to AndHow.
		 * @return This configuration instance for fluent configuration.
		 */
		public N addOverrideGroups(Collection<Class<?>> groups) {
			if (overrideGroups == null) overrideGroups = new ArrayList();

			this.overrideGroups.addAll(groups);
			return (N) this;
		}

		/**
		 * Sets an exclusive list of loaders, bypassing all default loaders and
		 * ignoring any loaders added via insertBefore or insertAfter loaders.
		 * 
		 * @param loaders
		 * @return 
		 */
		public N setLoaders(Loader... loaders) {
			if (_loaders == null) _loaders = new ArrayList();

			_loaders.addAll(Arrays.asList(loaders));
			return (N) this;
		}

		@Override
		public List<Loader> buildLoaders() {
			if (_loaders != null) {
				return _loaders;
			} else {
				return super.buildLoaders();
			}
		}

		/**
		 * Set a callback that is called when getNamingStrategy() is called.
		 * This is to simulate unusual conditions during startup, such as init loops
		 * where the configuration calls findConfig, and other weird things for testing.
		 * @param callback
		 */
		public void setGetNamingStrategyCallback(Supplier<Object> callback) {
			namingStrategyCallback = callback;
		}
	}
}
