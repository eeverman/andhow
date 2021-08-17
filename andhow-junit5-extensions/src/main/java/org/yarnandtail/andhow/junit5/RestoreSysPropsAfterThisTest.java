package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterThisTestExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for a test method to isolate and restore any changes made to System.Properties.
 *
 * Specifically, changes made to System.Properties within a:<p>
 * <ul>
 * <li><b>{@code @BeforeAll} ARE NOT REVERTED - USE {@code RestoreSysPropsAfterEachTest} for this</b></li>
 * <li><b>{@code @BeforeEach} ARE NOT REVERTED - USE {@code RestoreSysPropsAfterEachTest} for this</b></li>
 * <li><b>{@code @Test} method</b> will be visible only to that test and reverted after that test</li>
 * </ul><p>
 *
 * This can be useful for verifying your application behaves as you expect when you set specific
 * System properties, without affecting other tests.
 * <p>
 * Since the values are stored and reset around a single test method, it makes sense to use this
 * annotation when you plan to modify Sys Props in just a single test method.
 * Here is a complete usage example:
 * <pre>{@code
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
 * {@code @ExtendWith(RestoreSysPropsAfterEachTestExt.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(RestoreSysPropsAfterThisTestExt.class)
public @interface RestoreSysPropsAfterThisTest {

}
