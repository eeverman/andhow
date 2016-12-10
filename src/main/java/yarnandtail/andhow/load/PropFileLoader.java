package yarnandtail.andhow.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.LoaderException;
import java.util.Map.Entry;
import java.util.Properties;
import yarnandtail.andhow.*;
import static yarnandtail.andhow.ReportGenerator.ANDHOW_NAME;
import static yarnandtail.andhow.ReportGenerator.ANDHOW_TAG_LINE;
import static yarnandtail.andhow.ReportGenerator.ANDHOW_URL;
import yarnandtail.andhow.appconfig.AppConfigDefinition;
import yarnandtail.andhow.point.StringConfigPoint;

/**
 *
 * @author eeverman
 */
public class PropFileLoader extends BaseLoader implements ConfigSamplePrinter {

	String specificLoadDescription = null;
	
	@Override
	public LoaderValues load(AppConfigDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues, List<LoaderException> loaderExceptions) throws FatalException {
		
		ArrayList<PointValue> values = new ArrayList();
		Properties props = null;
		
		String filePath = existingValues.getEffectiveValue(CONFIG.FILESYSTEM_PATH);

		if (filePath != null) {
			specificLoadDescription = "File at: " + filePath;
			props = loadPropertiesFromFilesystem(new File(filePath), CONFIG.FILESYSTEM_PATH);			
		}
		
		if (props == null && existingValues.getEffectiveValue(CONFIG.EXECUTABLE_RELATIVE_PATH) != null) {
			File relPath = buildExecutableRelativePath(existingValues.getEffectiveValue(CONFIG.EXECUTABLE_RELATIVE_PATH));
			
			specificLoadDescription = "File at: " + filePath;
			
			if (relPath != null) {
				props = loadPropertiesFromFilesystem(relPath, CONFIG.EXECUTABLE_RELATIVE_PATH);
			}
		}
		
		if (props == null && existingValues.getEffectiveValue(CONFIG.CLASSPATH_PATH) != null) {
			
			specificLoadDescription = "File on classpath at: " + existingValues.getEffectiveValue(CONFIG.CLASSPATH_PATH);
			
			props = loadPropertiesFromClasspath(
				existingValues.getEffectiveValue(CONFIG.CLASSPATH_PATH), CONFIG.CLASSPATH_PATH);

		}

		if (props == null) {
			throw new FatalException(null,
				"Expected to find one of the PropFileLoader config points " +
				"pointing to a valid file, but couldn't read any file. ");
		}
		
		for(Entry<Object, Object> entry : props.entrySet()) {
			if (entry.getKey() != null && entry.getValue() != null) {
				String k = entry.getKey().toString();
				String v = entry.getValue().toString();
				
				try {

					attemptToAdd(appConfigDef, values, k, v);

				} catch (ParsingException e) {
					loaderExceptions.add(new LoaderException(e, this, null, specificLoadDescription)
					);
				}
				
				
			}
		}
		
		values.trimToSize();
		
		return new LoaderValues(this, values);
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
		
		InputStream inS = PropFileLoader.class.getResourceAsStream(classpath);
		
		return loadPropertiesFromInputStream(inS, fromPoint, classpath);

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
	
	@Override
	public String getSpecificLoadDescription() {
		return specificLoadDescription;
	}
	
	@Override
	public void printSampleStart(PrintStream out) {
		out.println(TextUtil.repeat("#", ReportGenerator.DEFAULT_LINE_WIDTH));
		out.println(TextUtil.padRight("# Sample properties file generated by " + 
				ANDHOW_NAME + "  " + ANDHOW_TAG_LINE + "  ", "#", ReportGenerator.DEFAULT_LINE_WIDTH));
		out.println(TextUtil.padRight(TextUtil.repeat("#", 50) + "  " + ANDHOW_URL + " ", "#", ReportGenerator.DEFAULT_LINE_WIDTH));
		out.println("# " + ConfigSamplePrinter.REQUIRED_HEADER_TEXT);
	}
	
	@Override
	public void printConfigGroupStart(PrintStream out, Class<? extends ConfigPointGroup> group) {
		out.println();
		out.println();
		out.println(TextUtil.repeat("#", ReportGenerator.DEFAULT_LINE_WIDTH));
		
		String name = null;
		String desc = null;
		
		ConfigGroupDescription groupDesc = group.getAnnotation(ConfigGroupDescription.class);
		if (groupDesc != null) {
			name = TextUtil.trimToNull(groupDesc.groupName());
			desc = TextUtil.trimToNull(groupDesc.groupDescription());
		}
		
		if (name != null || desc != null) {
			if (name != null && desc != null) {
				TextUtil.println(out, "# Configuration group {} - {}", name, desc);
				TextUtil.println(out, "# Defined in interface {}", group.getCanonicalName());
			} else {
				TextUtil.println(out, "# Configuration group {}", group.getCanonicalName());
				TextUtil.println(out, "# Description: {}", (name != null)?name:desc);
			}
			
			
		} else {
			TextUtil.println(out, "# Configuration group {}", group.getCanonicalName());
		}
		
	}
	
	
	@Override
	public void printConfigPoint(PrintStream out, Class<? extends ConfigPointGroup> group,
			ConfigPoint<?> point) throws IllegalArgumentException, IllegalAccessException, SecurityException {
		
		
		String pointFieldName = ConfigPointGroup.getFieldName(group, point);
		String pointCanondName = ConfigPointGroup.getCanonicalName(group, point);
		
		out.println();
		TextUtil.println(out, "# {} ({}) {}{}", 
				pointFieldName,
				point.getValueType().getDestinationType().getSimpleName(),
				(point.isRequired())?ConfigSamplePrinter.REQUIRED_TEXT:"",
				(TextUtil.trimToNull(point.getShortDescription()) == null)?"":" - " + point.getShortDescription());
		
		if (point.getDefaultValue() != null) {
			out.println("# Default Value: " + point.getDefaultValue());
		}
		
		if (TextUtil.trimToNull(point.getHelpText()) != null) {
			out.println("# " + point.getHelpText());
		}
		
		if (point.getValidators().size() == 1) {
			out.println("# " + VALIDATION_TEXT + " " + point.getValidators().get(0).getTheValueMustDescription());
		}
		
		if (point.getValidators().size() > 1) {
			out.println("# " + VALIDATION_TEXT + ":");	
			for (Validator v : point.getValidators()) {
				out.println("# \t- " + v.getTheValueMustDescription());
			}
		}
		
		//print the actual sample line
		if (point.getDefaultValue() != null) {
			TextUtil.println(out, "{} = {}", 
					pointCanondName, 
					point.getDefaultValue());
		} else {
			TextUtil.println(out, "{} = [{}]", 
					pointCanondName, 
					point.getValueType().getDestinationType().getSimpleName());
		}
		
		
	}

	@Override
	public void printConfigGroupEnd(PrintStream out, Class<? extends ConfigPointGroup> group) {
	}
	
	@Override
	public void printSampleEnd(PrintStream out) {
		out.println();
		out.println(TextUtil.repeat("#", ReportGenerator.DEFAULT_LINE_WIDTH));
		out.println(TextUtil.repeat("#", ReportGenerator.DEFAULT_LINE_WIDTH));
	}

	
	
	//TODO:  WOULD LIKE TO HAVE A REQUIRE-ONE TYPE ConfigGroup
	@ConfigGroupDescription(
			groupName="PropFileLoader Configuration",
			groupDescription= "Configure one of these points to specify a location to load a properties file from. " +
					"Search order is the order listed below.")
	public static interface CONFIG extends ConfigPointGroup {
		StringConfigPoint FILESYSTEM_PATH = StringConfigPoint.builder()
				.setDescription("Local filesystem path to a properties file, as interpreted by a Java File object").build();
		
		StringConfigPoint EXECUTABLE_RELATIVE_PATH = StringConfigPoint.builder()
				.setDescription("Path relative to the current executable for a properties file.  "
						+ "If running from a jar file, this would be a path relative to that jar. "
						+ "In other contexts, the parent directory may be unpredictable.").build();
		
		StringConfigPoint CLASSPATH_PATH = StringConfigPoint.builder()
				.setDefault("/andhow.properties")
				.setDescription("Classpath to a properties file as interpreted by a Java Classloader.  "
						+ "This path should start with a slash like this: /org/name/MyProperties.props").build();
	}
	
}
