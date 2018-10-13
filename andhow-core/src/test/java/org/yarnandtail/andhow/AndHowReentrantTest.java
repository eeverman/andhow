package org.yarnandtail.andhow;


import static org.junit.Assert.*;

import org.junit.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.ConstructionProblem;

/**
 *
 * @author eeverman
 */
public class AndHowReentrantTest extends AndHowCoreTestBase {
	
	
	@Test
	public void testAllValuesAreSet() {
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.group(ReentrantSample1.class);
		
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
