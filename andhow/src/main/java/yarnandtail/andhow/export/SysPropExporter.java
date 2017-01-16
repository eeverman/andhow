package yarnandtail.andhow.export;

import yarnandtail.andhow.*;

/**
 * Exports Properties to System.Properties.
 * 
 * @author ericeverman
 */
public class SysPropExporter extends BaseExporter {

	public SysPropExporter(INCLUDE_CANONICAL_NAMES includeCanonical, INCLUDE_OUT_ALIAS_NAMES includeOutAlias) {
		super(includeCanonical, includeOutAlias);
	}
	
	@Override
	public <T> void doExport(String name, Property<T> property, 
			ConstructionDefinition definition, ValueMap values) {
		
		T value = property.getValue(values);
		
		if (value != null) {
			System.setProperty(name, property.getValueType().toString(value));
		}
	}
	
}