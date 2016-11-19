package yarnandtail.andhow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for AppConfiguration
 * @author eeverman
 */
public class AppConfigUtil {
	

	/**
	 * Builds a nested map/list of ConfigPoints and the ConfigPointGroup they belong to.
	 * The structure of the Map is like this:
	 * ConfigPointGroup_1 (Map Key)
	 * List of contained ConfigPoints (Map Value)
	 * ConfigPointGroup_2 (Map Key) ...etc
	 * 
	 * @param groups 
	 */
	public static AppConfigDefinition 
		doRegisterConfigPoints(List<Class<? extends ConfigPointGroup>> groups, NamingStrategy naming) {

		AppConfigDefinition appDef = new AppConfigDefinition();
		
		for (Class<? extends ConfigPointGroup> grp : groups) {
			
			Field[] fields = grp.getDeclaredFields();

			for (Field f : fields) {

				if (Modifier.isStatic(f.getModifiers()) && ConfigPoint.class.isAssignableFrom(f.getType())) {

					ConfigPoint cp = null;

					try {
						cp = (ConfigPoint) f.get(null);
					} catch (Exception ex) {	
						try {
							f.setAccessible(true);
							cp = (ConfigPoint) f.get(null);
						} catch (Exception ex1) {
							throw new RuntimeException(
									"Unable to access non-public field " + 
									f.getName() + " in " + grp.getCanonicalName() + ".  " +
									"Is there a security policy that prevents setting it accessable?");
						}
					}
					
					NamingStrategy.Naming names = naming.buildNames(cp, grp, f.getName());

					appDef.addPoint(grp, cp, names);
				}

			}
		}
		
		return appDef;

	}
}
