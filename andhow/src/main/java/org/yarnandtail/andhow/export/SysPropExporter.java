package org.yarnandtail.andhow.export;

import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.StaticPropertyConfiguration;
import org.yarnandtail.andhow.api.ValidatedValues;

/**
 * Exports Properties to System.Properties.
 * 
 * @author ericeverman
 */
public class SysPropExporter extends BaseExporter {

	/**
	 * A default constructor is required.
	 */
	public SysPropExporter() {}


	
	@Override
	public <T> void doExport(String name, Property<T> property, 
			StaticPropertyConfiguration definition, ValidatedValues values) {
		T value = values.getValue(property);
		
		if (value != null) {
			System.setProperty(name, property.getValueType().toString(value));
		}
	}
	
}