package yarnandtail.andhow.load;

import java.io.File;
import java.io.FileInputStream;
import yarnandtail.andhow.LoaderException;
import yarnandtail.andhow.Loader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import yarnandtail.andhow.AppConfig;
import yarnandtail.andhow.ConfigGroupDescription;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.FatalLoaderException;
import yarnandtail.andhow.RequiredPointException;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.point.IntConfigPoint;
import yarnandtail.andhow.point.IntPointBuilder;
import yarnandtail.andhow.point.StringConfigPoint;
import yarnandtail.andhow.point.StringPointBuilder;
//import yarnandtail.andhow.*;

/**
 *
 * @author eeverman
 */
public class PropFileLoader extends BaseLoader {

	
	@Override
	public Map<ConfigPoint<?>, Object> load(LoaderState state) {
		
		Map<ConfigPoint<?>, Object> values = new HashMap();
			
		String filePath = state.getEffectiveValue(CONFIG.PROP_FILEPATH);
		
		//No checking has been done (up to this point) for required properties,
		//so it is possible this point could be null
		if (filePath == null) {
			
			RequiredPointException reqEx = new RequiredPointException(
					CONFIG.PROP_FILEPATH, 
					new BasicNamingStrategy().buildNames(CONFIG.PROP_FILEPATH, 
							CONFIG.class, "PROP_FILEPATH").getCanonicalName());
					
			state.getLoaderExceptions().add(
					new FatalLoaderException(reqEx, this, CONFIG.PROP_FILEPATH,
							"Expected to find this ConfigPoint already specified - "
							+ "it must be loaded by a loader prior to the PropFileLoader "
							+ "so that the this loader can locate a properties file."));
		}
		
		
		
		return values;
	}
	
	@Override
	public Class<? extends ConfigPointGroup> getLoaderConfig() {
		return CONFIG.class;
	}
	

	protected Properties getConfigFromPropertiesFile(String fileName) throws FatalLoaderException {
		
		File propFile = null;
			
		String actualPathFileName = fileName;
		
		if (fileName.startsWith("~")) {
			actualPathFileName = fileName.replaceFirst("~", System.getProperty("user.home"));
		} else if (!fileName.contains(File.separator)) { //if no path in filename, use executable
			actualPathFileName = findExecutableDirectory() + File.separator + fileName;
		}
		
		propFile = new File(actualPathFileName);
		
		if (! propFile.exists()) {
			throw new FatalLoaderException(null, this, null,
						"The properties file '" + actualPathFileName + "' doesn't exist");
		}
		
		if (! propFile.canRead()) {
			throw new FatalLoaderException(null, this, null,
						"The properties file '" + actualPathFileName + "'exists, but permissions do not allow it to be read");
		}
		


		Properties props = new Properties();

		try (FileInputStream in = new FileInputStream(propFile)) {
			props.load(in);
		} catch (Exception e) {
			throw new FatalLoaderException(e, this, null,
						"The properties file '" + actualPathFileName + "'exists, was unparsable as a properties file");
		}

		return props;
			
	}
	

	private static String findExecutableDirectory() {
		try {
			String path = ConfigLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			File jarFile = new File(path);
			File jarDir = jarFile.getParentFile();

			if (jarDir.exists()) {
				return jarDir.getCanonicalPath();
			} else {
				LOG.debug("Unable to find a directory containing the running jar file (maybe this is not running from a jar??)");
				return null;
			}
		} catch (Exception e) {
			LOG.error("Attempting to find the executable directory containing the running jar file caused an exception", e);
			return null;
		}
	}
	
	
	//TODO:  WOULD LIKE TO HAVE A REQUIRE-ONE TYPE ConfigGroup
	@ConfigGroupDescription(
			groupName="PropFileLoader ConfigPoints",
			groupDescription= "One of these properties must be specified")
	public static interface CONFIG extends ConfigPointGroup {
		StringConfigPoint FILESYSTEM_PATH = StringPointBuilder.init()
				.setDescription("Local filesystem path to a properties file, as interpreted by a Java File object").build();
		StringConfigPoint EXECUTABLE_RELATIVE_PATH = StringPointBuilder.init()
				.setDescription("Path relative to the current executable for a properties file.  "
						+ "If running from a jar file, this would be a path relative to that jar. "
						+ "In other contexts, the parent directory may be unpredictable.").build();
		StringConfigPoint CLASSPATH_PATH = StringPointBuilder.init()
				.setDescription("Classpath to a properties file as interpreted by a Java Classloader.  "
						+ "This path should start with a slash like this: /org/name/MyProperties.props").build();
	}
	
	
}
