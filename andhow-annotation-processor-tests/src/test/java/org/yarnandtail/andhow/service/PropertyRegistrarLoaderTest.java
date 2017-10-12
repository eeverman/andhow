package org.yarnandtail.andhow.service;

import java.util.List;
import org.junit.Test;
import org.yarnandtail.andhow.api.AutoPropertyGroup;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.classvistests.sample.NonStaticInnerClassSample;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrarLoaderTest {
	
	protected static final String NON_STATIC_TLC = NonStaticInnerClassSample.class.getCanonicalName();

	@Test
	public void testAll() throws Exception {
		PropertyRegistrarLoader loader = new PropertyRegistrarLoader();
		assertEquals(2, loader.getPropertyRegistrars().size());
		
		List<AutoPropertyGroup> groups = loader.getGroups();
		
		assertEquals(9, groups.size());
		//
		AutoPropertyGroup group = groups.stream().filter(g -> g.getCanonicalName().equals(NON_STATIC_TLC + ".PI")).findFirst().get();
		List<NameAndProperty> props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(NON_STATIC_TLC + ".PI.PI_DC")).findFirst().get();
		props = group.getProperties();
		assertEquals(2, props.size());
		//assertEquals("STRING", props.get(0).fieldName);
	}
}
