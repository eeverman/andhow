package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileBeforeAllTestsExt;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(ConfigFromFileBeforeAllTestsExt.class)
public @interface ConfigFromFileBeforeAllTests {

	/**
	 * The classpath to a properties file containing the {@code property_name : value} entries
	 * to configure AndHow Property values before the run of this test class.
	 * <p>
	 * If the path starts with a '{@code / }', it is interpreted as an absolute classpath.
	 * If the path does not start with a '{@code / }', it is interpreted as relative to the current
	 * test class.  Some examples:
	 * <ul>
	 *   <li>/myFile.props - The file 'myFile.props' expected at the root of the classpath</li>
	 *   <li>/sub/myFile.props - The file 'myFile.props' expected in the 'sub' package or directory of the classpath</li>
	 *   <li>myFile.props (for a test in the org.people package) - The file 'myFile.props' expected in the org.people package on the classpath</li>
	 *   <li>sub/myFile.props (for a test in the org.people package) - The file 'myFile.props' expected in the org.people.sub package on the classpath</li>
	 * </ul>
	 * <p>
	 * If specifying only the configuration properties file, the shortened annotation syntax can be used:<br>
	 * {@code @ConfigFromFileBeforeAllTests("myFile.props")}<br>
	 * Otherwise the full syntax must be used:<br>
	 * {@code @ConfigFromFileBeforeAllTests(value = "myFile.props"), includeClasses = {Conf1.class, Conf2.class}}<br>
	 * <p>
	 * @return A string containing the path to a properties file.
	 */
	String value();

	/**
	 * Optional array of classes that AndHow will scan for AndHow Properties.
	 * <p>
	 * If testing a single class or subset of classes that use AndHow Properties within a larger
	 * project, you can limit the scope of which Properties AndHow will 'see' to just those needed
	 * for the test.  All Properties in the listed classes and nested innerclasses will be scanned
	 * for AndHow Properties, but no others.  This reduces the size of the properties files
	 * required for configuration and limits the effects of refactors on Property names.
	 * <p>
	 * If unspecified, all AndHow configuration properties on the classpath
	 * will be discovered, configured and validated.  Thus, any configuration Properties that
	 * are required to be non-null will need to have values provided via the configured properties
	 * file or in some other way.
	 * <p>
	 * Typical usage looks like this:<br>
	 * {@code @ConfigFromFileBeforeAllTests(value = "myFile.props"), includeClasses = {Conf1.class, Conf2.class}}
	 * <p>
	 * @return An array of classes that should be scanned (along with their nested innerclasses) for
	 * AndHow Properties.
	 */
	Class<?>[] includeClasses() default {};
}
