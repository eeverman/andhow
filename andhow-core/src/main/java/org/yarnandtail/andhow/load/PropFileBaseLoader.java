package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.io.InputStream;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.sample.PropFileLoaderSamplePrinter;

/**
 * Shared functionality for all Property file loaders.
 * 
 * @author ericeverman
 */
public abstract class PropFileBaseLoader extends BaseLoader 
		implements ReadLoader, LocalFileLoader {
	
	/**
	 * Property containing the path of a property file. XOR w/ classpathStr
	 */
	protected Property<String> pathProp;

	/**
	 * String containing path of a property file. XOR w/ classpathProp
	 */
	protected String pathStr;

	/** The missing file A problem. */
	protected boolean missingFileAProblem = true;
	
	/** The unknown property A problem. */
	protected boolean unknownPropertyAProblem = true;
	
	/**
	 * Instantiates a new prop file base loader.
	 */
	public PropFileBaseLoader() { /* empty for easy construction */ }
	
	/**
	 * Load input stream to props.
	 *
	 * @param inputStream the input stream
	 * @param fromPath the from path
	 * @param appConfigDef the app config def
	 * @param existingValues the existing values
	 * @return the loader values
	 * @throws LoaderException the loader exception
	 */
	public LoaderValues loadInputStreamToProps(InputStream inputStream, 
			String fromPath, StaticPropertyConfigurationInternal appConfigDef,
			ValidatedValuesWithContext existingValues) throws LoaderException {
		
		
		if (inputStream == null) {
			Exception e = new IllegalArgumentException("The InputStream cannot be null");
			throw new LoaderException(e, this, "properties file at '" + fromPath + "'");
		}
		
		try {
			Properties props = new Properties();
			props.load(inputStream);
			
			ArrayList<ValidatedValue> values = new ArrayList();
			ProblemList<Problem> problems = new ProblemList();

			Set<Object> keys = props.keySet();
			for(Object key : keys) {
				if (key != null) {
					String k = key.toString();
					String v = props.getProperty(k);

					attemptToAdd(appConfigDef, values, problems, k, v);
				}
			}

			values.trimToSize();
			return new LoaderValues(this, values, problems);
			
		} catch (Exception e) {
			//These are nominally IO exceptions
			throw new LoaderException(e, this, "properties file at '" + fromPath + "'");
		}
	}
	
	/**
	 * Utility method to simplify finding the effective path.
	 * 
	 * @param existingValues
	 * @return 
	 */
	protected String getEffectivePath(ValidatedValuesWithContext existingValues) {
		if (pathStr != null) {
			return pathStr;
		} else if (pathProp != null) {
			if (existingValues != null) {
				return existingValues.getValue(pathProp);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.load.BaseLoader#getInstanceConfig()
	 */
	@Override
	public List<Property> getInstanceConfig() {
		if (pathProp != null) {
			ArrayList<Property> list = new ArrayList();
			list.add(pathProp);
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#isTrimmingRequiredForStringValues()
	 */
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.load.BaseLoader#getConfigSamplePrinter()
	 */
	@Override
	public SamplePrinter getConfigSamplePrinter() {
		return new PropFileLoaderSamplePrinter();
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#getLoaderType()
	 */
	@Override
	public String getLoaderType() {
		return "PropertyFile";
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#getLoaderDialect()
	 */
	@Override
	public String getLoaderDialect() {
		return "KeyValuePair";
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.ReadLoader#setUnknownPropertyAProblem(boolean)
	 */
	@Override
	public void setUnknownPropertyAProblem(boolean isAProblem) {
		unknownPropertyAProblem = isAProblem;
	}

	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.ReadLoader#isUnknownPropertyAProblem()
	 */
	@Override
	public boolean isUnknownPropertyAProblem() {
		return unknownPropertyAProblem;
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.LocalFileLoader#setMissingFileAProblem(boolean)
	 */
	@Override
	public void setMissingFileAProblem(boolean isAProblem) {
		missingFileAProblem = isAProblem;
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.LocalFileLoader#isMissingFileAProblem()
	 */
	@Override
	public boolean isMissingFileAProblem() {
		return missingFileAProblem;
	}	
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.LocalFileLoader#setFilePath(java.lang.String)
	 */
	@Override
	public void setFilePath(String path) {
		if (path != null && pathProp != null) {
			throw new IllegalArgumentException("The FilePath cannot be specified "
					+ "as both a String and StrProp");
		}
		pathStr = path;
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.LocalFileLoader#setFilePath(org.yarnandtail.andhow.api.Property)
	 */
	@Override
	public void setFilePath(Property<String> path) {
		if (path != null && pathStr != null) {
			throw new IllegalArgumentException("The FilePath cannot be specified "
					+ "as both a String and StrProp");
		}
		pathProp = path;
	}

}
