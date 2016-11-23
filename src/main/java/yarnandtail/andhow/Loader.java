package yarnandtail.andhow;

import java.util.Map;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.load.LoaderState;

/**
 *
 * @author eeverman
 */
public interface Loader {
	Map<ConfigPoint<?>, Object> load(LoaderState state);
	
	/**
	 * A group of ConfigPoints used to control the loader's behavior, such
	 * the location of a properties file to load from.
	 * 
	 * @return 
	 */
	Class<? extends ConfigPointGroup> getLoaderConfig();
}
