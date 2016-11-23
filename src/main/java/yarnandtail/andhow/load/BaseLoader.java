package yarnandtail.andhow.load;

import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.Loader;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends ConfigPointGroup> getLoaderConfig() {
		return null;
	}
	
}
