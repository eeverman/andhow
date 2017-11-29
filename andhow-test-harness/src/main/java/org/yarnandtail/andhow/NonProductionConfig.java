package org.yarnandtail.andhow;

import java.util.*;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.StdConfig.StdConfigAbstract;
import org.yarnandtail.andhow.api.Loader;

/**
 *
 * @author ericeverman
 * @param <N>
 */
public class NonProductionConfig {
	
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
			AndHowNonProductionUtil.forceRebuild(
					this.getNamingStrategy(),
					this.buildLoaders(),
					getRegisteredGroups());
		}
	}
}
