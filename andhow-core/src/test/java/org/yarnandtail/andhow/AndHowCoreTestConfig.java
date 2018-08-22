package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.StdConfig.StdConfigAbstract;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.load.KeyValuePairLoader;

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
 * @author ericeverman
 */
public class AndHowCoreTestConfig {
	
	public static NonProductionConfigImpl instance() {
		return new NonProductionConfigImpl();
	}
	
	public static final class NonProductionConfigImpl extends NonProductionConfigAbstract<NonProductionConfigImpl> {
		
	}
	
	public static abstract class NonProductionConfigAbstract<N extends StdConfigAbstract<N>> extends StdConfigAbstract<N> {
		
		//If non-empty, it overrides the default group discovery
		protected final List<Class<?>> _groups = new ArrayList();
		
		//If non-empty, it overrides the default list of loaders
		protected final List<Loader> _loaders = new ArrayList();
		

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
		 * @return
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
		 * Add a group to the list of groups being built.
		 *
		 * Group order makes no difference, but for error reports and sample
		 * configuration files, the order is preserved.
		 *
		 * @param group
		 * @return
		 */
		public N group(Class<?> group) {
			_groups.add(group);
			return (N) this;
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
		public N groups(Collection<Class<?>> groups) {
			this._groups.addAll(groups);
			return (N) this;
		}

		@Override
		public List<GroupProxy> getRegisteredGroups() {

			if (this._groups.size() > 0) {
				return AndHowUtil.buildGroupProxies(_groups);
			} else {
				PropertyRegistrarLoader registrar = new PropertyRegistrarLoader();
				List<GroupProxy> registeredGroups = registrar.getGroups();
				return registeredGroups;
			}
		}
		
		/**
		 * Sets an exclusive list of loaders, bypassing all default loaders and
		 * ignoring any loaders added via insertBefore or insertAfter loaders.
		 * 
		 * @param loaders
		 * @return 
		 */
		public N setLoaders(Loader... loaders) {
			_loaders.addAll(Arrays.asList(loaders));
			return (N) this;
		}

		@Override
		public List<Loader> buildLoaders() {
			if (_loaders.isEmpty()) {
				return super.buildLoaders();
			} else {
				return _loaders;
			}
		}

		public void forceBuild() {
			AndHowCoreTestUtil.forceRebuild(this);
		}
	}
}
