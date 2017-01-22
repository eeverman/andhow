package yarnandtail.andhow.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.*;

/**
 * An immutable instance that can be used during runtime.
 * 
 * @author eeverman
 */
public class ConstructionDefinitionImmutable implements ConstructionDefinition {

	private final NamingStrategy namingStrategy;
	private final List<Class<? extends PropertyGroup>> groupList;
	private final List<Property<?>> properties;
	private final Map<Class<? extends PropertyGroup>, List<Property<?>>> propertiesByGroup;
	private final Map<String, Property<?>> propertiesByAnyName;
	private final Map<Property<?>, List<Alias>> aliasesByProperty;
	private final Map<Property<?>, String> canonicalNameByProperty;
	private final List<ExportGroup> exportGroups;
	

	public ConstructionDefinitionImmutable(
			NamingStrategy namingStrategy,
			List<Class<? extends PropertyGroup>> groupList,
			List<Property<?>> properties,
			Map<Class<? extends PropertyGroup>, List<Property<?>>> propertiesByGroup, 
			Map<String, Property<?>> propertiesByAnyName, 
			Map<Property<?>, List<Alias>> aliasesByProperty, 
			Map<Property<?>, String> canonicalNameByProperty,
			List<ExportGroup> exportGroups) {
		
		this.namingStrategy = namingStrategy;

		//Full detach incomming data from existing collections for immutability
		
		ArrayList<Class<? extends PropertyGroup>> gl = new ArrayList();
		gl.addAll(groupList);
		gl.trimToSize();
		this.groupList = Collections.unmodifiableList(gl);
		
		ArrayList<Property<?>> props = new ArrayList();
		props.addAll(properties);
		props.trimToSize();
		this.properties = Collections.unmodifiableList(props);
		
		Map<Class<? extends PropertyGroup>, List<Property<?>>> propsByGrp = new HashMap();
		propsByGrp.putAll(propertiesByGroup);
		this.propertiesByGroup = Collections.unmodifiableMap(propsByGrp);
		
		Map<String, Property<?>> propsByName = new HashMap();
		propsByName.putAll(propertiesByAnyName);
		this.propertiesByAnyName = Collections.unmodifiableMap(propsByName);
		
		Map<Property<?>, List<Alias>> alsByProp = new HashMap();
		alsByProp.putAll(aliasesByProperty);
		this.aliasesByProperty = Collections.unmodifiableMap(alsByProp);

		Map<Property<?>, String> canonByProp = new HashMap();
		canonByProp.putAll(canonicalNameByProperty);
		this.canonicalNameByProperty = Collections.unmodifiableMap(canonByProp);
		
		ArrayList<ExportGroup> expGroups = new ArrayList();
		expGroups.addAll(exportGroups);
		expGroups.trimToSize();
		this.exportGroups = Collections.unmodifiableList(expGroups);
		
	}
	
	@Override
	public Property<?> getProperty(String name) {
		return propertiesByAnyName.get(name);
	}
	
	@Override
	public List<Alias> getAliases(Property<?> property) {
		return Collections.unmodifiableList(aliasesByProperty.get(property));
	}
		
	@Override
	public String getCanonicalName(Property<?> prop) {
		return canonicalNameByProperty.get(prop);
	}
	
	@Override
	public List<Property<?>> getProperties() {
		return properties;
	}
	
	@Override
	public List<Class<? extends PropertyGroup>> getPropertyGroups() {
		return groupList;
	}
	
	@Override
	public List<Property<?>> getPropertiesForGroup(Class<? extends PropertyGroup> group) {
		List<Property<?>> pts = propertiesByGroup.get(group);
		
		if (pts != null) {
			return Collections.unmodifiableList(pts);
		} else {
			return EMPTY_PROPERTY_LIST;
		}
	}
	
	@Override
	public Class<? extends PropertyGroup> getGroupForProperty(Property<?> prop) {
		for (Class<? extends PropertyGroup> group : groupList) {
			if (propertiesByGroup.get(group).contains(prop)) {
				return group;
			}
		}
		
		return null;
	}
	
	@Override
	public List<ExportGroup> getExportGroups() {
		return exportGroups;
	}
	
	@Override
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

}
