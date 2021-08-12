package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.EnableJndiForThisTestMethodExt;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that enables JNDI for use in an individual test method.
 *
 * Within the test method JNDI values can be bound and read, and any invocation of
 * {@code new InitialContext()} in the System Under Test (SUT) will return a JNDI Context
 * containing the bound values.
 * <p>
 * Here is a complete usage example:
 * <pre>{@code
 *  @Test
 *  @EnableJndiForThisTestMethod
 *  void myTest() throws NamingException {
 *
 * 		// Create a context and assign some values
 * 		InitialContext ctx = new InitialContext();
 * 		EnableJndiUtil.createSubcontexts(ctx, "org/do/good");
 * 		ctx.bind("org/do/good/SECRET", "ABCD");
 * 		ctx.close();
 *
 * 		// Meanwhile, somewhere deep in the system under test...
 * 		InitialContext appCtx = new InitialContext();
 * 		assertEquals("ABCD", appCtx.lookup("org/do/good/SECRET"));
 *  }
 * }</pre>
 * <p>
 * Note:  Using this annotation on a JUnit test method is the same as using
 * {@Code @ExtendWith(EnableJndiForThisTestExt.class)} on a method, but this annotation is
 * safer because it blocks placement on a class.
 */
@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(EnableJndiForThisTestMethodExt.class)
public @interface EnableJndiForThisTestMethod {

}
