package org.yarnandtail.andhow.testutil;

import java.util.function.UnaryOperator;

/**
 * A testing utility class that allows you to 'break the rules' of AndHow.
 *
 * <p><em>THIS CLASS CAN DO DANGEROUS THINGS THAT SHOULD NEVER BE DONE NEAR APPLICATION CODE</em></p>
 *
 * In production, AndHow is singleton that is only initialized once.  Application code depends
 * on that behaviour because it assumes that its basic configuration does not change.
 * If it seems like you need to use the utilities here in production - STOP - you are doing
 * it wrong.  See the AndHow documentation and usage examples instead.
 * <p>
 * The methods in this class are generally not needed (directly) for testing.  They
 * are used by JUnit extensions that use them in a controlled and predictable way so that
 * your application is configured correctly during testing.  Many of methods in this class are
 * used in the testing of the AndHow framework itself, thus, it has no dependency on AndHow
 * and many methods take and return 'Object' types rather than specific AndHow classes.
 * <p>
 * For examples using JUnit extensions and annotations, see the AndHow
 * Sample Usages module and its many sub-modules, which are stand-alone simulated
 * projects complete with testing.
 */
public final class AndHowTestUtils {

	/**
	 * No instances.
	 */
	private AndHowTestUtils() {}

	/**
	 * Kill AndHow to ground state, as it would be prior to any initialization.
	 *
	 * <em>Don't use this method to test application code - It will break your tests!</em>
	 * This method is only for testing the AndHow framework itself, not application code.
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
	
	/**
	 * Invoke AndHow.instance(), returning an AndHow instance and forcing initiation
	 * if AndHow is not already initiated.
	 * 
	 * See AndHow.instance().
	 *
	 * Since this module has no dependency on AndHow, the return type of Object.
	 *
	 * @return The current AndHow instance, or null if it is uninitiated.
	 */
	public static Object invokeAndHowInstance() {
		return ReflectionTestUtils.invokeStaticMethod(getAndHowClass(), "instance", null, new Class<?>[] {});
	}

	/**
	 * The current AndHow singleton instance, without forcing its creation.
	 *
	 * Since this module has no dependency on AndHow, the return type of Object.
	 *
	 * @return The current AndHow instance, or null if it is uninitiated.
	 */
	public static Object getAndHow() {
		return ReflectionTestUtils.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());
	}

	/**
	 * Set the AndHow singleton instance and return the previous one.
	 *
	 * <em>Don't use this method to test application code - It will break your tests!</em>
	 * This method is only for testing the AndHow framework itself, not application code.
	 * <p>
	 * Since this module has no dependency on AndHow, the return and arg types are 'Object'.
	 *
	 * @param newAndHow An AndHow instance to use as the current singleton instance,
	 *                  which may be null, but must be of type AndHow.
	 * @param <T> implicitly passed type of the instance, which must be type AndHow.
	 * @return The previous AndHow instance, which may be null.  If null is passed, the return
	 * 					value will need to be cast.
	 */
	public static <T> T setAndHow(T newAndHow) {

		if (newAndHow != null && ! getAndHowClass().isAssignableFrom(newAndHow.getClass())) {
			throw new RuntimeException("newAndHow must be of the type: " + getAndHowClass().getCanonicalName());
		}

		return ReflectionTestUtils.setStaticFieldValue(getAndHowClass(), "singleInstance", newAndHow);
	}

	/**
	 * The current AndHow singleton's 'Core' object.
	 *
	 * Since this module has no dependency on AndHow, the return type is 'Object'.
	 *
	 * @return The AndHow 'Core', which may be null.
	 */
	public static Object getAndHowCore() {
		Object ahInstance = ReflectionTestUtils.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());
		if (ahInstance == null) {
			return null;
		} else {
			return ReflectionTestUtils.getInstanceFieldValue(ahInstance, "core", getAndHowCoreClass());
		}
	}

	/**
	 * Set the AndHow 'Core'.
	 *
	 * This method is relatively safe for use in application testing and is used by the Junit
	 * extensions and annotations to set configurations for individual tests.
	 *
	 * Note:  This method will fail is AndHow is uninitialized.
	 **
	 * @param newCore The new core to assign to the AndHow singleton which may be null but must
	 *                be of type AndHowCore.
	 * @param <T> implicitly passed type of the core which must be type AndHowCore.
	 * @return The old core, which may be null.  If passing a null core, calling code will need
	 * to cast the result.
	 */
	public static <T> T setAndHowCore(T newCore) {

		if (newCore != null && ! getAndHowCoreClass().isAssignableFrom(newCore.getClass())) {
			throw new RuntimeException("newCore must be of the type: " + getAndHowCoreClass().getCanonicalName());
		}

		Object ahInstance = ReflectionTestUtils.getStaticFieldValue(getAndHowClass(), "singleInstance", getAndHowClass());

		if (ahInstance == null) {
			if (newCore == null) {
				return null; //no problem - its all null anyway
			} else {
				throw new RuntimeException("Cannot set a new core when AndHow is uninitialized");
			}
		} else {
			return ReflectionTestUtils.setInstanceFieldValue(
					ahInstance, "core", newCore, (Class<T>)(getAndHowCoreClass()));
		}
	}

	/**
	 * Force AndHow.Initialization (its debug info about initialization) to a new value.
	 *
	 * @param newInit The new initialization to assign which may be null, but must be of type
	 *                AndHow.Initialization.
	 * @param <T> implicitly passed type of the newInit which must be type AndHow.Initialization.
	 * @return The original value, which may have been null.  If null is passed, the return value
	 * 						will need to be cast.
	 */
	public static <T> T setAndHowInitialization(T newInit) {

		if (newInit != null && ! getAndHowInitializationClass().isAssignableFrom(newInit.getClass())) {
			throw new RuntimeException(
					"The newInit must be of the type: " + getAndHowInitializationClass().getCanonicalName());
		}

		return ReflectionTestUtils.setStaticFieldValue(getAndHowClass(), "initialization", newInit);
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
	 * @param newLocator The new locator to use, which may be null, but must be of type
	 *                {@code UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>>}
	 * @param <T> implicit type of the newLocator, which must be
	 * 								{@code UnaryOperator<AndHowConfiguration<? extends AndHowConfiguration>>}
	 * @return The original locator, which may have been null.  If null was passed,
	 * 								the return value will need to be cast.
	 */
	public static <T> UnaryOperator<T> setAndHowConfigLocator(UnaryOperator<T> newLocator) {
		return ReflectionTestUtils.setStaticFieldValue(
				getAndHowClass(), "configLocator", newLocator);
	}

	/**
	 * Forces the AndHow inProcessConfig to a new value.
	 *
	 * This is the config being built prior to initialization.
	 *
	 * @param newConfig The new configuration to have in-process, which may be null, but
	 *                  must be of type {@code AndHowConfiguration<? extends AndHowConfiguration>}
	 * @param <T> The implicit type, which must be of type
	 * 									{@code AndHowConfiguration<? extends AndHowConfiguration>}
	 * @return The original in-process configuration, which may have been null.
	 * 									If null is passed, the return value will need to be cast.
	 */
	public static <T> T setAndHowInProcessConfig(T newConfig) {
		if (newConfig != null && ! getAndHowConfigurationClass().isAssignableFrom(newConfig.getClass())) {
			throw new RuntimeException(
					"The newConfig instance must be of the type: " + getAndHowConfigurationClass().getCanonicalName());
		}

		return ReflectionTestUtils.setStaticFieldValue(getAndHowClass(), "inProcessConfig", newConfig);
	}

	/**
	 * Force the AndHow 'initializing flag' - If true, AndHow is in the process of initialization.
	 *
	 * @param newInitializingStatus non-null boolean value.
	 * @return The original value.
	 */
	public static Boolean setAndHowInitializing(Boolean newInitializingStatus) {
		 if (newInitializingStatus == null) {
			 throw new RuntimeException("Cannot set the initializating status to null");
		 }
		return ReflectionTestUtils.setStaticFieldValue(getAndHowClass(), "initializing", newInitializingStatus);
	}

	/**
	 * The AndHow 'findingConfig' flag, which indicates that AndHow is search for its configuration
	 * and blocks reentrent loops.
	 *
	 * Setting and inspecting this flag is only needed for testing the AndHow framework itself.
	 * It is not needed for testing application code.
	 *
	 * @return The finding Config flag value.
	 */
	public static ThreadLocal<Boolean> getFindingConfig() {
		return ReflectionTestUtils.getStaticFieldValue(
				getAndHowClass(), "findingConfig", ThreadLocal.class);
	}

	//
	//Basic access to AndHow classes via reflection so there is no dependency

	/**
	 * Get the Class<?> of AndHow - needed since this module has no AndHow dependency.
	 * @return Class<?> of AndHow
	 */
	public static Class<?> getAndHowClass() {
		return ReflectionTestUtils.getClassByName("org.yarnandtail.andhow.AndHow");
	}

	/**
	 * Get the Class<?> of AndHowCore - needed since this module has no AndHow dependency.
	 * @return Class<?> of AndHowCore
	 */
	public static Class<?> getAndHowCoreClass() {
		return ReflectionTestUtils.getClassByName("org.yarnandtail.andhow.internal.AndHowCore");
	}

	/**
	 * Get the Class<?> of AndHow.Initialization - needed since this module has no AndHow dependency.
	 * @return Class<?> of AndHow.Initialization
	 */
	private static Class<?> getAndHowInitializationClass() {
		return ReflectionTestUtils.getClassByName("org.yarnandtail.andhow.AndHow$Initialization");
	}

	/**
	 * Get the Class<?> of AndHowConfiguration - needed since this module has no AndHow dependency.
	 * @return Class<?> of AndHowConfiguration
	 */
	public static Class<?> getAndHowConfigurationClass() {
		return ReflectionTestUtils.getClassByName("org.yarnandtail.andhow.AndHowConfiguration");
	}

}
