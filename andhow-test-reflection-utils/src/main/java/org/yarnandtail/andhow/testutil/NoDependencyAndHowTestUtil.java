package org.yarnandtail.andhow.testutil;

import java.util.function.UnaryOperator;

/**
 * A testing utility class that breaks major rules of AndHow.
 * <p>
 * <em>THIS CLASS DOES DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em>
 * <p>
 * This util class is intentionally placed in the test directory because it is
 * not intended to be distributed.
 * <p>
 * For utilities to test your application code with AndHow, see the AndHow
 * Test Harness module.  For examples using the test harness, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 * 
 * @author ericeverman
 */
public class NoDependencyAndHowTestUtil {

	/**
	 * Kill AndHow to ground state, as it would be prior to any initialization.
	 * This is different than how AndHow is 'killed' during application tests, which only kills
	 * the core.
	 */
	public static void killAndHowFully() {
		setAndHowCore(null);
		setAndHow(null);
		setAndHowInitialization(null);
		setAndHowInProcessConfig(null);
		setAndHowInitializing(false);
		setAndHowConfigLocator(null);
		getFindingConfig().set(false);
	}

	public static Object getAndHow() {
		return ReflectionUtils.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());
	}

	public static Object setAndHow(Object newAndHow) {

		if (newAndHow != null && ! getAndHowClass().isAssignableFrom(newAndHow.getClass())) {
			throw new RuntimeException("newAndHow must be of the type: " + getAndHowClass().getCanonicalName());
		}

		return ReflectionUtils.setStaticFieldValue(getAndHowClass(), "singleInstance", newAndHow);
	}
	
	public static Object getAndHowCore() {
		Object ahInstance = ReflectionUtils.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());
		if (ahInstance == null) {
			return null;
		} else {
			return ReflectionUtils.getInstanceFieldValue(ahInstance, "core", getAndHowCoreClass());
		}
	}
	
	public static <T> T setAndHowCore(T newCore) {

		if (newCore != null && ! getAndHowCoreClass().isAssignableFrom(newCore.getClass())) {
			throw new RuntimeException("newCore must be of the type: " + getAndHowCoreClass().getCanonicalName());
		}

		Object ahInstance = ReflectionUtils.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());

		if (ahInstance == null) {
			if (newCore == null) {
				return null; //no problem - its all null anyway
			} else {
				throw new RuntimeException("Cannot set a new core when AndHow is uninitialized");
			}
		} else {
			return ReflectionUtils.setInstanceFieldValue(ahInstance, "core", newCore, (Class<T>)(getAndHowCoreClass()));
		}
	}

	/**
	 * Force AndHow.Initialization (its debug info about initialization) to a new value.
	 *
	 * @param newInit
	 * @return The original value, which may have been null.
	 */
	public static <T> T setAndHowInitialization(T newInit) {

		if (newInit != null && ! getAndHowInitializationClass().isAssignableFrom(newInit.getClass())) {
			throw new RuntimeException(
					"The newInit must be of the type: " + getAndHowInitializationClass().getCanonicalName());
		}

		return ReflectionUtils.setStaticFieldValue(getAndHowClass(), "initialization", newInit);
	}

	/**
	 * Set a locator to find AndHowConfiguration.
	 *
	 * The locator is used in AndHow.findConfig().  If no config exists, the normal path is
	 * for AndHow to call {@code AndHowUtil.findConfiguration(c)}, however, if a locator
	 * is set to nonnull, it will be used instead.
	 * <p>
	 * The locator takes a default Configuration to return if a configuration cannot
	 * be found otherwise.  See <code></code>org.yarnandtail.AndHow#findConfig()</code> for details.
	 * <p>
	 * Example setting to a custom locator:
	 * <pre>{@code setAndHowConfigLocator(c -> return MyConfig); }</pre>
	 * Example setting back to null:
	 * <pre>{@code setAndHowConfigLocator(null); }</pre>
	 * <p>
	 * If this is set to some custom value for testing, it must be set back to
	 * null or future initialization will use the custom locator.
	 *
	 * @return The original locator, which may have been null.
	 */
	public static <T> UnaryOperator<T> setAndHowConfigLocator(UnaryOperator<T> newLocator) {
		return ReflectionUtils.setStaticFieldValue(
				getAndHowClass(), "configLocator", newLocator);
	}

	/**
	 * Forces the AndHow inProcessConfig to a new value.
	 *
	 * This is the config being built prior to initialization.
	 * @return The original in-process configuration, which may have been null.
	 */
	public static <T> T setAndHowInProcessConfig(T newConfig) {
		if (newConfig != null && ! getAndHowConfigurationClass().isAssignableFrom(newConfig.getClass())) {
			throw new RuntimeException(
					"The newConfig instance must be of the type: " + getAndHowConfigurationClass().getCanonicalName());
		}

		return ReflectionUtils.setStaticFieldValue(getAndHowClass(), "inProcessConfig", newConfig);
	}

	/**
	 * Force the AndHow 'initializing flag' - If true, AndHow is in the process of initialization.
	 *
	 * @return The original value.
	 */
	public static Boolean setAndHowInitializing(Boolean newInitializingStatus) {
		 if (newInitializingStatus == null) {
			 throw new RuntimeException("Cannot set the initializating status to null");
		 }
		return ReflectionUtils.setStaticFieldValue(getAndHowClass(), "initializing", newInitializingStatus);
	}

	public static ThreadLocal<Boolean> getFindingConfig() {
		return ReflectionUtils.getStaticFieldValue(getAndHowClass(), "findingConfig", ThreadLocal.class);
	}

	/**
	 * Performs Class.forName, but only throws a RuntimeException so no exception needs to be handled.
	 *
	 * @param className The full class name, as would be needed for Class.forName
	 * @return The Class<?> for that name.
	 */
	public static Class<?> getClassByName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	//
	//Basic access to AndHow classes via reflection so there is no dependency

	private static Class<?> getAndHowClass() {
		return getClassByName("org.yarnandtail.andhow.AndHow");
	}

	private static Class<?> getAndHowCoreClass() {
		return getClassByName("org.yarnandtail.andhow.internal.AndHowCore");
	}

	private static Class<?> getAndHowInitializationClass() {
		return getClassByName("org.yarnandtail.andhow.AndHow$Initialization");
	}

	private static Class<?> getAndHowConfigurationClass() {
		return getClassByName("org.yarnandtail.andhow.AndHowConfiguration");
	}

}
