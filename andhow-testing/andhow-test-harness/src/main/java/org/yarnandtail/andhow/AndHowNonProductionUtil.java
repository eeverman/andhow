package org.yarnandtail.andhow;

import java.util.*;

import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.service.PropertyRegistrarLoader;
import org.yarnandtail.andhow.testutil.AndHowTestUtils;

/**
 * A collection of utilities for breaking the 'AndHow rules' during testing.
 *
 * The methods in this class are unsafe for using directly and are instead used
 * by JUnit annotations in the {@link org.yarnandtail.andhow.junit5} package.  They are also
 * used in the AndHowTestBase, but these classes are both
 * deprecated and will be removed at some point since they are only for JUnit 4 support,
 * which is aging...
 */
public final class AndHowNonProductionUtil {
	
	public static final String PERMISSION_MSG = 
			"There is some type of permissions/access error while trying to access and modify"
			+ "private fields during testing. "
			+ "Is there a security manager enforcing security during testing?";

	/**
	 * No instances.
	 */
	private AndHowNonProductionUtil() {}

	/**
	 * Gets the current AndHow instance without forcing its creation.
	 * 
	 * @return The AndHow instance or null if it is not initialized.
	 */
	public static AndHow getAndHowInstance() {
		return (AndHow) AndHowTestUtils.getAndHow();
	}
	
	public static AndHowCore getAndHowCore() {
		return (AndHowCore) AndHowTestUtils.getAndHowCore();
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
	 * @return The old core
	 */
	public static AndHowCore setAndHowCore(AndHowCore core) {
		return (AndHowCore) AndHowTestUtils.setAndHowCore(core);
	}
	
	/**
	 * Force AndHow to reload using the specified configuration.
	 * <p>
	 * If AndHow has not already initialized, it initialized normally using the
	 * passed configuration.  If AndHow has already initialized, a new
	 * {@code AndHowCore} is created using the passed configuration and it will
	 * replace the current core held by the singleton AndHow instance.
	 *
	 * @deprecated This method is unsafe and there is no reason to use it.
	 * Instead, use the <code>org.yarnandtail.andhow.junit5.Kill...</code>
	 * annotations.
	 * @param config The configuration to use, which must be non-null.  If you
	 * don't have specific needs for the configuration, you can use
	 * {@code AndHow.findConfiguration()}.
	 */
	@Deprecated
	public static void forceRebuild(AndHowConfiguration config) {

		AndHow ahInstance = getAndHowInstance();

		if (ahInstance == null) {

			//This is an uninitialized AndHow instance, initialize 'normally'
			AndHow.setConfig(config);
			AndHow.instance();

		} else {
			//AndHow is already initialized, so just reassign the core

			AndHowCore core = new AndHowCore(
					config.getNamingStrategy(),
					config.buildLoaders(),
					findGroups(config.getRegisteredGroups()));

			AndHowNonProductionUtil.setAndHowCore(core);

		}
	}

	/**
	 * Determine the 'Groups' (classes or interfaces containing AndHow Properties) that should be in
	 * scope of AndHow.
	 *
	 * AndHowConfiguration may pass a non-null list of groups to override automatic discovery, mostly
	 * for use during testing.  If the passed 'overrideGroups' is null, use auto-discovery.  If
	 * non-null, use the passed list, even if empty.
	 *
	 * @param overrideGroups A list of groups to use instead of the normal auto-discovery.
	 *   If overrideGroups is null, auto-discovery is used.  If non-null (even empty) overrideGroups is used.
	 * @return A list of groups that are in-scope for AndHow.
	 */
	private static List<GroupProxy> findGroups(List<GroupProxy> overrideGroups) {
		if (overrideGroups == null) {
			PropertyRegistrarLoader registrar = new PropertyRegistrarLoader();
			return registrar.getGroups();
		} else {
			return overrideGroups;
		}
	}
	
	/**
	 * Creates a clone of a Properties object so it can be detached from System.
	 *
	 * @deprecated Use <code>(Properties) Properties.clone()</code>
	 * @param props
	 * @return 
	 */
	@Deprecated
	public static Properties clone(Properties props) {
		return (Properties)props.clone();
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
	 *
	 * @deprecated This method is unsafe and there is no reason to use it.
	 * Instead, use the <code>org.yarnandtail.andhow.junit5.Kill...</code>
	 * annotations.
	 */
	@Deprecated
	public static void destroyAndHowCore() {
		if (getAndHowInstance() != null) {
			setAndHowCore(null);
		}
	}

}
