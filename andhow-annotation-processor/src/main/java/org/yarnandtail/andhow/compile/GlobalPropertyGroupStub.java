package org.yarnandtail.andhow.compile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.api.BasePropertyGroup;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.NameAndProperty;

/**
 *
 * @author ericeverman
 */
public abstract class GlobalPropertyGroupStub {
	
	
	public abstract String getSimpleRootName();
	
	public abstract String[][] getGroupPaths();
	
	public String getCanonicalRootName() {

		String pkgName = "";
		
		Package pkg = this.getClass().getPackage();
		
		if (pkg != null && pkg.getName().length() > 0) {
			pkgName = pkg.getName() + ".";
		}
		
		return pkgName + getSimpleRootName();
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
	
	public List<NameAndProperty> getProperties() 
		throws IllegalArgumentException, IllegalAccessException, SecurityException {

		List<NameAndProperty> props = new ArrayList();
		
		Field[] fields = this.getClass().getFields();

		for (Field f : fields) {

			if (Modifier.isStatic(f.getModifiers()) && Property.class.isAssignableFrom(f.getType())) {

				Property cp = null;

				try {
					cp = (Property) f.get(null);
				} catch (Exception ex) {	
					f.setAccessible(true);
					cp = (Property) f.get(null);
				}
					
				props.add(new NameAndProperty(f.getName(), cp));
				
			}

		}
		
		return props;
	}
}
