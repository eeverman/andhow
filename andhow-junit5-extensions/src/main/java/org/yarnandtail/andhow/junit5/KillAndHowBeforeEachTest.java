package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.KillAndHowBeforeEachTestExtension;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be used on a JUnit test class to reset AndHow to its unconfigured state
 * prior to each individual test.  When all tests in the class are done, the original
 * configured state of AndHow is restored, which may be unconfigured.
 * <p>
 * Example usage:
 * <pre>{@Code
 * @KillAndHowBeforeEachTest
 * public class MyJunit5TestClass {
 *
 *   @Test
 *   public void doATest(){
 * 		AndHow.findConfig()
 * 				.addFixedValue([AndHowProperty reference or name], [Value for that Property])
 * 				.addFixedValue(...)
 * 				.build();
 *
 * 		  ...	code for this test...
 *    }
 *
 *   ...other tests that can each configure AndHow...
 *
 * }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@Code @ExtendWith(KillAndHowBeforeEachTestExtension.class)} on a class, but this annotation is
 * safer because it cannot be put on a method.  '@ExtendWith' allows placement on a method,
 * but the extension will only work properly on a class.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(KillAndHowBeforeEachTestExtension.class)
public @interface KillAndHowBeforeEachTest {

}
