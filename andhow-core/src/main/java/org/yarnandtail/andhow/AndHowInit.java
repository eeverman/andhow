package org.yarnandtail.andhow;

/**
 * Marks a class as an auto-discoverable configuration provider for AndHow in production.
 * 
 * A single implementation of {@code AndHowInit} on the classpath, if it exists, will be
 * auto-discovered and used to initiate AndHow at startup.  Usage of this interfact is optional
 * and not normally required.  Times where your application would need to implement this interface
 * are:
 * <p><ul>
 * <li>When you need to change the order precedence of the loaders such as having JNDI properties
 * take precedence over environment variables vs System Properties.  The default order of precedence
 * is documented in each loader, see {@link org.yarnandtail.andhow.load.std.StdEnvVarLoader}.
 * <li>When you want to add a loader, such as a {@link org.yarnandtail.andhow.load.PropFileOnFilesystemLoader}
 * <li>Or to customize the default property file name used by the {@link org.yarnandtail.andhow.load.std.StdPropFileOnClasspathLoader}
 * </ul><p>
 * All of those things can be done at the entry point of your application (ie in the main method),
 * but using an implementation of this interface ensures that the configuration is found regardless
 * of the entry point.
 * <p>
 * It is a fatal RuntimeException for there to be more than one {@code AndHowInit} on the
 * classpath since this would make startup configuration ambiguous.
 * In general, never distribute a {@code AndHowInit} implementation in a reusable library since this
 * would prevent a user of that library from creating their own {@code AndHowInit} for startup
 * configuration of an application.
 * <p>
 * A single {@code AndHowTestInit} implementation is also allowed.  See {@link AndHowTestInit}.
 *
 */
public interface AndHowInit {
	AndHowConfiguration getConfiguration();
}
