package org.yarnandtail.andhow;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.ConstructionProblem;

/**
 *
 * Note:  The order of the tests is important and is sorted by method name.
 * The key reason for this is to test that an InitiationLoopException does not
 * bleed over from one test to another. 
 * @author eeverman
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AndHowReentrantTest extends AndHowCoreTestBase {
	
	
	@Test
	public void test_1_AndHowReentrantTest_BadSample_1() {
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.group(AndHowReentrantTest_BadSample_1.class);
		
		try {
			AndHow.instance(config);
			fail("This should have blown up");
		} catch (Throwable t) {
			assertTrue(t.getCause() instanceof AppFatalException);
			AppFatalException afe = (AppFatalException)t.getCause();
			assertEquals(1, afe.getProblems().size());
			assertTrue(afe.getProblems().get(0) instanceof ConstructionProblem.InitiationLoopException);
		}
	}
	
	@Test
	public void test_2_AndHowReentrantTest_OkSample_1() {
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.group(AndHowReentrantTest_OkSample_1.class);
		

			AndHow.instance(config);

			assertEquals("onetwo", AndHowReentrantTest_OkSample_1.getSomeString());
			assertEquals("one", AndHowReentrantTest_OkSample_1.STR_1.getValue());
			assertEquals("two", AndHowReentrantTest_OkSample_1.STR_2.getValue());

	}
	
	@Test
	public void test_3_AndHowReentrantTest_BadSample_2() {
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.group(AndHowReentrantTest_BadSample_2.class);
		
		try {
			AndHow.instance(config);
			fail("This should have blown up");
		} catch (Throwable t) {
			assertTrue(t.getCause() instanceof AppFatalException);
			AppFatalException afe = (AppFatalException)t.getCause();
			assertEquals(1, afe.getProblems().size());
			assertTrue(afe.getProblems().get(0) instanceof ConstructionProblem.InitiationLoopException);
		}
	}
	
	

}
