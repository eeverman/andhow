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
	private final List<Class<? extends ConfigPointGroup>> registeredGroups = new ArrayList();
	private final Map<ConfigPoint<?>, Object> startInputValues = new HashMap();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy naming = new BasicNamingStrategy();
	private final List<String> cmdLineArgs = new ArrayList();
	
	//Internal state
	private final List<ConfigPoint> registeredConfigPoints = new ArrayList();
	private final Map<String, ConfigPoint<?>> namedConfigPoints = new HashMap();
	
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
	
	public List<Class<? extends ConfigPointGroup>> getRegisteredGroups() {
		return Collections.unmodifiableList(registeredGroups);
	}

	public List<ConfigPoint> getRegisteredConfigPoints() {
		return Collections.unmodifiableList(registeredConfigPoints);
	}
	
	public Object getPointUserString(ConfigPoint point) {
		return startInputValues.get(point);
	}
	
	public boolean isPointPresent(ConfigPoint point) {
		return startInputValues.containsKey(point);
	}
	
	private static void doReset(AppConfig instanceToReset, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> startingValues) {
		synchronized (lock) {
			instanceToReset.loaders.clear();
			instanceToReset.startInputValues.clear();
			instanceToReset.registeredGroups.clear();
			instanceToReset.registeredConfigPoints.clear();
			instanceToReset.namedConfigPoints.clear();
			instanceToReset.cmdLineArgs.clear();

			if (loaders != null) {
				instanceToReset.loaders.addAll(loaders);
			}
			if (startingValues != null) {
				instanceToReset.startInputValues.putAll(startingValues);
			}
			if (registeredGroups != null) {
				instanceToReset.registeredGroups.addAll(registeredGroups);
			}
			if (cmdLineArgs != null && cmdLineArgs.length > 0) {
				instanceToReset.cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
			}
			
			doConfigPointRegistration(instanceToReset, registeredGroups);
			
		}
	}
	
	private void doLoad() {
		
		List<Map<ConfigPoint<?>, Object>> existingValues = new ArrayList();
		
		if (startInputValues.size() > 0) {
			existingValues.add(startInputValues);
		}
		
		LoaderState state = new LoaderState(cmdLineArgs, existingValues, namedConfigPoints);
		for (Loader loader : loaders) {
			Map<ConfigPoint<?>, Object> result = loader.load(state);
			if (result.size() > 0) existingValues.add(result);
		}
	}
	

	/**
	 * Loads the parameters to registeredConfigPoints and namedConfigPoints 
	 * @param instanceToReset
	 * @param registeredGroups 
	 */
	private static void doConfigPointRegistration(AppConfig instanceToReset, List<Class<? extends ConfigPointGroup>> registeredGroups) {
		synchronized (lock) {
			
			for (Class<? extends ConfigPointGroup> grp : registeredGroups) {
				Field[] fields = grp.getDeclaredFields();
				
				for (Field f : fields) {
					
					if (Modifier.isStatic(f.getModifiers()) && ConfigPoint.class.isAssignableFrom(f.getType())) {

						ConfigPoint cp = null;

						try {
							cp = (ConfigPoint) f.get(null);
						} catch (IllegalArgumentException ex) {
							Logger.getLogger(AppConfig.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IllegalAccessException ex) {
							Logger.getLogger(AppConfig.class.getName()).log(Level.SEVERE, null, ex);
							f.setAccessible(true);
							try {
								cp = (ConfigPoint) f.get(null);
							} catch (Exception ex1) {
								Logger.getLogger(AppConfig.class.getName()).log(Level.SEVERE, null, ex1);
							}
						}

						instanceToReset.registeredConfigPoints.add(cp);
						
						NamingStrategy.Naming names = instanceToReset.naming.buildNames(cp, grp, f.getName());
						
						instanceToReset.namedConfigPoints.put(names.getPrimaryName(), cp);
						
						for (String alias : names.getAliases()) {
							instanceToReset.namedConfigPoints.put(alias, cp);
						}
						
					}
					
				}
			}
		}
	}
	
	
	/**
	 * Mostly for testing - a backdoor to reset
	 * @param startingValues 
	 */
	public static void reset(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, HashMap<ConfigPoint<?>, Object> startingValues) {
		synchronized (lock) {
			
			if (singleInstance == null) {
				singleInstance = new AppConfig(loaders, registeredGroups, cmdLineArgs, startingValues);
			} else {
				doReset(singleInstance, loaders, registeredGroups, cmdLineArgs, startingValues);
			}
		}
	}

}
