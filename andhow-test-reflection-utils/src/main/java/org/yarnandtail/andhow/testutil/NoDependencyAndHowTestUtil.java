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

	private static volatile Class<?> andHowClass;
	private static volatile Class<?> andHowCoreClass;
	private static volatile Class<?> andHowInitializationClass;
	private static volatile Class<?> andHowConfigurationClass;

	private static Class<?> getAndHowClass() {
		if (andHowClass == null) {
			try {
				andHowClass = Class.forName("org.yarnandtail.andhow.AndHow");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return andHowClass;
	}

	private static Class<?> getAndHowCoreClass() {
		if (andHowCoreClass == null) {
			try {
				andHowCoreClass = Class.forName("org.yarnandtail.andhow.internal.AndHowCore");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return andHowCoreClass;
	}

	private static Class<?> getAndHowInitializationClass() {
		if (andHowInitializationClass == null) {
			try {
				andHowInitializationClass = Class.forName("org.yarnandtail.andhow.AndHow$Initialization");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return andHowInitializationClass;
	}

	private static Class<?> getAndHowConfigurationClass() {
		if (andHowConfigurationClass == null) {
			try {
				andHowConfigurationClass = Class.forName("org.yarnandtail.andhow.AndHowConfiguration");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return andHowConfigurationClass;
	}

	/**
	 * Kill AndHow to ground state, as it would be prior to any initialization.
	 * This is different than how AndHow is 'killed' during application tests, which only kills
	 * the core.
	 */
	public static void killAndHowFully() {
		setAndHowCore(null);
		setAndHow(null);
		setAndHowInitialization(null);
		setAndHowInProcessConfiguration(null);
		setAndHowInitializing(false);
		setAndHowConfigLocator(null);
		getFindingConfig().set(false);
	}

	public static Object getAndHow() {
		return ReflectTestUtil.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());
	}

	public static Object setAndHow(Object newAndHowInstance) {

		if (newAndHowInstance != null && ! newAndHowInstance.getClass().equals(getAndHowClass())) {
			throw new RuntimeException("The AndHow instance must be of the type: " + getAndHowClass().getCanonicalName());
		}

		return ReflectTestUtil.setStaticFieldValue(getAndHowClass(), "singleInstance", newAndHowInstance);
	}
	
	public static Object getAndHowCore() {
		Object ahInstance = ReflectTestUtil.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());
		if (ahInstance == null) {
			return null;
		} else {
			return ReflectTestUtil.getInstanceFieldValue(ahInstance, "core", getAndHowCoreClass());
		}
	}
	
	public static <T> T setAndHowCore(T newCore) {

		if (newCore != null && ! newCore.getClass().equals(getAndHowCoreClass())) {
			throw new RuntimeException("The AndHowCore must be of the type: " + getAndHowCoreClass().getCanonicalName());
		}

		Object ahInstance = ReflectTestUtil.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());

		if (ahInstance == null) {
			if (newCore == null) {
				return null; //no problem - its all null anyway
			} else {
				throw new RuntimeException("Cannot set a new core when AndHow is uninitialized");
			}
		} else {
			return ReflectTestUtil.setInstanceFieldValue(ahInstance, "core", newCore, (Class<T>)(getAndHowCoreClass()));
		}
	}

	/**
	 * Force AndHow.Initialization (its debug info about initialization) to a new value.
	 *
	 * @param newInit
	 * @return The original value, which may have been null.
	 */
	public static <T> T setAndHowInitialization(T newInit) {

		if (newInit != null && ! newInit.getClass().equals(getAndHowInitializationClass())) {
			throw new RuntimeException(
					"The Initialization instance must be of the type: " + getAndHowInitializationClass().getCanonicalName());
		}

		return ReflectTestUtil.setStaticFieldValue(getAndHowClass(), "initialization", newInit);
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

		return ReflectTestUtil.setStaticFieldValue(getAndHowClass(), "configLocator", newLocator);
	}

	/**
	 * Forces the AndHow inProcessConfig to a new value.
	 *
	 * This is the config being built prior to initialization.
	 * @return The original in-process configuration, which may have been null.
	 */
	public static <T> T setAndHowInProcessConfiguration(T newConfig) {
		if (newConfig != null && ! newConfig.getClass().equals(getAndHowConfigurationClass())) {
			throw new RuntimeException(
					"The newConfig instance must be of the type: " + getAndHowConfigurationClass().getCanonicalName());
		}

		return ReflectTestUtil.setStaticFieldValue(getAndHowClass(), "inProcessConfig", newConfig);
	}

	/**
	 * Force the AndHow 'initializing flag' - If true, AndHow is in the process of initialization.
	 *
	 * @return The original value.
	 */
	public static Boolean setAndHowInitializing(Boolean newInitializingStatus) {
		return ReflectTestUtil.setStaticFieldValue(getAndHowClass(), "initializing", newInitializingStatus);
	}

	public static ThreadLocal<Boolean> getFindingConfig() {
		return ReflectTestUtil.getStaticFieldValue(getAndHowClass(), "findingConfig", ThreadLocal.class);
	}

}
