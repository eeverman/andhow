package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test order is important here because we want to verify that JNDI and Sys Props are
 * reset ofter a test is complete.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class EnableJndiForThisTestMethodTest {
	private static String TEST_NAME = "org/do/well/out/there/SECRET_PIN";

	private static String TEST_VAL = "ABCD";

	@Test  //In order
	void test1_JndiWontWorkUntilSysPropsAreSetPointingToContextFactory() throws NamingException {
		InitialContext orgCtx = new InitialContext();

		assertThrows(javax.naming.NoInitialContextException.class,
				() -> orgCtx.bind("org/my", "Bob"),
				"w/o config from the extension, JNDI cannot find an implementation class"
		);
	}

	@Test  //In order
	@EnableJndiForThisTestMethod
	void test2_JndiWorksWhenAnnotationIsUsed() throws NamingException {

		//
		// Create a context and assign some values
		InitialContext orgCtx = new InitialContext();
		orgCtx.bind(TEST_NAME, TEST_VAL);
		orgCtx.close();

		//
		// Meanwhile, somewhere deep in the system under test...

		InitialContext appCtx = new InitialContext();
		assertEquals(TEST_VAL, appCtx.lookup(TEST_NAME));
	}

	@Test  //In order
	void test3_JndiAndSysPropsAreGoneForTestsFollowingTheAnnotation() throws NamingException {
		InitialContext orgCtx = new InitialContext();

		assertThrows(javax.naming.NoInitialContextException.class,
				() -> orgCtx.bind("org/my", "Bob"),
				"No config available"
		);

		//These Sys Props have been removed...
		assertFalse(System.getProperties().containsKey("java.naming.factory.initial"));
		assertFalse(System.getProperties().containsKey("org.osjava.sj.jndi.shared"));
		assertFalse(System.getProperties().containsKey("org.osjava.sj.jndi.ignoreClose"));

	}
}
