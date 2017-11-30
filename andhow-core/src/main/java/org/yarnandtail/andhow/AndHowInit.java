package org.yarnandtail.andhow;

/**
 * Marks a class as an auto-discovered configuration provider for AndHow.
 * 
 * A single instance of this interface on the classpath, if it exists, will be
 * auto-discovered and used to initiate AndHow at the time of the first attempt
 * to access a property.
 * 
 * Alternatively, if AndHow is explicitly initiated, the configuration from a
 * AndHowInit instance can be used for that configuration.  This provides the
 * safety of an explicit configuration if needed, the convenience of an auto-
 * discovered configuration if wanted, and shared configuration code between
 * those two alternatives.
 * 
 * Note that it is a fatal RuntimeException for there to be more than a single
 * AndHowInit implementation on the classpath because startup would become
 * ambiguous*.  In general, never place a non-abstract AndHowInit implementation
 * in a reusable library since this would prevent any user of that library
 * from creating their own AndHowInit implementation.
 * 
 * *In addition to a single <code>AndHowInit</code> implementation, a single
 * <code>AndHowTestInit</code> is also allowed.  If an <code>AndHowTestInit</code>
 * implementation is present, it will be used in preference to the <code>AndHowInit</code>.
 * This allows a test configuration to take precedence over the
 * production configuration simply by placing a <code>AndHowTestInit</code>
 * implementation on the test classpath.
 * 
 * @author ericeverman
 */
public interface AndHowInit {
	AndHowConfiguration getConfiguration();
}
