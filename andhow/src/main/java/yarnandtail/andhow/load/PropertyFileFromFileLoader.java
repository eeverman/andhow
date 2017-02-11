package yarnandtail.andhow.load;

import java.io.*;
import yarnandtail.andhow.internal.LoaderProblem;
import yarnandtail.andhow.util.TextUtil;
import java.util.*;
import yarnandtail.andhow.*;
import yarnandtail.andhow.property.StrProp;

/**
 * Reads from a Java .property file from the filesystem, following standard java conventions
 * for the structure of those file.
 * 
 * This loader finds the properties file to load from based on a file path property
 * it is passed in its constructor.  Since this loader expects to find a value
 * loaded for that property, an earlier loader must have loaded a value for it.
 * 
 * It is considered an error if its configured file path does not point to a valid
 * properties file.  It is not considered an error if the file path property has
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
public class PropertyFileFromFileLoader extends PropertyFileBaseLoader {

	/** Store it as a list, but we currently only accept one */
	StrProp filepath;
	
	String specificLoadDescription = null;
	
	public PropertyFileFromFileLoader(StrProp filePathOfPropertyFile) {
		filepath = filePathOfPropertyFile;
	}
	

	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {

		String path = existingValues.getEffectiveValue(filepath);
		specificLoadDescription = TextUtil.format("file on the file system at path : {} ({})",
					path, getAbsPath(path)) ;
		
		if (path != null) {

			LoaderValues vals = load(appConfigDef, existingValues, path);
			return vals;
			
		} else {
			//The filepath to load from is not specified, so just ignore it
			return new LoaderValues(this);
		}
	}
	
	
	public LoaderValues load(ConstructionDefinition appConfigDef,
			ValueMapWithContext existingValues, String path) {
					
		try {

			File propFile = new File(path);
			if (propFile.exists() && propFile.canRead()) {

				try (FileInputStream in = new FileInputStream(propFile)) {
					Properties props = loadPropertiesFromInputStream(in, "file system", path);
					if (props != null) {
						return load(appConfigDef, existingValues, props);
					} else {
						LoaderProblem p = new LoaderProblem.SourceNotFoundLoaderProblem(this, 
								TextUtil.format("Could not find a properties file in the file system at " + 
									"the configured path: {} ({})", path, propFile.getAbsolutePath())
						);

						return new LoaderValues(this, p);
					}
				}
			} else {
				return new LoaderValues(this, new LoaderProblem.SourceNotFoundLoaderProblem(this, path));
			}

			
		} catch (Exception e) {
			return new LoaderValues(this, new LoaderProblem.IOLoaderProblem(this, null, null, e));
		}

	}
	
	
	@Override
	public List<Property> getUserLoaderConfig() {
		ArrayList<Property> list = new ArrayList();
		list.add(filepath);
		return list;
	}
	

	
	@Override
	public String getSpecificLoadDescription() {
		
		if (specificLoadDescription != null) {
			return specificLoadDescription;
		} else {
			
			return TextUtil.format("file on the file system at path : {} ({})",
					filepath.getValue(), getAbsPath(filepath.getValue())) ;
		}
	}
	
	/**
	 * Completely safe way to convert a file system path to an absolute path.
	 * never errors or returns null.
	 * @param anything
	 * @return 
	 */
	private String getAbsPath(String anything) {
		
		try {
			File f = new File(anything);
			return f.getAbsolutePath();
		} catch (Exception e) {
			return "[Unknown absolute path]";
		}
		
	}
	
	

	
}
