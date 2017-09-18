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
		
		assertEquals(SampleNestedPropGroup.class, gropus.get(0));
		assertEquals(SampleNestedPropGroup.Nest1.Config.class, gropus.get(1));
		assertEquals(SampleNestedPropGroup.Nest2.Config.class, gropus.get(2));

	}
}
