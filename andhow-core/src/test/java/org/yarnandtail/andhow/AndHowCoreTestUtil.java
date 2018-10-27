package org.yarnandtail.andhow;

import java.lang.reflect.*;
import java.util.*;
import org.yarnandtail.andhow.internal.AndHowCore;

/**
 * A testing utility class that breaks major rules of AndHow.
 * <p>
 * This util class is intentionally placed in the test directory because it is
 * never intended to be distributed, not even for use by others in their testing.
 * <p>
 * <em>THIS CLASS DOES DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em>
 * <p>
 * For utilities to test your application code with AndHow, see the AndHow
 * Test Harness module.  For examples using the test harness, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 * 
 * @author ericeverman
 */
public class AndHowCoreTestUtil {
	
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
				if (core == null) {
					//no problem - its all null anyway
				} else {
					throw new RuntimeException("Cannot set a new core when AndHow is uninitialized");
				}
			} else {
				Field ahCoreField = AndHow.class.getDeclaredField("core");
				ahCoreField.setAccessible(true);
				ahCoreField.set(ahInstance, core);
			}

		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
	}
	
	public static AndHow setAndHowInstance(AndHow newInstance) {

		try {

			Field ahInstanceField = AndHow.class.getDeclaredField("singleInstance");
			ahInstanceField.setAccessible(true);

			AndHow oldInstance = (AndHow)(ahInstanceField.get(null));
			ahInstanceField.set(null, newInstance);
			
			return oldInstance;

		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new RuntimeException(PERMISSION_MSG, ex);
		}
		
	}
	
	public static void forceRebuild(AndHowConfiguration config) {

		AndHow ahInstance = getAndHowInstance();

		if (ahInstance == null) {

			//This is an uninitialized AndHow instance, initialize 'normally'
			AndHow.build(config);

		} else {
			//AndHow is already initialized, so just reassign the core

			AndHowCore core = new AndHowCore(config.getNamingStrategy(), config.buildLoaders(), config.getRegisteredGroups());
			AndHowCoreTestUtil.setAndHowCore(core);

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
	
	public static void destroyAndHow() {
		setAndHowInstance(null);
	}

}
