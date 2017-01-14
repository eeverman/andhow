package yarnandtail.andhow.load;

import yarnandtail.andhow.sample.PropFileLoaderSamplePrinter;
import yarnandtail.andhow.util.TextUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import yarnandtail.andhow.*;
import yarnandtail.andhow.property.StrProp;

/**
 * Reads properties from a Java .property file, following standard java conventions
 * for the structure of those file.
 * 
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property.
 * This loader considers it a problem to find unrecognized properties in a 
 * properties file and will throw a RuntimeException if that happens.
 * 
 * The PropFileLoader uses the java.util.Properties class to do read properties, 
 * so several behaviours are determined by that class.
 * 
 * In rare cases, whitespace handling of the JVM Properties file parser may be an issue. 
 * The property value is generally terminated by the end of the line. Whitespace 
 * following the property value is not ignored, and is treated as part of the property value.
 * This is not a problem in most cases because by default, properties have Trimmers 
 * that remove whitespace. Other Trimmer implementations can be assigned to properties, 
 * however, so be aware of the implementations if your are using non-default Trimmers.
 * 
 * The PropFileLoader is unable to detect duplicate properties (i.e., the same key 
 * value appearing more than once in a prop file). Instead of aborting the application 
 * startup with an error, only the last of the property values in the file is assigned. 
 * This is a basic limitation of the JVM Properties class, which silently ignores 
 * multiple entries, each value overwriting the last.
 * 
 * @author eeverman
 */
public class PropFileLoader extends BaseLoader {

	String specificLoadDescription = null;
	
	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
		ArrayList<PropertyValue> values = new ArrayList();
		ArrayList<LoaderProblem> problems = new ArrayList(0);
		Properties props = null;
		
		String filePath = existingValues.getEffectiveValue(CONFIG.FILESYSTEM_PATH);

		
		try {
			if (filePath != null) {
				specificLoadDescription = "file at: " + filePath;
				props = loadPropertiesFromFilesystem(new File(filePath), CONFIG.FILESYSTEM_PATH);			
			}

			if (props == null && existingValues.getEffectiveValue(CONFIG.EXECUTABLE_RELATIVE_PATH) != null) {
				File relPath = buildExecutableRelativePath(existingValues.getEffectiveValue(CONFIG.EXECUTABLE_RELATIVE_PATH));

				specificLoadDescription = "file at: " + filePath;

				if (relPath != null) {
					props = loadPropertiesFromFilesystem(relPath, CONFIG.EXECUTABLE_RELATIVE_PATH);
				}
			}

			if (props == null && existingValues.getEffectiveValue(CONFIG.CLASSPATH_PATH) != null) {

				specificLoadDescription = "file on classpath at: " + existingValues.getEffectiveValue(CONFIG.CLASSPATH_PATH);

				props = loadPropertiesFromClasspath(
					existingValues.getEffectiveValue(CONFIG.CLASSPATH_PATH), CONFIG.CLASSPATH_PATH);

			}

			if (props != null) {

				Set<Object> keys = props.keySet();
				for(Object key : keys) {
					if (key != null) {
						String k = key.toString();
						String v = props.getProperty(k);

						attemptToAdd(appConfigDef, values, problems, k, v);
					}
				}

				values.trimToSize();
			} else {

				LoaderProblem p = new LoaderProblem.SourceNotFoundLoaderProblem(this, 
						TextUtil.format("Could not find a properties file to read. " + 
							"Make sure there is a property file at the default location {}, " +
							"or that one of the PropFileLoader.CONFIG properties points to valid location.", 
						CONFIG.CLASSPATH_PATH.getDefaultValue())
				);

				problems.add(p);
			}
		} catch (LoaderException e) {
			problems.add(new LoaderProblem.IOLoaderProblem(this, null, null, e));
		}
		

		
		return new LoaderValues(this, values, problems);
	}
	
	@Override
	public Class<? extends PropertyGroup> getLoaderConfig() {
		return CONFIG.class;
	}
	

	/**
	 * @param propFile the File to load from
	 * @param fromProp For reference, which Property identified this file to load from
	 * @return
	 * @throws LoaderException 
	 */
	protected Properties loadPropertiesFromFilesystem(File propFile, Property<?> fromProp) 
			throws LoaderException {
		
		if (propFile.exists() && propFile.canRead()) {

			try (FileInputStream in = new FileInputStream(propFile)) {
				return loadPropertiesFromInputStream(in, fromProp, propFile.getAbsolutePath());
			} catch (IOException e) {
				//this exception from opening the FileInputStream
				//Ignore - non-fatal b/c we can try another
			}
		}

		return null;	
	}
	
	/**
	 * 
	 * @param classpath
	 * @param fromProp For reference, which Property identified this file to load from
	 * @return
	 * @throws yarnandtail.andhow.load.LoaderException
	 */
	protected Properties loadPropertiesFromClasspath(String classpath, Property<?> fromProp)
			throws LoaderException {
		
		InputStream inS = PropFileLoader.class.getResourceAsStream(classpath);
		return loadPropertiesFromInputStream(inS, fromProp, classpath);
	}
	
	/**
	 * 
	 * @param inputStream
	 * @param fromProp For reference, which Property identified this file to load from
	 * @param fromPath
	 * @return
	 * @throws yarnandtail.andhow.load.LoaderException
	 */
	protected Properties loadPropertiesFromInputStream(
			InputStream inputStream, Property<?> fromProp, String fromPath) throws LoaderException {

		if (inputStream == null) return null;
		
		try {
			Properties props = new Properties();
			props.load(inputStream);
			return props;
		} catch (Exception e) {
			throw new LoaderException(e, this, "properties file at '" + fromPath + "'");
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
	
	@Override
	public String getSpecificLoadDescription() {
		return specificLoadDescription;
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}
	
	@Override
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return true;
	}
	
	@Override
	public SamplePrinter getConfigSamplePrinter() {
		return new PropFileLoaderSamplePrinter();
	}
	
	@GroupInfo(
			name="PropFileLoader Configuration",
			desc="Configure one of these properties to specify a location to load a properties file from. " +
					"Search order is the order listed below.")
	public static interface CONFIG extends PropertyGroup {
		StrProp FILESYSTEM_PATH = StrProp.builder()
				.desc("Local filesystem path to a properties file, as interpreted by a Java File object").build();
		
		StrProp EXECUTABLE_RELATIVE_PATH = StrProp.builder()
				.desc("Path relative to the current executable for a properties file.  "
						+ "If running from a jar file, this would be a path relative to that jar. "
						+ "In other contexts, the parent directory may be unpredictable.").build();
		
		StrProp CLASSPATH_PATH = StrProp.builder()
				.defaultValue("/andhow.properties")
				.mustStartWith("/")
				.desc("Classpath to a properties file as interpreted by a Java Classloader.  "
						+ "e.g.: /org/name/MyProperties.props").build();
	}
	
}
