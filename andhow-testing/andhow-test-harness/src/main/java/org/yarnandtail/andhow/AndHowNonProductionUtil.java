package org.yarnandtail.andhow;

import java.lang.reflect.*;
import java.util.*;
import org.yarnandtail.andhow.internal.AndHowCore;

/**
 * A collection of utilities for breaking the 'AndHow rules' during testing.
 * 
 * @author ericeverman
 */
public class AndHowNonProductionUtil {
	
	public static final String PERMISSION_MSG = 
			"There is some type of permissions/access error while trying to access and modify"
			+ "private fields during testing. "
			+ "Is there a security manager enforcing security during testing?";
	
	/**
	 * Gets the current AndHow instance without forcing its creation.
	 * 
	 * @return The AndHow instance or null if it is not initialized.
	 */
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
	
	/**
	 * Sets a new {@code AndHowCore}
	 * 
	 * This inserts an entire new state into AndHow.  Inserting a <code>null</code> core
	 * puts AndHow into a reset state that is invalid during production, but
	 * can be useful during testing.  In this state, AndHow will allow itself
	 * to be reinitialized, which is not the intended operation during
	 * normal usage, but is useful during testing to test the application's
	 * behaviour with a variety of  configurations.
	 * 
	 * @param core A core instance to replace the current core with.  If AndHow
	 * is uninitialized, this will throw an error because it is assumed to be
	 * replacing a core.
	 */
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
	
	/**
	 * Force AndHow to reload using the specified configuration.
	 * <p>
	 * If AndHow has not already initialized, it initialized normally using the
	 * passed configuration.  If AndHow has already initialized, a new
	 * {@code AndHowCore} is created using the passed configuration and it will
	 * replace the current core held by the singleton AndHow instance.
	 * 
	 * @param config The configuration to use, which must be non-null.  If you
	 * don't have specific needs for the configuration, you can use
	 * {@code AndHow.findConfiguration()}.
	 */
	public static void forceRebuild(AndHowConfiguration config) {

		AndHow ahInstance = getAndHowInstance();

		if (ahInstance == null) {

			//This is an uninitialized AndHow instance, initialize 'normally'
			AndHow.setConfig(config);
			AndHow.instance();

		} else {
			//AndHow is already initialized, so just reassign the core

			AndHowCore core = new AndHowCore(config.getNamingStrategy(), config.buildLoaders(), config.getRegisteredGroups());
			AndHowNonProductionUtil.setAndHowCore(core);

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
	
	/**
	 * Forces the {@code AndHowCore} to be null.
	 * <p>
	 * If AndHow has not been initialized, it will just remain in the uninitialized
	 * state.  If AndHow has been initialized, the core state will be set to null,
	 * causing AndHow to be in a reset state that is invalid during production, but
	 * can be useful during testing.  In this state, AndHow will allow itself
	 * to be reinitialized, which is not the intended operation during
	 * normal usage, but is useful during testing to test the application's
	 * behaviour with a variety of  configurations.
	 */
	public static void destroyAndHowCore() {
		if (getAndHowInstance() != null) {
			setAndHowCore(null);
		}
	}

}
