package yarnandtail.andhow.load;

import yarnandtail.andhow.internal.LoaderProblem;
import yarnandtail.andhow.sample.PropFileLoaderSamplePrinter;
import yarnandtail.andhow.util.TextUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
 * The PropFileLoader uses the java.util.Properties class to read properties, 
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
public class PropertiesFileFromClasspathLoader extends PropertiesFileBaseLoader {

	/** Store it as a list, but we currently only accept one */
	List<StrProp> classpaths;
	
	String specificLoadDescription = null;
	
	public PropertiesFileFromClasspathLoader(StrProp classpathOfPropertyFile) {
		classpaths = new ArrayList();
		classpaths.add(classpathOfPropertyFile);
		classpaths = Collections.unmodifiableList(classpaths);
	}
	

	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
		List<String> triedPaths = new ArrayList();
		
		for (StrProp prop : classpaths) {
			
			String path = existingValues.getEffectiveValue(prop);
			
			if (path != null) {
			
				triedPaths.add(path);
				
				LoaderValues vals = load(appConfigDef, existingValues, path);
				if (vals != null) {
					return vals;
				}
			}
		}
		
		//
		// No properties file found
		
		LoaderProblem p = new LoaderProblem.SourceNotFoundLoaderProblem(this, 
				TextUtil.format("Could not find a properties file to read at any of these " + 
					"configured locations: {}, ",
				String.join(", ", triedPaths))
		);
		
		return new LoaderValues(this, p);
	}
	
	
	public LoaderValues load(ConstructionDefinition appConfigDef,
			ValueMapWithContext existingValues, String path) {
		
		specificLoadDescription = "file on classpath at: " + path;
					
		try {

			InputStream inS = PropertiesFileFromClasspathLoader.class.getResourceAsStream(path);
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
		list.addAll(classpaths);
		return list;
	}
	

	
	@Override
	public String getSpecificLoadDescription() {
		return specificLoadDescription;
	}
	
	

	
}
