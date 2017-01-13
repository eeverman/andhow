package yarnandtail.andhow.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import yarnandtail.andhow.Alias;
import yarnandtail.andhow.ConstructionProblem;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.NamingStrategy;

/**
 * The defined set of PropertyGroups, child Properties and their names for use by the app.
 * 
 * This class does not contain any values for the Properties or Loaders - it is
 * just the list of known Properties and their structure in PropertyGroups.
 * 
 * This class is intended to be constructed populated by Loaders during initialization,
 * then should remain unchanged.
 * 
 * @author eeverman
 */
public class RuntimeDefinition {
	public static final List<Property<?>> EMPTY_PROPERTY_LIST = Collections.unmodifiableList(new ArrayList());
	
	
	private final Map<Class<? extends PropertyGroup>, List<Property<?>>> propertiesByGroup = new HashMap();
	private final List<Class<? extends PropertyGroup>> groupList = new ArrayList();
	private final Map<Property<?>, List<Alias>> aliasesByProperty = new HashMap();
	private final Map<String, Property<?>> propertiesByAnyName = new HashMap();
	private final Map<Property<?>, String> canonicalNameByProperty = new HashMap();
	private final List<Property<?>> properties = new ArrayList();
	private final List<ConstructionProblem> constructProblems = new ArrayList();
	
	/**
	 * Adds a PropertyGroup, its Property and the name and aliases for that property
	 * to all the collections.
	 * 
	 * @param group The PropertyGroup parent of the property
	 * @param property The Property to be added
	 * @param names The names associated w/ this property
	 */
	public void addProperty(Class<? extends PropertyGroup> group, Property<?> property, 
			NamingStrategy.Naming names) {
		
		if (group == null || property == null || names == null || names.getCanonicalName() == null) {
			throw new RuntimeException("Null values are not allowed when registering a property.");
		}
		
		aliasesByProperty.put(property, property.getRequestedAliases());
		
		//Build a list of the canonical name and all the aliases (if any) to simplify later logic
		ArrayList<String> allNames = new ArrayList();
		allNames.add(names.getCanonicalName());
		allNames.addAll(names.getInAliases());
		
		if (canonicalNameByProperty.containsKey(property)) {
			ConstructionProblem.DuplicateProperty dupProp = new ConstructionProblem.DuplicateProperty(
					getGroupForProperty(property),
					property, group, property);
			
			constructProblems.add(dupProp);
			return;
		}
		
		//Check for duplicate names
		for (String a : allNames) {
			Property<?> conflictProp = propertiesByAnyName.get(a);
			if (conflictProp != null) {
				ConstructionProblem.NonUniqueNames notUniqueName = new ConstructionProblem.NonUniqueNames(
					getGroupForProperty(conflictProp),
						conflictProp, group, property, a);
						
				constructProblems.add(notUniqueName);
				return;
			}
		}
		
		//Check for bad internal validation configuration (eg, bad regex string)
		for (Validator v : property.getValidators()) {
			if (! v.isSpecificationValid()) {
				ConstructionProblem.InvalidValidationConfiguration badValid = new
					ConstructionProblem.InvalidValidationConfiguration(
					group, property, v);
				
				constructProblems.add(badValid);
				return;
			}
		}
		
		//Check the default value against validation
		if (checkForInvalidDefaultValue(property, group, names.getCanonicalName())) {
			return;
		}
		
		//
		//All checks pass, so add property
		
		canonicalNameByProperty.put(property, names.getCanonicalName());
		properties.add(property);


		for (String a : allNames) {
			propertiesByAnyName.put(a, property);
		}

		List<Property<?>> list = propertiesByGroup.get(group);
		if (list != null) {
			list.add(property);
		} else {
			list = new ArrayList();
			list.add(property);
			propertiesByGroup.put(group, list);
			groupList.add(group);
		}
		
	}
	
	/**
	 * Since code outside this class is adding properties, external code also needs
	 * to be able to record when it cannot add properties to the definition.
	 * 
	 * @param problem 
	 */
	public void addConstructionProblem(ConstructionProblem problem) {
		constructProblems.add(problem);
	}
	
	/**
	 * Finds a registered property by any recognized name.
	 * 
	 * @param name
	 * @return 
	 */
	public Property<?> getProperty(String name) {
		return propertiesByAnyName.get(name);
	}
	
	/**
	 * Returns all aliases (in and out) for a property.
	 * 
	 * This may be different than the aliases requested for the Property, since
	 * the application-level configuration has the ability to add and remove
	 * aliases to mitigate name conflicts.
	 * 
	 * @param property
	 * @return 
	 */
	public List<Alias> getAliases(Property<?> property) {
		return aliasesByProperty.get(property);
	}
		
	/**
	 * Returns the cononical name of a registered property.
	 * 
	 * If the property is not registered, null is returned.
	 * @param prop
	 * @return 
	 */
	public String getCanonicalName(Property<?> prop) {
		return canonicalNameByProperty.get(prop);
	}
	
	/**
	 * Returns an unmodifiable list of registered properties.
	 * 
	 * @return 
	 */
	public List<Property<?>> getProperties() {
		return Collections.unmodifiableList(properties);
	}
	
	/**
	 * Returns an unmodifiable list of all registered groups.
	 * 
	 * @return 
	 */
	public List<Class<? extends PropertyGroup>> getPropertyGroups() {
		return Collections.unmodifiableList(groupList);
	}
	
	/**
	 * Returns a list of Properties registered with the passed group.
	 * If the group is unregistered or has no properties, an empty list is returned.
	 * @param group
	 * @return 
	 */
	public List<Property<?>> getPropertiesForGroup(Class<? extends PropertyGroup> group) {
		List<Property<?>> pts = propertiesByGroup.get(group);
		
		if (pts != null) {
			return Collections.unmodifiableList(pts);
		} else {
			return EMPTY_PROPERTY_LIST;
		}
	}
	
	/**
	 * Finds the PropertyGroup containing the specified Property.
	 * 
	 * @param prop
	 * @return May return null if the Property is not in any group, or during 
	 * construction, if the group has not finished registering all of its properties.
	 */
	public Class<? extends PropertyGroup> getGroupForProperty(Property<?> prop) {
		for (Class<? extends PropertyGroup> group : groupList) {
			if (propertiesByGroup.get(group).contains(prop)) {
				return group;
			}
		}
		
		return null;
	}

	/**
	 * Returns a list of ConstructionProblems found while building the definition.
	 * 
	 * @return Never returns null - only an empty list.
	 */
	public List<ConstructionProblem> getConstructionProblems() {
		return Collections.unmodifiableList(constructProblems);
	}
	
	/**
	 * Checks a Property's default value against its Validators and adds entries
	 * to constructProblems if there are issues.
	 * 
	 * @param <T>
	 * @param property
	 * @param group
	 * @param canonName
	 * @return True if the default value is invalid.
	 */
	protected final <T> boolean checkForInvalidDefaultValue(Property<T> property, 
			Class<? extends PropertyGroup> group, String canonName) {
		
		boolean foundProblem = false;
		
		if (property.getDefaultValue() != null) {
			T t = property.getDefaultValue();
			
			if (t != null) {
			
				for (Validator<T> v : property.getValidators()) {
					if (! v.isValid(t)) {

						ConstructionProblem.InvalidDefaultValue problem = 
								new ConstructionProblem.InvalidDefaultValue(
										group, property, 
										v.getInvalidMessage(t));
						constructProblems.add(problem);
						foundProblem = true;
					}
				}
			}
		}
		
		return foundProblem;
	}
	
}
