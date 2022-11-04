package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileBeforeEachTestExt;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
@ExtendWith(ConfigFromFileBeforeEachTestExt.class)
public @interface ConfigFromFileBeforeEachTest {
	String filePath();

	/**
	 * Optional array of classes that AndHow will limit its inspection to for finding
	 * AndHow Properties.
	 * <p>
	 * If you are testing a single class or subset of classes that require
	 * configuration within a large project with lots of configuration,
	 * it may make sense to limit AndHow to just the smaller set of classes that
	 * need configuration for this test or set of tests.
	 * <p>
	 * If unspecified, all AndHow configuration properties on the classpath
	 * will be discovered, configured and validated.  For a project with lots of
	 * configuration, that may require lots of configuration unrelated to the test
	 * in the configured properties file.
	 *
	 * @return
	 */
	Class<?>[] classesInScope() default {};
}
