package org.yarnandtail.andhow.compile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import org.yarnandtail.andhow.api.BasePropertyGroup;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 *
 * @author ericeverman
 */
public abstract class SamplePropertyStub {
	
	
	public abstract Class<?> getRootClass();
	
	public abstract List<PropRef> getPropertyRefs();
	
	public String getCanonicalRootName() {
		return getRootClass().getCanonicalName();
	}
	
	
	
	public List<Registration> getRegistrations() {
		
		List<Registration> registrations = new ArrayList();
		ArrayList<String> propNames = new ArrayList();
		List<PropRef> props = getPropertyRefs();
		
		String currentInnerPath = "";
		

		for (PropRef propRef : props) {
			
			if (propRef.innerPath != null) {
				
				//This is either the first entry or we just changed inner paths
				//so need to store all previous props
				if (propNames.size() > 0) {
					registrations.add(new Registration(
							getCanonicalRootName() + currentInnerPath,
							propNames.toArray(new String[propNames.size()])));
					propNames.clear();
				} else {
					//this is just the first entry
				}
				
				currentInnerPath = "$" + String.join("$", propRef.innerPath);
			}	
			
			propNames.add(propRef.getName());
		
		}
		
		//Catch the last set of props where there was no inner name change
		if (propNames.size() > 0) {
			registrations.add(new Registration(
					getCanonicalRootName() + currentInnerPath,
					propNames.toArray(new String[propNames.size()])));
		}
		
		return registrations;
		
	}
	

	
	public static class PropRef {
		final String[] innerPath;
		final String name;
		
		PropRef(String name) {
			this.name = name;
			innerPath = null;
		}
		
		PropRef(String name, String... pathStep) {
			this.name = name;
			innerPath = pathStep;
		}

		public String[] getInnerPath() {
			return innerPath;
		}

		public String getName() {
			return name;
		}
	}
	
	public static class Registration {
		String classCanonName;
		String[] propNames;
		
		public Registration(String classCanonicalName, String... propertyNames) {
			this.classCanonName = classCanonicalName;
			this.propNames = propertyNames;
		}
		
		public String getClassCanonicalName() {
			return classCanonName;
		}
		
		public List<String> getPropertyNames() {
			return Arrays.asList(propNames);
		}
	}
}
