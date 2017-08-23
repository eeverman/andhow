package org.yarnandtail.andhow.compile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ericeverman
 */
public class SampleGlobalPropertyGroupStub extends GlobalPropertyGroupStub {

	@Override
	public String getSimpleRootName() {
		return SampleNestedPropGroup.class.getSimpleName();
	}
	
	public String getCanonicalRootName() {

		String pkgName = "";
		
		Package pkg = this.getClass().getPackage();
		
		if (pkg != null && pkg.getName().length() > 0) {
			pkgName = pkg.getName() + ".";
		}
		
		return pkgName + getSimpleRootName();
	}
	
	@Override
	public String[][] getGroupPaths() {
		return new String[][] {
			{"Nested", "Config"}
		};
	}
	
	public List<Class<?>> getGroups() throws Exception {
		List<Class<?>> groups = new ArrayList();
		
		Class<?> rootClazz = Class.forName(getCanonicalRootName());
		
		
		for (String[] path : getGroupPaths()) {
			
			Class<?> currentClazz = rootClazz;
			
			for (String step : path) {
				currentClazz = findClass(currentClazz, step);
			}
			
			groups.add(currentClazz);
			
		}
		
		return groups;
	}
	
	public Class<?> findClass(Class<?> parent, String childName) throws Exception {
		Class<?>[] clazzes = parent.getDeclaredClasses();
		
		for (Class<?> clazz : clazzes) {
			if (childName.equals(clazz.getSimpleName())) {
				return clazz;
			}
		}
		
		throw new Exception(parent.getCanonicalName() + 
				" does not contain a nested class or interface named " + childName);
		
	}

}
