package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test order is important here because we want to verify that JNDI and Sys Props are
 * reset ofter a test is complete.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@EnableJndiForThisTestClass
public class EnableJndiForThisTestClassTest {
	private static String TEST_NAME = "org/do/well/out/there/SECRET_PIN";

	private static String TEST_VAL = "ABCD";

	@BeforeAll
	static void beforeAll() throws NamingException {
		//
		// Create a context and assign some values
		InitialContext orgCtx = new InitialContext();
		EnableJndiUtil.createSubcontexts(orgCtx, "org/do/well/out/there");
		orgCtx.bind(TEST_NAME, TEST_VAL);
	}


	@Test  //In order
	void test1_ShouldBeAbleToSeeValueBoundInBeforeAll() throws NamingException {
		InitialContext appCtx = new InitialContext();
		assertEquals(TEST_VAL, appCtx.lookup(TEST_NAME));

		//This is a bad thing to do in a test, but just to prove the the context is the same for
		//all tests, modify the value here and see if its the same in the 2nd test
		appCtx.rebind(TEST_NAME, "SOMETHING_ELSE");
	}

	@Test  //In order
	void test2_LaterTestsShouldSeeModifiedState() throws NamingException {
		InitialContext appCtx = new InitialContext();
		assertEquals("SOMETHING_ELSE", appCtx.lookup(TEST_NAME));
	}

}
