/*
 */
package org.yarnandtail.andhow.compile;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrationTest {
	

	@Test
	public void testNonNestedProperty() {
		
		PropertyRegistration reg = new PropertyRegistration("org.yat.Root", "Prop");
		
		assertEquals("org.yat.Root", reg.getCanonicalRootName());
		assertEquals("org.yat.Root", reg.getJavaCanonicalParentName());
		assertEquals("Prop", reg.getPropertyName());
		assertEquals("org.yat.Root.Prop", reg.getCanonicalPropertyName());
		assertNull(reg.getInnerPath());
	}
	
	@Test
	public void testConversionOfInnerPathToNull() {
		
		PropertyRegistration reg = new PropertyRegistration("org.yat.Root", "Prop", new String[0]);
		assertNull(reg.getInnerPath());
	}
	
	@Test
	public void testNonNestedPropertyWithDollarSignInName() {
		PropertyRegistration reg = new PropertyRegistration("org.yat.$Root", "$Prop");
		
		assertEquals("org.yat.$Root", reg.getCanonicalRootName());
		assertEquals("org.yat.$Root", reg.getJavaCanonicalParentName());
		assertEquals("$Prop", reg.getPropertyName());
		assertEquals("org.yat.$Root.$Prop", reg.getCanonicalPropertyName());
		assertNull(reg.getInnerPath());
	}
	
	@Test
	public void testNestedProperty() {
		
		PropertyRegistration reg = new PropertyRegistration("org.yat.Root", "Prop", "A", "B", "C");
		
		assertEquals("org.yat.Root", reg.getCanonicalRootName());
		assertEquals("org.yat.Root$A$B$C", reg.getJavaCanonicalParentName());
		assertEquals("Prop", reg.getPropertyName());
		assertEquals("org.yat.Root.A.B.C.Prop", reg.getCanonicalPropertyName());
		assertArrayEquals(new String[] {"A", "B", "C"}, reg.getInnerPath());
	}
	
	@Test
	public void testNestedPropertyWithDollarNames() {
		
		PropertyRegistration reg = new PropertyRegistration("org.yat.$Root", "$Prop", "A", "B", "C");
		
		assertEquals("org.yat.$Root", reg.getCanonicalRootName());
		assertEquals("org.yat.$Root$A$B$C", reg.getJavaCanonicalParentName());
		assertEquals("$Prop", reg.getPropertyName());
		assertEquals("org.yat.$Root.A.B.C.$Prop", reg.getCanonicalPropertyName());
		assertArrayEquals(new String[] {"A", "B", "C"}, reg.getInnerPath());
	}

}
