package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.ConfigPoint;

/**
 * State of the loading process that the chain of loaders can access.
 * @author eeverman
 */
public class LoaderState {
	public List<String> cmdLineArgs;
	List<Map<ConfigPoint<?>, Object>> existingValues;
	Map<String, ConfigPoint<?>> registeredConfigPoints;
	List<LoaderException> loaderExceptions = new ArrayList();

	public LoaderState(List<String> cmdLineArgs, List<Map<ConfigPoint<?>, Object>> existingValues, Map<String, ConfigPoint<?>> registeredConfigPoints) {
		this.cmdLineArgs = cmdLineArgs;
		this.existingValues = existingValues;
		this.registeredConfigPoints = registeredConfigPoints;
	}
	
	public List<String> getCmdLineArgs() {
		return cmdLineArgs;
	}

	public List<Map<ConfigPoint<?>, Object>> getExistingValues() {
		return existingValues;
	}

	public Map<String, ConfigPoint<?>> getRegisteredConfigPoints() {
		return registeredConfigPoints;
	}

	public List<LoaderException> getLoaderExceptions() {
		return loaderExceptions;
	}
	
	
}
