package org.yarnandtail.andhow;

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
public class AndHowTestUtil {
	
	public static AndHow getAndHowInstance() {
		return ReflectionTestUtil.getStaticFieldValue(AndHow.class, "singleInstance", AndHow.class);
	}
	
	public static AndHowCore getAndHowCore() {
		AndHow ahInstance = ReflectionTestUtil.getStaticFieldValue(AndHow.class, "singleInstance", AndHow.class);
		if (ahInstance == null) {
			return null;
		} else {
			return ReflectionTestUtil.getInstanceFieldValue(ahInstance, "core", AndHowCore.class);
		}
	}
	
	public static AndHowCore setAndHowCore(AndHowCore core) {

		AndHow ahInstance = ReflectionTestUtil.getStaticFieldValue(AndHow.class, "singleInstance", AndHow.class);

		if (ahInstance == null) {
			if (core == null) {
				return null; //no problem - its all null anyway
			} else {
				throw new RuntimeException("Cannot set a new core when AndHow is uninitialized");
			}
		} else {
			return ReflectionTestUtil.setInstanceFieldValue(ahInstance, "core", core, AndHowCore.class);
		}
	}

	/**
	 * Kill AndHow to ground state, as it would be prior to any initialization.
	 * This is different than how AndHow is 'killed' during application tests, which only kills
	 * the core.
	 */
	public static void killAndHowFully() {
		setAndHowCore(null);
		setAndHowInstance(null);
		setAndHowInitialization(null);
		setAndHowInProgressConfiguration(null);
		setAndHowInitializing(false);
	}
	
	public static AndHow setAndHowInstance(AndHow newInstance) {
		return ReflectionTestUtil.setStaticFieldValue(AndHow.class, "singleInstance", newInstance);
	}

	/**
	 * Force AndHow.Initialization (its debug info about initialization) to a new value.
	 *
	 * @param newInit
	 * @return The original value, which may have been null.
	 */
	public static AndHow.Initialization setAndHowInitialization(AndHow.Initialization newInit) {
		return ReflectionTestUtil.setStaticFieldValue(AndHow.class, "initialization", newInit);
	}

	/**
	 * Forces the AndHow inProcessConfig to a new value.
	 *
	 * This is the config being built prior to initialization.
	 * @return The original in-process configuration, which may have been null.
	 */
	public static AndHowConfiguration<? extends AndHowConfiguration>
			setAndHowInProgressConfiguration(AndHowConfiguration<? extends AndHowConfiguration> newConfig) {

		return ReflectionTestUtil.setStaticFieldValue(AndHow.class, "inProcessConfig", newConfig);
	}

	/**
	 * Force the AndHow 'initializing flag' - If true, AndHow is in the process of initialization.
	 *
	 * @return The original value.
	 */
	public static Boolean setAndHowInitializing(boolean newInitializingStatus) {
		return ReflectionTestUtil.setStaticFieldValue(AndHow.class, "initializing", Boolean.valueOf(newInitializingStatus));
	}

	public static void forceRebuild(AndHowConfiguration config) {

		AndHow ahInstance = getAndHowInstance();

		if (ahInstance == null) {

			//This is an uninitialized AndHow instance, initialize 'normally'
			AndHow.instance(config);

		} else {
			//AndHow is already initialized, so just reassign the core

			AndHowCore core = new AndHowCore(config.getNamingStrategy(), config.buildLoaders(), config.getRegisteredGroups());
			AndHowTestUtil.setAndHowCore(core);

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
