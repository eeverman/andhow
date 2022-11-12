package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.KillAndHowBeforeAllTestsExt;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on a test class to reset AndHow to its
 * unconfigured state before the first test runs.  When all tests in the class
 * are done, the original AndHow configured state is restored, if any.
 * <p>
 * Example usage:
 * <pre>{@code
 * @KillAndHowBeforeAllTests
 * public class MyJunit5Test {
 *
 *   @BeforeAll
 *   public static void configAndHowForAllTests(){
 * 		AndHow.findConfig()
 * 				.setClasspathPropFilePath("/test1-prop-file.properties");
 *
 * 			...
 * 		  	An AndHow instance used only for the tests in this class will now load
 * 		  	property values from the test1-prop-file.properties file prior to all
 * 		  	of the tests in this class.  The tests that will all share the same
 * 		  	configuration unless an individual test uses the
 * 		  	@KillAndHowBeforeThisTest annotation.
 * 		  ...
 *    }
 * }
 * }</pre>
 * <p>
 * The example above uses the {@code setClasspathPropFilePath()} method to specify a properties file
 * on the classpath.  See {@link KillAndHowBeforeEachTest} for other
 * examples.
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@code @ExtendWith(KillAndHowBeforeAllTestsExtension.class)} on a class,
 * but this annotation is safer because it blocks placement on a method.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
@ExtendWith(KillAndHowBeforeAllTestsExt.class)
public @interface KillAndHowBeforeAllTests {

}
