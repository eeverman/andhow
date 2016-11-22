package yarnandtail.andhow;

import java.io.PrintStream;
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
	 * Build a fully populated AppConfigDefinition from the points contained in
	 * the passed Groups, using the NamingStrategy to generate names for each.
	 * 
	 * @param groups The ConfigPointGroups from which to find ConfigPoints.  May be null
	 * @param naming  A naming strategy to use when reading the properties during loading
	 * @return A fully configured instance
	 */
	public static AppConfigDefinition 
		doRegisterConfigPoints(List<Class<? extends ConfigPointGroup>> groups, NamingStrategy naming) {

		AppConfigDefinition appDef = new AppConfigDefinition();
		
		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
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
								throw new ConfigurationException(
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
		}
		
		return appDef;

	}
		
	public static void printExceptions(List<? extends Exception> exceptions, PrintStream out) {
		for (Exception ne : exceptions) {
			out.println(ne.getMessage());
		}
	}
}
