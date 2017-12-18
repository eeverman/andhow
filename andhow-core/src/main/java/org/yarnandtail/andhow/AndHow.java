package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Central AndHow singleton class.
 * 
 * This class is not directly constructed.  The primary way to configure an
 * instance is indirectly by creating a subclass of org.yarnandtail.andhow.AndHowInit.
 * At startup, AndHow discovers your AndHowInit implementation and uses it to configure
 * a single instance.
 * 
 * For cases where the application needs to accept command line arguments or
 * augment the configuration with fixed values, the configuration can have those
 * parameters added like this:
 * <pre>{@code
 * AndHow.findConfig().setCmdLineArgs(myCmdLineArgs).build();
 * }</pre>
 * <code>findConfig()</code> finds the <code>AndHowConfiguration</code> that
 * would be used if <code>AndHow.instance()</code> was called.  <code>build()</code>
 * then causes the AndHow instance to be built with that modified configuration.
 * The code above (or any method of AndHow initiation) can only be executed once
 * during the life of the application.
 * 
 * @author eeverman
 */
public class AndHow implements StaticPropertyConfiguration, ValidatedValues {

	//
	//A few app-wide constants
	public static final String ANDHOW_INLINE_NAME = "AndHow";
	public static final String ANDHOW_NAME = "AndHow!";
	public static final String ANDHOW_URL = "https://github.com/eeverman/andhow";
	public static final String ANDHOW_TAG_LINE = "strong.simple.valid.AppConfiguration";

	private static volatile AndHow singleInstance;
	private static final Object LOCK = new Object();

	private volatile AndHowCore core;

	private AndHow(AndHowConfiguration config) throws AppFatalException {
		synchronized (LOCK) {
			core = new AndHowCore(
					config.getNamingStrategy(),
					config.buildLoaders(),
					config.getRegisteredGroups());
		}
	}
	
	/**
	 * Finds and creates a new instance of the <code>AndHowConfiguration</code>
	 * that would be used if <code>AndHow.instance()</code> was called.
	 * 
	 * <strong>Note:</strong> If <code>AndHow.instance()</code> is later called, a new
	 * <code>AndHowConfiguration</code> will be returned, not this same instance.
	 * <p>
	 * This method provides a way to add command line parameters or fixed values
	 * to the existing configuration and then immediately initiate <code>AndHow</code>.
	 * <p>
	 * Example usage:
	 * <pre>{@code
	 * AndHow.findConfig().setCmdLineArgs(myCmdLineArgs).build();
	 * }</pre>
	 * The call to <code>build()</code> at the end of that command string is
	 * just a convenience method to call <code>AndHow.instance(thisConfiguration);</code>
	 * 
	 * @return 
	 */
	public static AndHowConfiguration<? extends AndHowConfiguration> findConfig() {
		return AndHowUtil.findConfiguration(StdConfig.instance());
	}

	/**
	 * Returns the current AndHow instance.  If there is no instance, one is created
	 * using auto-discovered configuration.
	 * 
	 * @return
	 * @throws AppFatalException 
	 */
	public static AndHow instance() throws AppFatalException {
		if (singleInstance != null && singleInstance.core != null) {
			return singleInstance;
		} else {
			synchronized (LOCK) {
				if (singleInstance == null || singleInstance.core == null) {
					return instance(AndHowUtil.findConfiguration(StdConfig.instance()));
				} else {
					return singleInstance;
				}
			}

		}
	}

	/**
	 * Builds a new AndHow instance using the specified configuration ONLY IF
	 * there is no existing AndHow instance.
	 * 
	 * A fatal RuntimeException will be thrown if there is an existing AndHow
	 * instance.  This method does not normally need to be used to initiate AndHow.
	 * See the AndHow class docs for typical configuration examples.
	 *
	 * @param config
	 * @return
	 * @throws AppFatalException
	 */
	public static AndHow instance(AndHowConfiguration config) throws AppFatalException {
		synchronized (LOCK) {

			if (singleInstance != null && singleInstance.core != null) {
				throw new AppFatalException("Cannot request construction of new "
						+ "AndHow instance when there is an existing instance.");
			} else {

				if (singleInstance == null) {

					singleInstance = new AndHow(config);

				} else if (singleInstance.core == null) {

					/*	This is a concession for testing.  During testing the
					core is deleted to force AndHow to reload.  Its really an
					invalid state (instance and core should be null/non-null
					together, but its handled here to simplify testing.  */
					try {

						AndHowCore newCore = new AndHowCore(
								config.getNamingStrategy(),
								config.buildLoaders(),
								config.getRegisteredGroups());
						Field coreField = AndHow.class.getDeclaredField("core");
						coreField.setAccessible(true);
						coreField.set(singleInstance, newCore);

					} catch (Exception ex) {
						if (ex instanceof AppFatalException) {
							throw (AppFatalException) ex;
						} else {
							throwFatal("", ex);
						}
					}

				}
				return singleInstance;

			}

		}	//end sync
	}

	/**
	 * Determine if AndHow is initialized or not w/out forcing AndHow to load.
	 *
	 * @return
	 */
	public static boolean isInitialize() {
		return singleInstance != null && singleInstance.core != null;
	}

	//
	//PropertyValues Interface
	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return core.isExplicitlySet(prop);
	}

	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		return core.getExplicitValue(prop);
	}

	@Override
	public <T> T getValue(Property<T> prop) {
		return core.getValue(prop);
	}

	//
	//StaticPropertyConfiguration Interface
	@Override
	public List<EffectiveName> getAliases(Property<?> property) {
		return core.getAliases(property);
	}

	@Override
	public String getCanonicalName(Property<?> prop) {
		return core.getCanonicalName(prop);
	}

	@Override
	public GroupProxy getGroupForProperty(Property<?> prop) {
		return core.getGroupForProperty(prop);
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return core.getNamingStrategy();
	}
	
	/**
	 * Builds and throws an AppFatalException. The stack trace is edited to
	 * remove 2 method calls, which should put the stacktrace at the user code
	 * of the build.
	 *
	 * @param message
	 */
	private static void throwFatal(String message, Throwable throwable) {

		if (throwable instanceof AppFatalException) {
			throw (AppFatalException) throwable;
		} else {
			AppFatalException afe = new AppFatalException(message, throwable);
			StackTraceElement[] stes = afe.getStackTrace();
			stes = Arrays.copyOfRange(stes, 2, stes.length);
			afe.setStackTrace(stes);
			throw afe;
		}
	}

}
