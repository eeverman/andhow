package org.yarnandtail.andhow;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.internal.RequirementProblem;
import org.yarnandtail.andhow.load.KeyValuePairLoader;
import org.yarnandtail.andhow.property.*;

/**
 *
 * @author eeverman
 */
public class AndHowReentrantTest extends AndHowCoreTestBase {
	
	
	@Test
	public void testAllValuesAreSet() {
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.group(ReentrantSample1.class);
		
		AndHow.instance(config);
		
		assertEquals("one", ReentrantSample1.STR_1.getValue());
		assertEquals("one", ReentrantSample1.STR_2.getValue());
		
		fail("This should have blown up");
	}
	

}
