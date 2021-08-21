package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.KillAndHowBeforeAllTestsExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on a test class to reset AndHow to its unconfigured state
 * (only) before the first test runs.  When all tests in the class are done, the
 * original AndHow configured state is restored, which may be unconfigured.
 * <p>
 * Example usage:
 * <pre>{@code
 * @KillAndHowBeforeAllTests
 * public class MyJunit5Test {
 *
 *   @BeforeAll
 *   public static void configAndHowForAllTests(){
 * 		AndHow.findConfig()
 * 				.addFixedValue([AndHowProperty reference or name], [Value for that Property])
 * 				.addFixedValue(...)
 * 				.build();
 *    }
 *
 *   ...tests that will all share the same configuration...
 *
 * }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@code @ExtendWith(KillAndHowBeforeAllTestsExtension.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(KillAndHowBeforeAllTestsExt.class)
public @interface KillAndHowBeforeAllTests {

}
