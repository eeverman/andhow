package org.yarnandtail.andhow.service;

import java.util.List;
import org.junit.Test;
import org.yarnandtail.andhow.internal.NameAndProperty;
import org.yarnandtail.classvistests.sample.NonStaticInnerClassSample;
import org.yarnandtail.classvistests.sample.PropertySample;

import static org.junit.Assert.*;

import org.yarnandtail.andhow.api.GroupProxy;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrarLoaderTest {
	
	protected static final String NON_STATIC_TLC = NonStaticInnerClassSample.class.getCanonicalName();
	protected static final String PROP_SAMPLE_TLC = PropertySample.class.getCanonicalName();

	@Test
	public void testAll() throws Exception {
		PropertyRegistrarLoader loader = new PropertyRegistrarLoader();
		assertEquals(2, loader.getPropertyRegistrars().size());
		
		List<GroupProxy> groups = loader.getGroups();
		
		assertEquals(9, groups.size());
		
		//
		//Test config of NonStaticInnerClassSample Class and its subclasses
		
		//
		GroupProxy group = groups.stream().filter(g -> g.getCanonicalName().equals(NON_STATIC_TLC + ".PI")).findFirst().get();
		List<NameAndProperty> props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(NON_STATIC_TLC + ".PI.PI_DC")).findFirst().get();
		props = group.getProperties();
		assertEquals(2, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		assertEquals("STRING_PUB", props.get(1).fieldName);
		//
		assertEquals(0, groups.stream().filter(g -> g.getCanonicalName().equals(NON_STATIC_TLC + ".PI.PI_DC.PI_DC_DC")).count());

		//
		assertEquals(0, groups.stream().filter(g -> g.getCanonicalName().equals(NON_STATIC_TLC + ".PC")).count());

		//
		//Test config of PropertySample Class and its subclasses
		
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC)).findFirst().get();
		props = group.getProperties();
		assertEquals(2, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		assertEquals("STRING_PUB", props.get(1).fieldName);
		
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC + ".PI")).findFirst().get();
		props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);

		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC + ".PI.PI_DC")).findFirst().get();
		props = group.getProperties();
		assertEquals(2, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		assertEquals("STRING_PUB", props.get(1).fieldName);
		
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC + ".PI.PI_DI")).findFirst().get();
		props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC + ".PC")).findFirst().get();
		props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC + ".PC.PC_PC")).findFirst().get();
		props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);
		
		//
		group = groups.stream().filter(g -> g.getCanonicalName().equals(PROP_SAMPLE_TLC + ".PC.PC_PI")).findFirst().get();
		props = group.getProperties();
		assertEquals(1, props.size());
		assertEquals("STRING", props.get(0).fieldName);
	}
}
