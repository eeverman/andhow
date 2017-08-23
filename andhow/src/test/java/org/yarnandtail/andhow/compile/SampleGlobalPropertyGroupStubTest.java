package org.yarnandtail.andhow.compile;

import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author ericeverman
 */
public class SampleGlobalPropertyGroupStubTest {
	@Test
	public void testGenerateClassString() throws Exception {
		
		SampleGlobalPropertyGroupStub stub = new SampleGlobalPropertyGroupStub();
		List<Class<?>> gropus = stub.getGroups();
		
		assertEquals(SampleNestedPropGroup.Nested.Config.class, gropus.get(0));

	}
}
