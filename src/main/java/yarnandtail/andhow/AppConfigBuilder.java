package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 * A utility builder class to make AppConfig construction easier.
 * @author eeverman
 */
public class AppConfigBuilder {
	//User config
	private final Map<ConfigPoint<?>, Object> forcedValues = new HashMap();
	private final List<Loader> loaders = new ArrayList();
	private NamingStrategy namingStrategy = new BasicNamingStrategy();
	private final List<String> cmdLineArgs = new ArrayList();
	List<Class<? extends ConfigPointGroup>> groups = new ArrayList();
	
	public static AppConfigBuilder init() {
		return new AppConfigBuilder();
	}
	
	public AppConfigBuilder add(Loader loader) {
		loaders.add(loader);
		return this;
	}
	
	public AppConfigBuilder add(Class<? extends ConfigPointGroup> group) {
		groups.add(group);
		return this;
	}
	
	public AppConfigBuilder add(ConfigPoint<?> point, Object value) {
		forcedValues.put(point, value);
		return this;
	}
	
	/**
	 * Sets the command line arguments and clears out any existing cmd line values.
	 * 
	 * @param commandLineArgs
	 * @return 
	 */
	public AppConfigBuilder set(String[] commandLineArgs) {
		cmdLineArgs.clear();
		cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
		return this;
	}
	
	/**
	 * Adds a command line argument.
	 * 
	 * Note that values added this way are overwritten if set(String[]) is called.
	 * @param key
	 * @param value
	 * @return 
	 */
	public AppConfigBuilder addCmdLine(String key, String value) {
		cmdLineArgs.add(key + AppConfig.KVP_DELIMITER + value);
		return this;
	}
	
	public AppConfigBuilder set(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		return this;
	}
	
	/**
	 * Builds a new AppConfig and returns the Reloader instance that can be used
	 * for reloading the instance.
	 * 
	 * Reloading the AppConfig is mostly intended for testing - full support for
	 * reloading (including the issue of inconsistent reads of the data) is not
	 * in place.
	 * 
	 * @return
	 * @throws ConfigurationException 
	 */
	public AppConfig.Reloader build() throws ConfigurationException {
		String[] args = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
		return AppConfig.build(namingStrategy, loaders, groups,  args, forcedValues);
	}
	
	/**
	 * Reload the AppConfig.
	 * 
	 * Not recommended in production.
	 * 
	 * @param reloader
	 * @throws ConfigurationException 
	 */
	public void build(AppConfig.Reloader reloader) throws ConfigurationException {
		String[] args = cmdLineArgs.toArray(new String[cmdLineArgs.size()]);
		reloader.reload(namingStrategy, loaders, groups,  args, forcedValues);
	}
	
	
	
	
}
