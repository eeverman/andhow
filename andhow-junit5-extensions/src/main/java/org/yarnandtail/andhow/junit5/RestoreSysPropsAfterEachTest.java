package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterEachTestExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for a test class to isolate and restore any changes made to System.Properties.
 *
 * Specifically, changes made to System.Properties within a:<p>
 * <ul>
 * <li><b>{@code @BeforeAll}</b> will be visible to each test and reverted after the last test or {@code @AfterAll}</li>
 * <li><b>{@code @BeforeEach}</b> will be visible to each test and reverted after each test or {@code @AfterEach}</li>
 * <li><b>{@code @Test} method</b> will be visible only to that test and reverted after that test</li>
 * </ul><p>
 * This can be useful for verifying an application behaves as expected with specific Sys Props
 * without affecting other tests. Use this annotation when several test methods share a set of
 * Sys Prop settings, or if multiple tests set Sys Props.
 * <p>
 * Here is a complete usage example:
 * <pre>{@code
 * @RestoreSysPropsAfterEachTest
 * public class MyJunit5Test {
 *
 *   @BeforeAll //Could be @BeforeAll or use both
 *   public static void configAndHowForAllTests(){
 * 		System.setProperty("SomeKey", "SomeValue");	// will be visible to myTest
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
