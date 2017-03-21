package org.yarnandtail.andhow.export;
		
import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.api.BasePropertyGroup;

/**
 * All implementations must have a zero argument constructor.
 * 
 * @author ericeverman
 */
public abstract class BaseExporter implements Exporter {
	protected EXPORT_CANONICAL_NAME includeCanonical;
	protected EXPORT_OUT_ALIASES includeOutAlias;
	

	@Override
	public void setExportByOutAliases(EXPORT_OUT_ALIASES option) {
		
		if (option == null) {
			throw new AppFatalException("The export alias option cannot be null");
		}
		
		includeOutAlias = option;
	}

	@Override
	public void setExportByCanonicalName(EXPORT_CANONICAL_NAME option) {
		
		if (option == null) {
			throw new AppFatalException("The canonical name option cannot be null");
		}
		
		includeCanonical = option;
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
		for (Class<? extends BasePropertyGroup> pg : definition.getPropertyGroups()) {
			export(pg, definition, values);
		}
	}

	@Override
	public void export(Class<? extends BasePropertyGroup> group, ConstructionDefinition definition, ValueMap values) {
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
		
		if (EXPORT_OUT_ALIASES.ALWAYS.equals(includeOutAlias) && hasOut) {
			exportAlias = true;
		}
		
		if (EXPORT_CANONICAL_NAME.ALWAYS.equals(includeCanonical)) {
			exportCanon = true;
		} else if (EXPORT_CANONICAL_NAME.NEVER.equals(includeCanonical)) {
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
					forEachOrdered(a -> names.add(a.getActualName()));
			
		}
		
		names.stream().forEach(n -> doExport(n, property, definition, values));
		
	}
	
	protected boolean hasOutAlias(Property<?> property, ConstructionDefinition definition) {
		List<EffectiveName> aliases = definition.getAliases(property);
		
		for (EffectiveName a : aliases) {
			if (a.isOut()) {
				return true;
			}
		}
		
		return false;
	}
}