package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on an individual test method to reset AndHow to its
 * unconfigured state before the test runs.  When the test is done, the original AndHow configured
 * state is restored, which may be unconfigured.
 * <p>
 * Example usage:
 * <pre>{@Code
 * public class MyJunit5TestClass {
 *
 *   @Test
 *   @KillAndHowBeforeThisTest
 *   public void aTest() { .... }
 *
 * }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test method is the same as using
 * {@Code @ExtendWith(KillAndHowBeforeThisTestExtension.class)} on a method, but this annotation is
 * safer because it cannot be put on a class.  '@ExtendWith' allows placement on a class,
 * but the extension will only work properly on a method.
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(KillAndHowBeforeThisTestExtension.class)
public @interface KillAndHowBeforeThisTest {

}
