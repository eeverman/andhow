package org.yarnandtail.andhow.service;

import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.junit.Test;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.property.*;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrarLoaderTest {
	
	@Test
	public void testGetGroups() {
		TestPropertyRegistrarLoader loader = new TestPropertyRegistrarLoader();
		
		List<GroupProxy> groups = loader.getGroups();
		assertEquals(2, groups.size());
		assertEquals(PropertyRegistrarLoaderTest.class.getCanonicalName(), groups.get(0).getCanonicalName());
		assertEquals(PropertyRegistrarLoaderTest.class.getCanonicalName() + "$InnerClass", groups.get(1).getJavaCanonicalName());
			
		//Props in the main class (which is this test class)	
		assertEquals(1, groups.get(0).getProperties().size());
		assertEquals("MY_PROP1", groups.get(0).getProperties().get(0).fieldName);
		assertTrue(MY_PROP1 == groups.get(0).getProperties().get(0).property);
		
		//Props in the inner class (which is an inner class of this test class)	
		assertEquals(2, groups.get(1).getProperties().size());
		assertEquals("MY_PROP2", groups.get(1).getProperties().get(0).fieldName);
		assertTrue(InnerClass.MY_PROP2 == groups.get(1).getProperties().get(0).property);
		assertEquals("MY_PROP3", groups.get(1).getProperties().get(1).fieldName);
		assertTrue(InnerClass.MY_PROP3 == groups.get(1).getProperties().get(1).property);
	}
	
	//
	//This set of three properties are used to test the Registrar.
	private static BolProp MY_PROP1 = BolProp.builder().build();
	
	public static class InnerClass {
		private static DblProp MY_PROP2 = DblProp.builder().build();
		public static LocalDateTimeProp MY_PROP3 = LocalDateTimeProp.builder().build();
	}
	
	private class TestPropertyRegistrarLoader extends PropertyRegistrarLoader {


		@Override
		public List<PropertyRegistrar> getPropertyRegistrars() {
			List<PropertyRegistrar> regs = new ArrayList();
			
			PropertyRegistrar pr1 = new AbstractPropertyRegistrar() {
				@Override
				public void addPropertyRegistrations(PropertyRegistrationList list) {
					list.add("MY_PROP1");
					list.add("MY_PROP2", "InnerClass");
					list.add("MY_PROP3");	//should be in InnerClass
				}

				@Override
				public String getRootCanonicalName() {
					return PropertyRegistrarLoaderTest.class.getCanonicalName();
				}
			};
					
			regs.add(pr1);
			
			
			
			return regs;
		}
		
	}
}
