package yarnandtail.andhow.load;

import yarnandtail.andhow.ParsingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import yarnandtail.andhow.*;
import static yarnandtail.andhow.ReportGenerator.DEFAULT_LINE_WIDTH;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.property.StrProp;

/**
 * Reads properties from a Java .property file, following standard java conventions
 * for the structure of those file.
 * 
 * The PropFileLoader actually uses the java.util.Properties class to do read
 * the properties, so several behaviours are determined by that JVM class.
 * Particular quirks of how Java Properties reads properties files:
 * <ul>
 * <li>Multiple entries for the same property (i.e., the same key value appearing
 * on multiple lines) will result in only the last of the values being set.  The
 * previous entries for the same key are just overwritten by the later ones.
 * Normally AndHow would consider multiple entries for a property a Problem, but
 * in this case, the Java properties file reader doesn't report the issue, only
 * the final value.
 * <li>In rare cases, whitespace handling may be an issue.
 * After finding the start of a value on a row, which happens after
 * finding the key and ignoring any whitespace or divider character, whitespace
 * is kept and is part of the value until the end of the file or a carraige return.
 * This shouldn't be a problem in most cases.  By default, non-string properties
 * have all whitespace removed by the Trimmer.  For string properties, all white
 * space is removed, but double quotes can be used to preserve it.  If, however,
 * you set a custom Trimmer implementation that preserves all whitespace, be
 * aware of this behaviour.
 * 
 * TODO:  Add these docs to the User Manual.
 * </ul>
 * @author eeverman
 */
public class PropFileLoader extends BaseLoader implements ConfigSamplePrinter {

	String specificLoadDescription = null;
	
	@Override
	public LoaderValues load(RuntimeDefinition appConfigDef, List<String> cmdLineArgs,
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

				for(Entry<Object, Object> entry : props.entrySet()) {
					if (entry.getKey() != null && entry.getValue() != null) {
						String k = entry.getKey().toString();
						String v = entry.getValue().toString();

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
	public void printSampleStart(PrintStream out) {
		out.println(TextUtil.repeat("#", DEFAULT_LINE_WIDTH));
		out.println(TextUtil.padRight("# Sample properties file generated by " + 
				AndHow.ANDHOW_NAME + "  " + AndHow.ANDHOW_TAG_LINE + "  ", "#", DEFAULT_LINE_WIDTH));
		out.println(TextUtil.padRight(TextUtil.repeat("#", 50) + "  " + AndHow.ANDHOW_URL + " ", "#", DEFAULT_LINE_WIDTH));
		out.println("# " + ConfigSamplePrinter.REQUIRED_HEADER_TEXT);
	}
	
	private static final String LINE_PREFIX = "# ";
	
	@Override
	public void printPropertyGroupStart(PrintStream out, Class<? extends PropertyGroup> group) {
		out.println();
		out.println();
		out.println(TextUtil.repeat("#", DEFAULT_LINE_WIDTH));
		
		String name = null;
		String desc = null;
		
		GroupInfo groupDesc = group.getAnnotation(GroupInfo.class);
		if (groupDesc != null) {
			name = TextUtil.trimToNull(groupDesc.name());
			desc = TextUtil.trimToNull(groupDesc.desc());
		}
		
		if (name != null || desc != null) {
			if (name != null && desc != null) {
				
				if (! desc.endsWith(".")) desc = desc + ".";
				
				TextUtil.println(out, DEFAULT_LINE_WIDTH, LINE_PREFIX, 
						"Property Group '{}' - {}  Defined in interface {}", name, desc, group.getCanonicalName());

			} else {
				TextUtil.println(out, LINE_PREFIX + "Property Group {}", group.getCanonicalName());
				TextUtil.println(out, DEFAULT_LINE_WIDTH, LINE_PREFIX, "Description: {}", (name != null)?name:desc);
			}
			
			
		} else {
			TextUtil.println(out, "# Property Group {}", group.getCanonicalName());
		}
		
	}
	
	
	@Override
	public void printProperty(PrintStream out, Class<? extends PropertyGroup> group,
			Property<?> prop) throws IllegalArgumentException, IllegalAccessException, SecurityException {
		
		
		String propFieldName = PropertyGroup.getFieldName(group, prop);
		String propCanonName = PropertyGroup.getCanonicalName(group, prop);
		
		out.println();
		TextUtil.println(out, DEFAULT_LINE_WIDTH, LINE_PREFIX, "{} ({}) {}{}", 
				propFieldName,
				prop.getValueType().getDestinationType().getSimpleName(),
				(prop.isRequired())?ConfigSamplePrinter.REQUIRED_KEYWORD:"",
				(TextUtil.trimToNull(prop.getShortDescription()) == null)?"":" - " + prop.getShortDescription());
		
		if (prop.getDefaultValue() != null) {
			out.println(LINE_PREFIX + DEFAULT_VALUE_TEXT + ": " + prop.getDefaultValue());
		}
		
		if (TextUtil.trimToNull(prop.getHelpText()) != null) {
			TextUtil.println(out, DEFAULT_LINE_WIDTH, LINE_PREFIX, prop.getHelpText());
		}
		
		if (prop.getValidators().size() == 1) {
			TextUtil.println(out, DEFAULT_LINE_WIDTH, LINE_PREFIX, THE_VALUE_MUST_TEXT + " " + prop.getValidators().get(0).getTheValueMustDescription());
		}
		
		if (prop.getValidators().size() > 1) {
			out.println(LINE_PREFIX + THE_VALUE_MUST_TEXT + ":");	
			for (Validator v : prop.getValidators()) {
				out.println(LINE_PREFIX + "\t- " + v.getTheValueMustDescription());
			}
		}
		
		//print the actual sample line
		if (prop.getDefaultValue() != null) {
			TextUtil.println(out, "{} = {}", 
					propCanonName, 
					prop.getDefaultValue());
		} else {
			TextUtil.println(out, "{} = [{}]", 
					propCanonName, 
					prop.getValueType().getDestinationType().getSimpleName());
		}
		
		
	}

	@Override
	public void printPropertyGroupEnd(PrintStream out, Class<? extends PropertyGroup> group) {
	}
	
	@Override
	public void printSampleEnd(PrintStream out) {
		out.println();
		out.println(TextUtil.repeat("#", DEFAULT_LINE_WIDTH));
		out.println(TextUtil.repeat("#", DEFAULT_LINE_WIDTH));
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
