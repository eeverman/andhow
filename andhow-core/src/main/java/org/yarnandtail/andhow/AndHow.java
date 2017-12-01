package org.yarnandtail.andhow;

import java.lang.reflect.Field;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.service.InitLoader;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Central AndHow singleton class.
 * 
 * This class is not directly constructed.  The primary way to configure an
 * instance is indirectly by simply creating a subclass of org.yarnandtail.andhow.AndHowInit.
 * At startup, AndHow finds your AndHowInit implementation and uses it to configure
 * a single instance.
 * 
 * Alternatively, you can directly create a AndHowConfiguration instance and
 * pass it to AndHow.instance().  StdConfig can be used to do that like this:
 * <code>
 * AndHow.instance( StdConfig.instance().addCmdLineArgs(args) );
 * </code>
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

	/**
	 * Private constructor - Use the AndHowBuilder to build instances.
	 *
	 * @param naming
	 * @param loaders
	 * @param registeredGroups
	 * @param cmdLineArgs
	 * @param forcedValues
	 * @param defaultValues
	 * @throws AppFatalException
	 */
	private AndHow(NamingStrategy naming, List<Loader> loaders,
			List<GroupProxy> registeredGroups)
			throws AppFatalException {

		synchronized (LOCK) {
			core = new AndHowCore(naming, loaders, registeredGroups);
		}
	}

	private AndHow(AndHowConfiguration config) throws AppFatalException {
		synchronized (LOCK) {
			core = new AndHowCore(
					config.getNamingStrategy(),
					config.buildLoaders(),
					config.getRegisteredGroups());
		}
	}

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
	 * Used internally only when it is known that the AndHow instance or its
	 * core is null.
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
							throwFatal(ex);
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
	//ConstructionDefinition Interface
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
	public List<Property<?>> getPropertiesForGroup(GroupProxy group) {
		return core.getPropertiesForGroup(group);
	}

	@Override
	public Property<?> getProperty(String name) {
		return core.getProperty(name);
	}

	@Override
	public List<GroupProxy> getPropertyGroups() {
		return core.getPropertyGroups();
	}
	
	@Override
	public boolean containsUserGroups() {
		return core.containsUserGroups();
	}

	@Override
	public List<Property<?>> getProperties() {
		return core.getProperties();
	}

	@Override
	public List<ExportGroup> getExportGroups() {
		return core.getExportGroups();
	}

	@Override
	public NamingStrategy getNamingStrategy() {
		return core.getNamingStrategy();
	}

	private static void throwFatal(Throwable throwable) {
		throwFatal("", throwable);
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
