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
 * Typical usage is {@code NonProductionConfig.instance()...}
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
		 * Add a group to the list of groups being built.
		 *
		 * Group order makes no difference, but for error reports and sample
		 * configuration files, the order is preserved.
		 *
		 * @param group A group (a class) to add to the known
		 * classes containing AndHow {@code Property} declarations.
		 * @return The same NonProductionConfig instance to continue configuring.
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
		 * @param groups A collection of groups (classes) to add to the known
		 * classes containing AndHow {@code Property} declarations.
		 * @return The same NonProductionConfig instance to continue configuring.
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
		 * @return The same NonProductionConfig instance to continue configuring.
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
