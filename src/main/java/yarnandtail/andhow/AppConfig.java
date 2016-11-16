package yarnandtail.andhow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	private final HashMap<ConfigPoint, String> startInputValues = new HashMap();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy naming = new BasicNamingStrategy();
	
	//Internal state
	private final List<ConfigPoint> registeredConfigPoints = new ArrayList();
	private final HashMap<String, ConfigPoint> namedConfigPoints = new HashMap();
	
	private AppConfig(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, HashMap<ConfigPoint, String> startingValues) {
		doReset(this, loaders, registeredGroups, startingValues);

	}
	
	public static AppConfig instance() {
		if (singleInstance != null) {
			return singleInstance;
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					return singleInstance;
				} else {
					singleInstance = new AppConfig(null, null, null);
					return singleInstance;
				}
			}
		}
	}
	
	public static AppConfig instance(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, HashMap<ConfigPoint, String> startingValues) {
		if (singleInstance != null) {
			throw new RuntimeException("Already constructed!");
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					throw new RuntimeException("Already constructed!");
				} else {
					singleInstance = new AppConfig(loaders, registeredGroups, startingValues);
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
	
	public String getPointUserString(ConfigPoint point) {
		return startInputValues.get(point);
	}
	
	public boolean isPointPresent(ConfigPoint point) {
		return startInputValues.containsKey(point);
	}
	
	private static void doReset(AppConfig instanceToReset, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, HashMap<ConfigPoint, String> startingValues) {
		synchronized (lock) {
			instanceToReset.loaders.clear();
			instanceToReset.startInputValues.clear();
			instanceToReset.registeredGroups.clear();
			instanceToReset.registeredConfigPoints.clear();
			instanceToReset.namedConfigPoints.clear();

			if (loaders != null) {
				instanceToReset.loaders.addAll(loaders);
			}
			if (startingValues != null) {
				instanceToReset.startInputValues.putAll(startingValues);
			}
			if (registeredGroups != null) {
				instanceToReset.registeredGroups.addAll(registeredGroups);
			}
			
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
	public static void reset(List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, HashMap<ConfigPoint, String> startingValues) {
		synchronized (lock) {
			
			if (singleInstance == null) {
				singleInstance = new AppConfig(loaders, registeredGroups, startingValues);
			} else {
				doReset(singleInstance, loaders, registeredGroups, startingValues);
			}
		}
	}

}
