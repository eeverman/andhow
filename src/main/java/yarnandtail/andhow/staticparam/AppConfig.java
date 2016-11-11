package yarnandtail.andhow.staticparam;

import java.util.HashMap;

/**
 *
 * @author eeverman
 */
public class AppConfig {
	
	private static AppConfig singleInstance;
	private static Object lock = new Object();
	
	private static final HashMap<ConfigPoint, String> effectiveUserPoints = new HashMap();
	
	private AppConfig(HashMap<ConfigPoint, String> startingValues) {
		
		synchronized (lock) {
			if (startingValues != null) {
				effectiveUserPoints.putAll(startingValues);
			}
		}
	}
	
	public static AppConfig instance() {
		if (singleInstance != null) {
			return singleInstance;
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					return singleInstance;
				} else {
					singleInstance = new AppConfig(null);
					return singleInstance;
				}
			}
		}
	}
	
	public static AppConfig instance(HashMap<ConfigPoint, String> startingValues) {
		if (singleInstance != null) {
			throw new RuntimeException("Already constructed!");
		} else {
			synchronized (lock) {
				if (singleInstance != null) {
					throw new RuntimeException("Already constructed!");
				} else {
					singleInstance = new AppConfig(startingValues);
					return singleInstance;
				}
			}
		}
	}
	
	public String getUserString(ConfigPoint point) {
		return effectiveUserPoints.get(point);
	}
	
	/**
	 * Mostly for testing - a backdoor to reset
	 * @param startingValues 
	 */
	protected static void reset(HashMap<ConfigPoint, String> startingValues) {
		synchronized (lock) {
			
			if (singleInstance == null) {
				singleInstance = new AppConfig(startingValues);
			} else {
				effectiveUserPoints.clear();
				if (startingValues != null) {
					effectiveUserPoints.putAll(startingValues);
				}
			}
		}
	}

}
