package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.EnableJndiForThisTestClassExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that enables JNDI for use in a test class.
 *
 * Within the test class JNDI values can be bound and read, and any invocation of
 * {@code new InitialContext()} in the System Under Test (SUT) will return a JNDI Context
 * containing the bound values.
 * <p>
 * Since the JNDI context spans the complete lifecycle of the test class, best practice is to
 * bind values in a {@code @BeforeAll} block. Here is a complete usage example:
 * <pre>{@code
 * @EnableJndiForThisTestClass
 * public class MyTestClass {
 *
 *  @Test
 *  void myTest() throws NamingException {
 *
 *    @BeforeAll
 *    static void beforeAll() throws NamingException {
 *
 * 		  // Create a context and assign some values
 * 		  InitialContext orgCtx = new InitialContext();
 * 		  ctx.bind("org/do/good/SECRET", "ABCD");
 * 		  ctx.close();
 * 		}
 *
 *  @Test
 *  void myTest() throws NamingException {
 *
 * 		// In this test method or somewhere deep in the system under test...
 * 		InitialContext appCtx = new InitialContext();
 * 		assertEquals("ABCD", appCtx.lookup("org/do/good/SECRET"));
 *  }
 *  }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test class is the same as using
 * {@Code @ExtendWith(EnableJndiForThisTestClassExt.class)} on a class, but this annotation is
 * safer because it blocks placement on a method.
 */
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(EnableJndiForThisTestClassExt.class)
public @interface EnableJndiForThisTestClass {

}
