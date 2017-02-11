package yarnandtail.andhow.load;

import yarnandtail.andhow.internal.LoaderProblem;
import yarnandtail.andhow.util.TextUtil;
import java.io.InputStream;
import java.util.*;
import yarnandtail.andhow.*;
import yarnandtail.andhow.property.StrProp;

/**
 * Reads from a Java .property file on the classpath, following standard java conventions
 * for the structure of those file.
 * 
 * This loader finds the properties file to load from based on a classpath property
 * it is passed in its constructor.  Since this loader expects to find a value
 * loaded for that property, an earlier loader must have loaded a value for it.
 * 
 * It is considered an error if its configured classpath does not point to a valid
 * properties file.  It is not considered an error if the classpath property has
 * not been assigned a value.
 * 
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property.
 * This loader considers it a problem to find unrecognized properties in a 
 * properties file and will throw a RuntimeException if that happens.
 * 
 * Properties File Loaders use the java.util.Properties class to read properties, 
 * so several behaviors are determined by that class.
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
public class PropertyFileFromClasspathLoader extends PropertyFileBaseLoader {

	/** Store it as a list, but we currently only accept one */
	StrProp classpath;
	
	String specificLoadDescription = null;
	
	public PropertyFileFromClasspathLoader(StrProp classpathOfPropertyFile) {
		classpath = classpathOfPropertyFile;
	}
	

	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {

		String path = existingValues.getEffectiveValue(classpath);
		specificLoadDescription = "file on classpath at: " + path;
		
		if (path != null) {

			LoaderValues vals = load(appConfigDef, existingValues, path);
			if (vals != null) {
				return vals;
			} else {
				LoaderProblem p = new LoaderProblem.SourceNotFoundLoaderProblem(this, 
						TextUtil.format("Could not find a properties file on the classpath at " + 
							"the configured location: {}", path)
				);
				
				return new LoaderValues(this, p);
			}
		} else {
			//The classpath to load from is not specified, so just ignore it
			return new LoaderValues(this);
		}
	}
	
	
	public LoaderValues load(ConstructionDefinition appConfigDef,
			ValueMapWithContext existingValues, String path) {
					
		try {

			InputStream inS = PropertyFileFromClasspathLoader.class.getResourceAsStream(path);
			Properties props = loadPropertiesFromInputStream(inS, "classpath", path);

			if (props != null) {
				return load(appConfigDef, existingValues, props);
			} else {
				return null;
			}
			
		} catch (LoaderException e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, null, null, e));
		}

	}
	
	
	@Override
	public List<Property> getUserLoaderConfig() {
		ArrayList<Property> list = new ArrayList();
		list.add(classpath);
		return list;
	}
	

	
	@Override
	public String getSpecificLoadDescription() {
		
		if (specificLoadDescription != null) {
			return specificLoadDescription;
		} else {
			return "file on classpath at: " + classpath.getValue();
		}
	}
	
	

	
}
