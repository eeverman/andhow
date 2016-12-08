package yarnandtail.andhow;

import yarnandtail.andhow.appconfig.AppConfigCore;
import java.util.List;
import yarnandtail.andhow.PointValue;

/**
 *
 * @author eeverman
 */
public class AppConfig implements AppConfigValues {
	
	/**
	 * In text formats, this is the default delimiter between a key and a value.
	 * Known usage:  The CmdLineLoader uses this value to parse values.
	 */
	public static final String KVP_DELIMITER = "=";
	
	
	private static AppConfig singleInstance;
	private static final Object lock = new Object();
	
	AppConfigCore core;
	Reloader reloader;
	
	private AppConfig(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues)
			throws AppFatalException {
		core = new AppConfigCore(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
		reloader = new Reloader(this);
	}
	
	public static AppConfig instance() {
		if (singleInstance != null && singleInstance.core != null) {
			return singleInstance;
		} else {
			throw new RuntimeException("AppConfig has not been initialized.  " +
					"Possible causes:  1) There is a race condition where ConfigPoint access may happen before configuration " +
					"2) There is no configuration at the entry point to the application. " +
					"Refer to " + ReportGenerator.ANDHOW_URL + " for code examples and FAQs.");
		}
	}
	
	public static Reloader build(
			NamingStrategy naming, List<Loader> loaders, List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues) throws AppFatalException {

		synchronized (lock) {
			if (singleInstance != null) {
				throw new RuntimeException("Already constructed!");
			} else {
				singleInstance = new AppConfig(naming, loaders, registeredGroups, cmdLineArgs, startingValues);
				return singleInstance.reloader;
			}
		}

	}
	
	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return core.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return core.getPoints();
	}
	
	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return core.isPointPresent(point);
	}
	
	@Override
	public <T> T getValue(ConfigPoint<T> point) {
		return core.getValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return core.getEffectiveValue(point);
	}


	public static class Reloader {
		private final AppConfig instance;
		
		private Reloader(AppConfig instance) {
			this.instance = instance;
		}
		/**
		 * Forces a reload of the AppConfig state.
		 * 
		 * This may someday support production reloading of values, but for now
		 * it is really just a means for testing w/o having to deal with new
		 * classloaders for the singleton instancing.
		 * 
		 * @param naming
		 * @param loaders
		 * @param registeredGroups
		 * @param cmdLineArgs
		 * @param forcedValues
		 * @throws AppFatalException 
		 */
		public void reload(NamingStrategy naming, List<Loader> loaders, 
				List<Class<? extends ConfigPointGroup>> registeredGroups, String[] cmdLineArgs, 
				List<PointValue> forcedValues) 
				throws AppFatalException {
			
			synchronized (AppConfig.lock) {
				instance.core = new AppConfigCore(naming, loaders, registeredGroups, cmdLineArgs, forcedValues);
			}
		}
		
		/**
		 * For shutdown or testing.
		 * 
		 * Flushes the internal state, making the AppConfig appear unconfigured.
		 */
		public void destroy() {
			
			synchronized (AppConfig.lock) {
				if (instance != null) {
					instance.core = null;
				}
			}
		}
	}

}
