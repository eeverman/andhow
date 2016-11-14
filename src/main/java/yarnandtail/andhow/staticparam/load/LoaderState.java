package yarnandtail.andhow.staticparam.load;

import java.util.List;
import java.util.Map;
import yarnandtail.andhow.staticparam.ConfigPoint;

/**
 * State of the loading process that the chain of loaders can access.
 * @author eeverman
 */
public interface LoaderState {
	String[] getCmdLineArgs();
	List<Map<ConfigPoint, String>> getExistingValues();
	Map<String, ConfigPoint> getRegisteredConfigPoints();
	List<LoaderException> getLoaderExceptions();
}
