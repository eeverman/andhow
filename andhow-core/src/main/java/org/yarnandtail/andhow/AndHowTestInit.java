package org.yarnandtail.andhow;

/**
 * Marks a class as an auto-discoverable configuration provider for AndHow during testing.
 * <p>
 * <strong>Only place implementations of this interface on the test classpath.</strong>
 * <p>
 * If an {@code AndHowTestInit} implementation is on the classpath,
 * it will be used in preference to {@code AndHowInit} to allow a test configuration to take
 * precedence over the production configuration.  Implementation example, where config values
 * for a local test db are hardcoded into the configuration:
 * <p>
 * <pre>
 *     <code>
 * public class TestInitiation implements AndHowTestInit {
 *
 *  {@literal @}Override
 *  public AndHowConfiguration getConfiguration() {
 * 		return AndHow.findConfig()
 * 				.addFixedValue("com.bigcorp.config.DB_URL", "jdbc://local.db")
 * 				.addFixedValue("com.bigcorp.config.DB_PWD", "changeme");
 *  }
 *
 * }
 *     </code>
 * </pre>
 * <p>
 * It is a fatal RuntimeException for there to be more than one {@code AndHowTestInit} on the
 * classpath since this would make startup configuration ambiguous.
 *
 */
public interface AndHowTestInit extends AndHowInit {

}
