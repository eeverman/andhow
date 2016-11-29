package yarnandtail.andhow.appconfig;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.ConfigurationException;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.NamingStrategy;

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
		doRegisterConfigPoints(List<Class<? extends ConfigPointGroup>> groups, List<Loader> loaders, NamingStrategy naming) {

		AppConfigDefinition appDef = new AppConfigDefinition();
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				Class<? extends ConfigPointGroup> group = loader.getLoaderConfig();
				if (group != null) {
					
					doRegisterGroup(appDef, group, naming);
					
				}
			}
		}
		
		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
			for (Class<? extends ConfigPointGroup> group : groups) {

				doRegisterGroup(appDef, group, naming);
				
			}
		}
		
		return appDef;

	}
		
	protected static void doRegisterGroup(AppConfigDefinition appDef,
			Class<? extends ConfigPointGroup> group, NamingStrategy naming) {


		Field[] fields = group.getDeclaredFields();

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
								f.getName() + " in " + group.getCanonicalName() + ".  " +
								"Is there a security policy that prevents setting it accessable?");
					}
				}

				NamingStrategy.Naming names = naming.buildNames(cp, group, f.getName());

				appDef.addPoint(group, cp, names);
			}

		}

	}
		
	public static void printExceptions(List<? extends Exception> exceptions, PrintStream out) {
		for (Exception ne : exceptions) {
			out.println(ne.getMessage());
		}
	}
}
