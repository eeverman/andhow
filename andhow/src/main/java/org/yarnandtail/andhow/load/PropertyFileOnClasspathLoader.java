package org.yarnandtail.andhow.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.property.StrProp;

/**
 * Reads from a Java .property file on the classpath, following standard java conventions
 * for the structure of those file.
 * 
 * This loader finds the properties file to loadJavaPropsToAndhowProps from based on a classpath property
 it is passed in its constructor.  Since this loader expects to find a value
 loaded for that property, an earlier loader must have loaded a value for it.
 
 It is considered an error if its configured classpath does not point to a valid
 properties file.  It is not considered an error if the classpath property has
 not been assigned a value.
 
 This loader trims incoming values for String type properties using the
 Trimmer of the associated Property.
 This loader considers it a problem to find unrecognized properties in a 
 properties file and will throw a RuntimeException if that happens.
 
 Properties File Loaders use the java.util.Properties class to read properties, 
 so several behaviors are determined by that class.
 
 In rare cases, whitespace handling of the JVM Properties file parser may be an issue. 
 The property value is generally terminated by the end of the line. Whitespace 
 following the property value is not ignored, and is treated as part of the property value.
 This is not a problem in most cases because by default, properties have Trimmers 
 that remove whitespace. Other Trimmer implementations can be assigned to properties, 
 however, so be aware of the implementations if your are using non-default Trimmers.
 
 The PropFileLoader is unable to detect duplicate properties (i.e., the same key 
 value appearing more than once in a prop file). Instead of aborting the application 
 startup with an error, only the last of the property values in the file is assigned. 
 This is a basic limitation of the JVM Properties class, which silently ignores 
 multiple entries, each value overwriting the last.
 * 
 * @author eeverman
 */
public class PropertyFileOnClasspathLoader extends PropertyFileBaseLoader {

	/** Store it as a list, but we currently only accept one */
	StrProp classpath;
	
	String specificLoadDescription = null;
	
	public PropertyFileOnClasspathLoader(StrProp classpathOfPropertyFile) {
		classpath = classpathOfPropertyFile;
	}
	

	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, ValueMapWithContext existingValues) {

		String path = existingValues.getValue(classpath);
		specificLoadDescription = "file on classpath at: " + path;
		
		if (path != null) {

			LoaderValues vals = load(appConfigDef, existingValues, path);
			return vals;
			
		} else {
			//The classpath is not specified, so just ignore
			return new LoaderValues(this);
		}
	}
	
	/**
	 * Load from a non-null classpath path.
	 * 
	 * @param appConfigDef
	 * @param existingValues
	 * @param path
	 * @return 
	 */
	public LoaderValues load(ConstructionDefinition appConfigDef,
			ValueMapWithContext existingValues, String path) {
					
		try (InputStream inS = PropertyFileOnClasspathLoader.class.getResourceAsStream(path)) {

			if (inS == null) {
				//If the file is not there, the inS is null (no exception thrown)
				return new LoaderValues(this, new LoaderProblem.SourceNotFoundLoaderProblem(this, "Expected file at classpath:" + path));
			}
			
			return loadInputStreamToProps(inS, path, appConfigDef, existingValues);
			
		} catch (LoaderException e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, e.getCause(), "classpath:" + path));
		} catch (IOException ioe) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, ioe, "classpath:" + path));
		}
	}
	
	
	@Override
	public List<Property> getInstanceConfig() {
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
