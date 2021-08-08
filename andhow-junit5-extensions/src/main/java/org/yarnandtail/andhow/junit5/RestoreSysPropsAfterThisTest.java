package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterThisTestExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on a test method to store System.Properties values prior to the
 * test method, then restore them after the test method is run.
 *
 * This can be useful for verifying your application behaves as you expect when you set specific
 * System properties, without affecting other tests.
 * <p>
 * Since the values are stored and reset around a single test method, it makes sense to use this
 * annotation when you plan to modify Sys Props in just a single test method.
 * Here is a complete usage example:
 * <pre>{@Code
 * public class MyJunit5Test {
 *
 *   @Test
 *   @RestoreSysPropsAfterThisTest
 *   public void myTest(){
 * 		System.setProperty("SomeKey", "SomeValue");
 * 		...some test assertions...
 *   }
 * }
 * }</pre>
 * <p>
 * <em>Caution: </em>If you modify System Properties in {@code @BeforeAll} or {@code @BeforeEach}
 * blocks, use {@code RestoreSysPropsAfterAllTests} or {@code RestoreSysPropsAfterEachTest} instead,
 * which will restore changes made in those pre-test methods.
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@Code @ExtendWith(RestoreSysPropsAfterEachTestExt.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(RestoreSysPropsAfterThisTestExt.class)
public @interface RestoreSysPropsAfterThisTest {

}
