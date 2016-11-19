package yarnandtail.andhow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import yarnandtail.andhow.load.LoaderState;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AppConfig {
	
	private static AppConfig singleInstance;
	private static Object lock = new Object();
	
	//User config
	private final Map<ConfigPoint<?>, Object> forcedValues = new HashMap();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy naming = new BasicNamingStrategy();
	private final List<String> cmdLineArgs = new ArrayList();
	
	//Internal state
	
	//Note: This should be an AtomicReference to ensure we don't transiently 
	//assign to null when updating.
	private AppConfigDefinition appConfigDef;
	private final List<Map<ConfigPoint<?>, Object>> loadedValues = new ArrayList();
	
	private AppConfig(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> startingValues) {
		doReset(this, loaders, registeredGroups, cmdLineArgs, startingValues);

	}
	
	public static AppConfig instance() {
		if (singleInstance != null) {
			return singleInstance;
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					return singleInstance;
				} else {
					singleInstance = new AppConfig(null, null, null, null);
					return singleInstance;
				}
			}
		}
	}
	
	public static AppConfig instance(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> startingValues) {
		if (singleInstance != null) {
			throw new RuntimeException("Already constructed!");
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					throw new RuntimeException("Already constructed!");
				} else {
					singleInstance = new AppConfig(loaders, registeredGroups, cmdLineArgs, startingValues);
					return singleInstance;
				}
			}
		}
	}
	
	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return appConfigDef.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return appConfigDef.getPoints();
	}
	
	public boolean isPointPresent(ConfigPoint<?> point) {
		return getValue(point) != null;
	}
	
	public Object getValue(ConfigPoint<?> point) {
		for (Map<ConfigPoint<?>, Object> map : loadedValues) {
			if (map.containsKey(point)) {
				return map.get(point);
			}
		}
		
		return null;
	}
	
	private static void doReset(AppConfig instanceToReset, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> forcedValues) {
		synchronized (lock) {
			instanceToReset.loaders.clear();
			instanceToReset.forcedValues.clear();
			instanceToReset.appConfigDef = null;	//TODO:  how should this work
			instanceToReset.cmdLineArgs.clear();
			instanceToReset.loadedValues.clear();

			if (loaders != null) {
				instanceToReset.loaders.addAll(loaders);
			}
			if (forcedValues != null) {
				instanceToReset.forcedValues.putAll(forcedValues);
			}

			if (cmdLineArgs != null && cmdLineArgs.length > 0) {
				instanceToReset.cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
			}
			
			instanceToReset.appConfigDef = AppConfigUtil.doRegisterConfigPoints(registeredGroups, instanceToReset.naming);
			instanceToReset.doLoad();
			
		}
	}
	
	private void doLoad() {
		
		synchronized (lock) {
			List<Map<ConfigPoint<?>, Object>> existingValues = new ArrayList();

			if (forcedValues.size() > 0) {
				existingValues.add(forcedValues);
			}

			LoaderState state = new LoaderState(cmdLineArgs, existingValues, appConfigDef);
			for (Loader loader : loaders) {
				Map<ConfigPoint<?>, Object> result = loader.load(state);
				if (result.size() > 0) existingValues.add(result);
			}

			loadedValues.clear();
			loadedValues.addAll(existingValues);
		}
	}
	
	/**
	 * Mostly for testing - a backdoor to reset
	 * @param startingValues 
	 */
	public static void reset(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> forcedValues) {
		synchronized (lock) {
			
			if (singleInstance == null) {
				singleInstance = new AppConfig(loaders, registeredGroups, cmdLineArgs, forcedValues);
			} else {
				doReset(singleInstance, loaders, registeredGroups, cmdLineArgs, forcedValues);
			}
		}
	}

}
