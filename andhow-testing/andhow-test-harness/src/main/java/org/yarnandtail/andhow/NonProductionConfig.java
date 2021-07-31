package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.StdConfig.StdConfigAbstract;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.load.KeyValuePairLoader;

/**
 * An {@code AndHowConfiguration} implementation that allows 'breaking the rules'
 * to make unit testing of applications possible.
 * <p>
 * In particular, this configuration implementation allows the caller to supply
 * the list of {@code Groups} and by extension the list of {@code Property}s that
 * are known to AndHow.  It also provides a simple method to force AndHow to
 * reload based on a specific configuration, which is a common need during testing.
 * <p>
 * Typical usage inside of a JUnit 5 test method:
 * <pre>{@code
 *
 * @Test
 * @KillAndHowBeforeThisTest
 * public void myTest() {
 *   NonProductionConfigImpl config = NonProductionConfig.instance();
 *   ...Do some stuff like set groups...
 *   AndHow.setConfig(config);
 *   AndHow.instance();
 *
 *   ...My assertions
 * }
 * }</pre>
 * 
 * @author ericeverman
 */
public class NonProductionConfig {
	
	/**
	 * Returns a new instance of a {@code NonProductionConfig} implemented by
	 * an implementing inner class.
	 * 
	 * @return 
	 */
	public static NonProductionConfigImpl instance() {
		return new NonProductionConfigImpl();
	}
	
	public static final class NonProductionConfigImpl extends NonProductionConfigAbstract<NonProductionConfigImpl> {
		
	}
	
	public static abstract class NonProductionConfigAbstract<N extends StdConfigAbstract<N>> extends StdConfigAbstract<N> {

		//
		// If loaders and the related methods change, be sure to
		// update AndHowTestConfig as well.  Unfortunately must be duplicate code. :-(

		//If non-null, it overrides the default list of loaders
		protected List<Loader> _loaders = null;

		// //

		/**
		 * Adds a command line argument in key=value form.
		 *
		 * If the value is null, only the key is added (ie its a flag).
		 * This method is added as a convenience to the NonProductionConfig
		 * because during testing command line arguments are often simulated
		 * rather than passed in as a set.
		 *
		 * @param key The property canonical name or alias.
		 * @param value The value.
		 * @return The same NonProductionConfig instance to continue configuring.
		 */
		public N addCmdLineArg(String key, String value) {

			if (key == null) {
				throw new RuntimeException("The key cannot be null");
			}

			if (value != null) {
				_cmdLineArgs.add(key + KeyValuePairLoader.KVP_DELIMITER + value);
			} else {
				_cmdLineArgs.add(key);
			}

			return (N) this;
		}

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
		 * Add a group to a custom list of 'Groups' (classes or interfaces containing
		 * AndHow Properties) to use instead of allowing the auto-discovery to find the Groups.
		 *
		 * @deprecated Use {@link #addOverrideGroups(Collection)}
		 * @param group A group (a classe) to add to those known to AndHow.
		 * @return This configuration instance for fluent configuration.
		 */
		@Deprecated
		public N group(Class<?> group) {
			return addOverrideGroup(group);
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

			overrideGroups.addAll(groups);
			return (N) this;
		}

		/**
		 * Add a collection of groups to a custom list of 'Groups' (classes or interfaces containing
		 * AndHow Properties) to use instead of allowing the auto-discovery to find the Groups.
		 *
		 * @deprecated Use {@link #addOverrideGroups(Collection)}
		 * @param groups A collection of groups (classes) to add to those known to AndHow.
		 * @return This configuration instance for fluent configuration.
		 */
		@Deprecated
		public N groups(Collection<Class<?>> groups) {
			return addOverrideGroups(groups);
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
		 * Forces a rebuild, dumping the previous configuration and creating a
		 * new AndHowCore, which contains all AndHow state, and using that.
		 * <p>
		 * This method is not safe for production, but that is obvious from the
		 * name of this class.
		 */
		public void forceBuild() {
			AndHowNonProductionUtil.forceRebuild(this);
		}
	}
}
