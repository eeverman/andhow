package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.KillAndHowBeforeEachTestExt;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be used on a JUnit test class to reset AndHow to its unconfigured state
 * prior to each individual test.  When all tests in the class are done, the original
 * configured state of AndHow is restored, which may be unconfigured.
 * <p>
 * Example usage:
 * <pre>{@code
 * @KillAndHowBeforeEachTest
 * public class MyJunit5Test {
 *
 *   @Test
 *   public void myFirstTest(){
 * 		AndHow.findConfig()
 *        .setClasspathPropFilePath("/test1-prop-file.properties");
 *
 * 		  ...This test will read property values from test1-prop-file.properties...
 *    }
 *   @Test
 *   public void mySecondTest(){
 * 		AndHow.findConfig()
 * 				.addFixedValue([AndHowProperty reference or name], [Value for that Property])
 * 				.addFixedValue(...);
 *
 * 		  ...This test just has some explicitly set values via 'addFixedValue()'...
 *    }
 *
 *   ...other tests that can each configure AndHow...
 *
 * }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@code @ExtendWith(KillAndHowBeforeEachTestExtension.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
@ExtendWith(KillAndHowBeforeEachTestExt.class)
public @interface KillAndHowBeforeEachTest {

}
