package yarnandtail.andhow.export;
		
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;

public abstract class BaseExporter implements Exporter {
	protected INCLUDE_CANONICAL_NAMES includeCanonical;
	protected INCLUDE_OUT_ALIAS_NAMES includeOutAlias;
	
	public BaseExporter(INCLUDE_CANONICAL_NAMES includeCanonical, INCLUDE_OUT_ALIAS_NAMES includeOutAlias) {
		if (includeCanonical == null || includeOutAlias == null) {
			throw new AppFatalException("The Exporter constructor arguments cannot be null");
		}
		
		this.includeCanonical = includeCanonical;
		this.includeOutAlias = includeOutAlias;
	}
	
	
	/**
	 * Subclasses can implement just this method.
	 * 
	 * @param name
	 * @param property
	 * @param definition
	 * @param values 
	 */
	public abstract <T> void doExport(String name, Property<T> property, 
			ConstructionDefinition definition, ValueMap values);
		
	
	@Override
	public void export(ConstructionDefinition definition, ValueMap values) {
		for (Class<? extends PropertyGroup> pg : definition.getPropertyGroups()) {
			export(pg, definition, values);
		}
	}

	@Override
	public void export(Class<? extends PropertyGroup> group, ConstructionDefinition definition, ValueMap values) {
		List<Property<?>> props = definition.getPropertiesForGroup(group);
		
		for (Property<?> prop : props) {
			export(prop, definition, values);
		}
	}
	
	public void export(Property<?> property, ConstructionDefinition definition, ValueMap values) {
		
		List<String> names = new ArrayList();
		
		boolean exportCanon = false;
		boolean exportAlias = false;
		boolean hasOut = hasOutAlias(property, definition);
		
		if (INCLUDE_OUT_ALIAS_NAMES.ALL.equals(includeOutAlias) && hasOut) {
			exportAlias = true;
		}
		
		if (INCLUDE_CANONICAL_NAMES.ALL.equals(includeCanonical)) {
			exportCanon = true;
		} else if (INCLUDE_CANONICAL_NAMES.NONE.equals(includeCanonical)) {
			exportCanon = false;
		} else {
			exportCanon = !hasOut;
		}
		
		if (exportCanon) {
			names.add(definition.getCanonicalName(property));
		}
		
		if (exportAlias) {
			
			definition.getAliases(property).stream().
					filter(a -> a.isOut()).
					forEachOrdered(a -> names.add(a.getName()));
			
		}
	}
	
	protected boolean hasOutAlias(Property<?> property, ConstructionDefinition definition) {
		List<Alias> aliases = definition.getAliases(property);
		
		for (Alias a : aliases) {
			if (a.isOut()) {
				return true;
			}
		}
		
		return false;
	}
}