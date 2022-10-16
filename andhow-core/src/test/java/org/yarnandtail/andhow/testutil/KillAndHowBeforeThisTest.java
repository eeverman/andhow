package org.yarnandtail.andhow.testutil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.testutil.KillAndHowBeforeThisTestExt;

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
 * This class has the same functionality as the class in the junit5 extension module, however, that
 * module depends on andhow-core, so it cannot be used in andhow-core.
 *
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(KillAndHowBeforeThisTestExt.class)
public @interface KillAndHowBeforeThisTest {

}
