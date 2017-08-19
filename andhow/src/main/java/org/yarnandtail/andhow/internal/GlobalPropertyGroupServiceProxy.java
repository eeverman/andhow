package org.yarnandtail.andhow.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.api.BasePropertyGroup;
import org.yarnandtail.andhow.api.Property;

/**
 *
 * @author ericeverman
 */
public class GlobalPropertyGroupServiceProxy {
	
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
