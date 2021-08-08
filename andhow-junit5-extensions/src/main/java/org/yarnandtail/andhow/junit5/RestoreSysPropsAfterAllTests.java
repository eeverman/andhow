package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterAllTestsExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on a test class to store System.Properties values prior to the test
 * class, then restore them after all tests in this class have completed.
 *
 * This can be useful for verifying your application behaves as you expect when you set specific
 * System properties, without affecting other tests.
 * <p>
 * Since the values are reset after all tests in the class have completed, a {@code @BeforeAll}
 * block should be used to modify the values for your tests.  Here is a complete usage example:
 * <pre>{@Code
 * @RestoreSysPropsAfterAllTests
 * public class MyJunit5Test {
 *
 *   @BeforeAll
 *   public static void configAndHowForAllTests(){
 * 		System.setProperty("SomeKey", "SomeValue");
 *   }
 *
 *   ...tests that will all share the same configuration...
 * }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@Code @ExtendWith(RestoreSysPropsAfterAllTests.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(RestoreSysPropsAfterAllTestsExt.class)
public @interface RestoreSysPropsAfterAllTests {

}
