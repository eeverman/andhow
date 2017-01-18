package yarnandtail.andhow.export;

import yarnandtail.andhow.*;

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
			ConstructionDefinition definition, ValueMap values) {
		
		T value = property.getValue(values);
		
		if (value != null) {
			System.setProperty(name, property.getValueType().toString(value));
		}
	}
	
}