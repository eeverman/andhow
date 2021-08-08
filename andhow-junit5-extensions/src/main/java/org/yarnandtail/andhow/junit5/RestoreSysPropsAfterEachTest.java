package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterEachTestExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that can be placed on a test class to store System.Properties values prior to the test
 * class, then restore them after each test method is run.
 *
 * This annotation will restore changes made in {@code @BeforeAll} and {@code @BeforeEach} as well,
 * but their System.Properties modifications will be visible within the tests.
 * <p>
 * This can be useful for verifying your application behaves as you expect when you set specific
 * System properties, without affecting other tests.
 * <p>
 * Since the property values are reset after each test is run to the state created by
 * {@code @BeforeAll} and {@code @BeforeEach}, it makes sense to use this annotation
 * when you need a shared complex property state which is modified slightly for each test.
 * Here is a complete usage example:
 * <pre>{@Code
 * @RestoreSysPropsAfterEachTest
 * public class MyJunit5Test {
 *
 *   @BeforeAll //Could be @BeforeAll or use both
 *   public static void configAndHowForAllTests(){
 * 		System.setProperty("SomeKey", "SomeValue");
 *   }
 *
 *   @Test
 *   public void myTest(){
 * 		System.setProperty("AddAnAdditionProperty", "SomeValue");
 * 		...some test assertions...
 *   }
 * }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@Code @ExtendWith(RestoreSysPropsAfterEachTestExt.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(RestoreSysPropsAfterEachTestExt.class)
public @interface RestoreSysPropsAfterEachTest {

}
