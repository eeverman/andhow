package yarnandtail.andhow.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import yarnandtail.andhow.LoaderException;
import yarnandtail.andhow.Loader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import yarnandtail.andhow.AppConfig;
import yarnandtail.andhow.ConfigGroupDescription;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.FatalException;
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
	public Map<ConfigPoint<?>, Object> load(LoaderState state) throws FatalException {
		
		Map<ConfigPoint<?>, Object> values = new HashMap();
		Properties props = null;
		
		String filePath = state.getEffectiveValue(CONFIG.FILESYSTEM_PATH);
		

		if (filePath != null) {
			props = loadPropertiesFromFilesystem(new File(filePath), CONFIG.FILESYSTEM_PATH);			
		}
		
		if (props == null && state.getEffectiveValue(CONFIG.EXECUTABLE_RELATIVE_PATH) != null) {
			File relPath = buildExecutableRelativePath(state.getEffectiveValue(CONFIG.EXECUTABLE_RELATIVE_PATH));
			if (relPath != null) {
				props = loadPropertiesFromFilesystem(relPath, CONFIG.EXECUTABLE_RELATIVE_PATH);
			}
		}
		
		if (props == null && state.getEffectiveValue(CONFIG.CLASSPATH_PATH) != null) {
			
			props = loadPropertiesFromClasspath(
				state.getEffectiveValue(CONFIG.CLASSPATH_PATH), CONFIG.CLASSPATH_PATH);

		}

		if (props == null) {
			throw new FatalLoaderException(null, this, null,
				"Expected to find one of the PropFileLoader config points " +
				"pointing to a valid file, but couldn't read any file. ");
		}
		
		
		//return values;
	}
	
	@Override
	public Class<? extends ConfigPointGroup> getLoaderConfig() {
		return CONFIG.class;
	}
	

	protected Properties loadPropertiesFromFilesystem(File propFile, ConfigPoint<?> fromPoint) throws FatalException {
		
		if (propFile.exists() && propFile.canRead()) {

			try (FileInputStream in = new FileInputStream(propFile)) {
				return loadPropertiesFromInputStream(in, fromPoint, propFile.getAbsolutePath());
			} catch (IOException e) {
				//this exception from opening the FileInputStream
				//Ignore - non-fatal b/c we can try another
			}
		}

		return null;	
	}
	
	protected Properties loadPropertiesFromClasspath(String classpath, ConfigPoint<?> fromPoint) throws FatalException {
		
		return loadPropertiesFromInputStream(
				PropFileLoader.class.getClassLoader().getResourceAsStream(classpath), fromPoint, classpath);

	}
	
	protected Properties loadPropertiesFromInputStream(InputStream inputStream, ConfigPoint<?> fromPoint, String fromPath) throws FatalException {

		if (inputStream == null) return null;
		
		try {
			Properties props = new Properties();
			props.load(inputStream);
			return props;
		} catch (Exception e) {

			LoaderException le = new LoaderException(e, this, fromPoint,
					"The properties file at '" + fromPath + 
					"' exists and is accessable, but was unparsable.");

			throw new FatalException(le,
					"Unable to continue w/ configuration loading.  " +
					"Fix the properties file and try again.");
		}
	
	}
	

	protected File buildExecutableRelativePath(String filePath) {
		try {
			String path = PropFileLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			File jarFile = new File(path);
			File jarDir = jarFile.getParentFile();

			if (jarDir.exists()) {
				return new File(jarDir, filePath);
			} else {
				//LOG.debug("Unable to find a directory containing the running jar file (maybe this is not running from a jar??)");
				return null;
			}
		} catch (Exception e) {
			//LOG.error("Attempting to find the executable directory containing the running jar file caused an exception", e);
			return null;
		}
	}
	
	protected LoaderException buildLoaderException() {
							new LoaderException(reqEx, this, CONFIG.PROP_FILEPATH,
							"Expected to find this ConfigPoint already specified - "
							+ "it must be loaded by a loader prior to the PropFileLoader "
							+ "so that the this loader can locate a properties file."));
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
