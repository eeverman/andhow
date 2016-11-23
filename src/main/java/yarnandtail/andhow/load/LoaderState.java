package yarnandtail.andhow.load;

import yarnandtail.andhow.LoaderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.AppConfigDefinition;
import yarnandtail.andhow.ConfigPoint;

/**
 * State of the loading process that the chain of loaders can access.
 * @author eeverman
 */
public class LoaderState {
	public List<String> cmdLineArgs;
	List<Map<ConfigPoint<?>, Object>> existingValues;
	AppConfigDefinition appConfigDef;
	List<LoaderException> loaderExceptions = new ArrayList();

	public LoaderState(List<String> cmdLineArgs, List<Map<ConfigPoint<?>, Object>> existingValues, AppConfigDefinition appConfigDef) {
		this.cmdLineArgs = cmdLineArgs;
		this.existingValues = existingValues;
		this.appConfigDef = appConfigDef;
	}
	
	public List<String> getCmdLineArgs() {
		return cmdLineArgs;
	}

	public List<Map<ConfigPoint<?>, Object>> getExistingValues() {
		return existingValues;
	}

	public AppConfigDefinition getAppConfigDef() {
		return appConfigDef;
	}

	public List<LoaderException> getLoaderExceptions() {
		return loaderExceptions;
	}
	
	/**
	 * Returns the loaded value for the specified point, or the default value
	 * if not loaded.
	 * 
	 * @param <T>
	 * @param point
	 * @return 
	 */
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		for (Map<ConfigPoint<?>, Object> map : existingValues) {
			if (map.containsKey(point)) {
				return point.cast(map.get(point));
			}
		}
		
		return point.getDefaultValue();
	}
	
	
}
