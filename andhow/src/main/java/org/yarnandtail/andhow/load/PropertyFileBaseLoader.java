package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.PropertyValue;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.api.LoaderValues;
import org.yarnandtail.andhow.api.ValueMapWithContext;
import org.yarnandtail.andhow.api.SamplePrinter;
import org.yarnandtail.andhow.api.ConstructionDefinition;
import java.io.InputStream;
import java.util.*;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.sample.PropFileLoaderSamplePrinter;

/**
 * Shared functionality for all Property file loaders.
 * 
 * @author ericeverman
 */
public abstract class PropertyFileBaseLoader extends BaseLoader {

	public PropertyFileBaseLoader() {
	}
	
	public LoaderValues loadInputStreamToProps(InputStream inputStream, 
			String fromPath, ConstructionDefinition appConfigDef,
			ValueMapWithContext existingValues) throws LoaderException {
		
		
		if (inputStream == null) {
			Exception e = new IllegalArgumentException("The InputStream cannot be null");
			throw new LoaderException(e, this, "properties file at '" + fromPath + "'");
		}
		
		try {
			Properties props = new Properties();
			props.load(inputStream);
			
			ArrayList<PropertyValue> values = new ArrayList();
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
	
	@Override
	public String getLoaderType() {
		return "PropertyFile";
	}
	
	@Override
	public String getLoaderDialect() {
		return "KeyValuePair";
	}

}
