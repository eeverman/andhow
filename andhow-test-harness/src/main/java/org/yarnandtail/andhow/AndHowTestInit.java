package org.yarnandtail.andhow;

/**
 * Marks a class as an auto-discovered configuration provider for AndHow for testing.
 * 
 * If an <code>AndHowTestInit</code> implementation is on the classpath, 
 * it will be used in preference to <code>AndHowInit</code>.
 * This allows a test configuration to take precedence over the
 * production configuration simply by placing a <code>AndHowTestInit</code>
 * implementation on the test classpath.
 * 
 * Note that it is a fatal RuntimeException for there to be more than one each of
 * <code>AndHowTestInit</code> and <code>AndHowInit</code> implementations on the
 * classpath because startup would become ambiguous.  In general, never place a
 * non-abstract AndHowInit implementation in a reusable library since this would
 * prevent any user of that library from creating their own AndHowInit implementation.
 * 
 * @author ericeverman
 */
public interface AndHowTestInit extends AndHowInit {

}
