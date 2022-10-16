package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.KillAndHowBeforeThisTestExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on an individual test method to reset AndHow to its
 * unconfigured state before the test runs.  When the test is done, the original AndHow configured
 * state is restored, if any.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class MyJunit5Test {
 *
 *   @Test
 *   @KillAndHowBeforeThisTest
 *   public void myFirstTest(){
 * 		AndHow.findConfig()
 *        .setClasspathPropFilePath("/test1-prop-file.properties");
 *
 * 		  ...This test will read property values from test1-prop-file.properties...
 *    }
 *
 *    ...All other tests will ignore the AndHow configuration used inside the 'myFirstTest' test.
 *
 * }
 * }</pre>
 * <p>
 * The example above uses the {@code setClasspathPropFilePath()} method to specify a properties file
 * on the classpath.  See {@link KillAndHowBeforeEachTest} for other
 * examples.
 * <p>
 * Note:  Using this annotation on a JUnit test method is the same as using
 * {@code @ExtendWith(KillAndHowBeforeThisTestExtension.class)} on a method, but this annotation is
 * safer because it blocks placement on a class.
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(KillAndHowBeforeThisTestExt.class)
public @interface KillAndHowBeforeThisTest {

}
