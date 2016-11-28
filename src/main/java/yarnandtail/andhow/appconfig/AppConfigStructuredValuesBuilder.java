package yarnandtail.andhow.appconfig;

import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.LoaderValues;

/**
 *
 * @author eeverman
 */
public class AppConfigStructuredValuesBuilder extends AppConfigStructuredValuesImpl {
	
	public AppConfigStructuredValuesBuilder() {
	}

	
	public void addValues(LoaderValues values) {
		loadedValuesList.add(values);
	}
	
	public AppConfigStructuredValues getAppConfigStructuredValues() {
		return new AppConfigStructuredValuesImpl(loadedValuesList);
	}
}
