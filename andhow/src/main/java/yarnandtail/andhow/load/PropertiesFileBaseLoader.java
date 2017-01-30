package yarnandtail.andhow.load;

import java.io.InputStream;
import java.util.*;
import yarnandtail.andhow.*;
import yarnandtail.andhow.sample.PropFileLoaderSamplePrinter;

/**
 * Shared functionality for all Property file loaders.
 * 
 * @author ericeverman
 */
public abstract class PropertiesFileBaseLoader extends BaseLoader {

	public PropertiesFileBaseLoader() {
	}

	/**
	 * Read an input stream into a Java properties file.
	 * 
	 * If the InputStream is null, null is returned.  If an exception is encountered
	 * reading the file, a LoaderException is thrown.
	 * 
	 * @param inputStream
	 * @param sourceType What the fromPath is, eg, 'classpath', 'file path', etc.
	 * @param fromPath The path the inputstream was opened on.
	 * @return
	 * @throws yarnandtail.andhow.load.LoaderException
	 */
	protected Properties loadPropertiesFromInputStream(InputStream inputStream, 
			String sourceType, String fromPath) throws LoaderException {
		
		if (inputStream == null) {
			return null;
		}
		
		try {
			Properties props = new Properties();
			props.load(inputStream);
			return props;
		} catch (Exception e) {
			throw new LoaderException(e, this, "properties file at " + sourceType + ": '" + fromPath + "'");
		}
	}
	

	public LoaderValues load(ConstructionDefinition appConfigDef,
			ValueMapWithContext existingValues, Properties props) {
			
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

}
