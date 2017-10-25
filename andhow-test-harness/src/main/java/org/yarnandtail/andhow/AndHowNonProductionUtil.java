package org.yarnandtail.andhow;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 *
 * @author ericeverman
 */
public class AndHowNonProductionUtil {
	
	public static final String PERMISSION_MSG = 
			"There is some type of permissions/access error while trying to access and modify"
			+ "private fields during testing. "
			+ "Is there a security manager enforcing security during testing?";
	
	public static AndHow getAndHowInstance() {
		try {
			Field ahInstanceField = AndHow.class.getDeclaredField("singleInstance");
			ahInstanceField.setAccessible(true);
			
			AndHow ahInstance = (AndHow)(ahInstanceField.get(null));
			
			return ahInstance;
		} catch (Exception ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}
	
	public static AndHowCore getAndHowCore() {

		try {

			Field ahInstanceField = AndHow.class.getDeclaredField("singleInstance");
			ahInstanceField.setAccessible(true);

			AndHow ahInstance = (AndHow)(ahInstanceField.get(null));

			if (ahInstance == null) {
				return null;
			} else {
				Field ahCoreField = AndHow.class.getDeclaredField("core");
				ahCoreField.setAccessible(true);
				return (AndHowCore) ahCoreField.get(ahInstance);
			}

		} catch (Exception ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}
	
	public static void setAndHowCore(AndHowCore core) {

		try {

			Field ahInstanceField = AndHow.class.getDeclaredField("singleInstance");
			ahInstanceField.setAccessible(true);

			AndHow ahInstance = (AndHow)(ahInstanceField.get(null));

			if (ahInstance == null) {
				throw new RuntimeException("Cannot set a new core when AndHow is uninitialized");
			} else {
				Field ahCoreField = AndHow.class.getDeclaredField("core");
				ahCoreField.setAccessible(true);
				ahCoreField.set(ahInstance, core);
			}

		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}
	
	public static void forceRebuild(NamingStrategy namingStrategy,
			List<Loader> loaders, List<GroupProxy> registeredGroups) {

		try {
			AndHow ahInstance = getAndHowInstance();

			if (ahInstance == null) {
				//This is an uninitialized AndHow instance, initialize 'normally'
				Method build = AndHow.class.getDeclaredMethod("build", NamingStrategy.class, List.class, List.class);
				build.setAccessible(true);
				build.invoke(null, namingStrategy, loaders, registeredGroups);
			} else {
				//AndHow is already initialized, so just reassign the core

				AndHowCore core = new AndHowCore(namingStrategy, loaders, registeredGroups);
				AndHowNonProductionUtil.setAndHowCore(core);
			}
		} catch (IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}
	
	/**
	 * Creates a clone of a Properties object so it can be detached from System.
	 * 
	 * @param props
	 * @return 
	 */
	public static Properties clone(Properties props) {
		Properties newProps = new Properties();
		newProps.putAll(props);
		return newProps;
	}

}
