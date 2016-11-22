package yarnandtail.andhow;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author eeverman
 */
public class AppConfig {
	
	private static AppConfig singleInstance;
	private static Object lock = new Object();
	AppConfigCore core;
	
	private AppConfig(NamingStrategy naming, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> startingValues) {
		core = new AppConfigCore(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
	}
	
	public static AppConfig instance() {
		if (singleInstance != null) {
			return singleInstance;
		} else {
			return null;	//this should block
		}
	}
	
	public static AppConfig instance(
			NamingStrategy naming, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> startingValues) throws ConfigurationException {

		if (singleInstance != null) {
			throw new RuntimeException("Already constructed!");
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					throw new RuntimeException("Already constructed!");
				} else {
					singleInstance = new AppConfig(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
					return singleInstance;
				}
			}
		}
	}
	
	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return core.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return core.getPoints();
	}
	
	public boolean isPointPresent(ConfigPoint<?> point) {
		return core.isPointPresent(point);
	}
	
	public Object getValue(ConfigPoint<?> point) {
		return core.getValue(point);
	}

	/**
	 * Mostly for testing - a backdoor to reset
	 * @param startingValues 
	 */
	public static void reset(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, 
			HashMap<ConfigPoint<?>, Object> forcedValues) {
		synchronized (lock) {
			
			if (singleInstance == null) {
				singleInstance = new AppConfig(naming, loaders, registeredGroups, cmdLineArgs, forcedValues);
			} else {
				singleInstance.core = new AppConfigCore(naming, loaders, registeredGroups, cmdLineArgs, forcedValues);
			}
		}
	}

}
