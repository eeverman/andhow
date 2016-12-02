package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 * A builder class to make AppConfig construction incremental and more readable.
 * 
 * Generally <em>addXXX</em> adds to a collection and  <em>setXXX</em> replaces 
 * the value or values.
 * 
 * Usage looks like below, always starting with init() and ending with build():
 * <pre>
 * {@code 
 * 		AppConfigBuilder.init()
 *			.setNamingStrategy(basicNaming)
 *			.addLoaders(loaders)
 *			.addGroups(configPtGroups)
 *			.setCmdLineArgs(cmdLineArgsWExplicitName)
 *			.build();
 * }
 * </pre>
 * 
 * build() returns an AppConfig.Reloader object.  If you do need to reload the
 * AppConfig at some point (primarily for testing, not production), you will need
 * to keep a reference to that Reloader.  An overloaded version of build(Reloader)
 * expects that same instance of the reloader to allow the AppConfig to reload.
 * 
 * Attempting to call build() a 2nd time w/o the reloader instance will
 * cause a RuntimeException.
 * 
 * @author eeverman
 */
public class AppConfigBuilder {
	//User config
	private final ArrayList<PointValue> forcedValues = new ArrayList();
	private final List<Loader> loaders = new ArrayList();
	private NamingStrategy namingStrategy = new BasicNamingStrategy();
	private final List<String> cmdLineArgs = new ArrayList();
	List<Class<? extends ConfigPointGroup>> groups = new ArrayList();
	
	public static AppConfigBuilder init() {
		return new AppConfigBuilder();
	}
	
	public AppConfigBuilder addLoader(Loader loader) {
		loaders.add(loader);
		return this;
	}
	
	public AppConfigBuilder addLoaders(Collection<Loader> loaders) {
		this.loaders.addAll(loaders);
		return this;
	}
	
	public AppConfigBuilder addGroup(Class<? extends ConfigPointGroup> group) {
		groups.add(group);
		return this;
	}
	
	public AppConfigBuilder addGroups(Collection<Class<? extends ConfigPointGroup>> groups) {
		this.groups.addAll(groups);
		return this;
	}
	
	public AppConfigBuilder addForcedValue(ConfigPoint<?> point, Object value) {
		forcedValues.add(new PointValue(point, value));
		return this;
	}
	
	/**
	 * Alternative to adding individual forced values if you already have them
	 * in a list
	 * @param startVals
	 * @return 
	 */
	public AppConfigBuilder addForcedValues(List<PointValue> startVals) {
		this.forcedValues.addAll(startVals);
		return this;
	}
	
	/**
	 * Sets the command line arguments and clears out any existing cmd line values.
	 * 
	 * @param commandLineArgs
	 * @return 
	 */
	public AppConfigBuilder setCmdLineArgs(String[] commandLineArgs) {
		cmdLineArgs.clear();
		cmdLineArgs.addAll(Arrays.asList(commandLineArgs));
		return this;
	}
	
	/**
	 * Adds a command line argument.
	 * 
	 * Note that values added this way are overwritten if setCmdLineArgs(String[]) is called.
	 * @param key
	 * @param value
	 * @return 
	 */
	public AppConfigBuilder addCmdLineArg(String key, String value) {
		cmdLineArgs.add(key + AppConfig.KVP_DELIMITER + value);
		return this;
	}
	
	public AppConfigBuilder setNamingStrategy(NamingStrategy namingStrategy) {
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
