/*
 */
package org.yarnandtail.andhow.service;

import org.yarnandtail.andhow.service.PropertyRegistration;
import java.util.List;
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
	
	@Test
	public void testCompareRootTo_SameLengthPaths() {
		PropertyRegistration A = new PropertyRegistration("Root", "aaa");
		PropertyRegistration B = new PropertyRegistration("Root", "bbb");
		assertTrue(A.compareRootTo(B) == 0);
		
		A = new PropertyRegistration("com.Root", "aaa");
		B = new PropertyRegistration("com.Root", "bbb");
		assertTrue(A.compareRootTo(B) == 0);
		
		A = new PropertyRegistration("com.Root", "aaa");
		B = new PropertyRegistration("com.Soot", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("com.Soot", "aaa");
		B = new PropertyRegistration("com.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("com.aaa.Root", "aaa");
		B = new PropertyRegistration("com.bbb.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("com.a.Root", "aaa");
		B = new PropertyRegistration("com.bbb.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("com.aaa.Root", "aaa");
		B = new PropertyRegistration("com.b.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("com.bbb.Root", "aaa");
		B = new PropertyRegistration("com.aaa.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("com.b.Root", "aaa");
		B = new PropertyRegistration("com.aaa.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("com.bbb.Root", "aaa");
		B = new PropertyRegistration("com.a.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
	}
	
	@Test
	public void testCompareRootTo_VariousLengthPaths() {
		PropertyRegistration A = new PropertyRegistration("aaa.Root", "aaa");
		PropertyRegistration B = new PropertyRegistration("Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("aaa.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("aaa.bbb.Root", "aaa");
		B = new PropertyRegistration("zzz.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("zzz.Root", "aaa");
		B = new PropertyRegistration("aaa.bbb.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("aaa.bbb.ccc.Root", "aaa");
		B = new PropertyRegistration("aaa.zzz.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("aaa.zzz.Root", "aaa");
		B = new PropertyRegistration("aaa.bbb.ccc.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("aaa.bbb.ccc.Root", "aaa");
		B = new PropertyRegistration("aaa.bbb.Root", "bbb");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("aaa.bbb.Root", "aaa");
		B = new PropertyRegistration("aaa.bbb.ccc.Root", "bbb");
		assertTrue(A.compareRootTo(B) < 0);
		
		A = new PropertyRegistration("aaa.Root1", "aaa");
		B = new PropertyRegistration("Root2", "aaa");
		assertTrue(A.compareRootTo(B) > 0);
		
		A = new PropertyRegistration("aaa.bbb.Root1", "aaa");
		B = new PropertyRegistration("aaa.Root2", "aaa");
		assertTrue(A.compareRootTo(B) > 0);
	}
	

	@Test
	public void testCompareInnerPathTo() {
		PropertyRegistration A = new PropertyRegistration("Root", "aaa", "Inner1");
		PropertyRegistration B = new PropertyRegistration("Root", "bbb", "Inner1");
		assertTrue(A.compareInnerPathTo(B) == 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner0");
		B = new PropertyRegistration("Root", "aaa", "Inner1");
		assertTrue(A.compareInnerPathTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1");
		B = new PropertyRegistration("Root", "aaa", "Inner0");
		assertTrue(A.compareInnerPathTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("XXXX", "bbb");
		assertTrue(A.compareInnerPathTo(B) == 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("XXXX", "bbb", "Inner1", "Inner2");
		assertTrue(A.compareInnerPathTo(B) == 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("XXXX", "bbb", "Inner1", "Inner3");
		assertTrue(A.compareInnerPathTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		B = new PropertyRegistration("XXXX", "bbb", "Inner1", "Inner2");
		assertTrue(A.compareInnerPathTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("Root", "aaa", "Inner1");
		assertTrue(A.compareInnerPathTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		B = new PropertyRegistration("Root", "aaa");
		assertTrue(A.compareInnerPathTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2", "Inner3");
		assertTrue(A.compareInnerPathTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2", "Inner3");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareInnerPathTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner0", "Inner2", "Inner3");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareInnerPathTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner3");
		assertTrue(A.compareInnerPathTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner3");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareInnerPathTo(B) > 0);
		
	}
	
	private boolean compare(List<String> list, String... array) {
		if (array.length != list.size()) {
			return false;
		}
		
		for (int i = 0; i < array.length; i++) {
			if (! array[i].equals(list.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	@Test
	public void testComparator() {
		
		//Simple sort by Property name only
		PropertyRegistration A = new PropertyRegistration("Root", "aaa");
		PropertyRegistration B = new PropertyRegistration("Root", "bbb");
		assertTrue(A.compareTo(B) < 0);
		
		//
		//Sort by prop name only
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("Root", "bbb");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Root", "bbb");
		B = new PropertyRegistration("Root", "aaa");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "bbb", "Inner1", "Inner2");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Root", "bbb", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareTo(B) > 0);
		
		//
		//Equal paths
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("Root", "aaa");
		assertTrue(A.compareTo(B) == 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareTo(B) == 0);
		
		//
		//Sort by root class name only
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("Soot", "aaa");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Soot", "aaa");
		B = new PropertyRegistration("Root", "aaa");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("aaa.Root", "aaa");
		B = new PropertyRegistration("zzz.Root", "aaa");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("zzz.Root", "aaa");
		B = new PropertyRegistration("aaa.Root", "aaa");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("aaa.Root", "aaa");
		B = new PropertyRegistration("Root", "aaa");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa");
		B = new PropertyRegistration("aaa.Root", "aaa");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("aaa.bbb.Root", "aaa");
		B = new PropertyRegistration("aaa.Root", "aaa");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("aaa.Root", "aaa");
		B = new PropertyRegistration("aaa.bbb.Root", "aaa");
		assertTrue(A.compareTo(B) < 0);
		
		//
		//Sort by Inner Path
		A = new PropertyRegistration("Root", "aaa", "Inner1");
		B = new PropertyRegistration("Root", "aaa", "Inner2");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner0", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner0", "Inner2");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		assertTrue(A.compareTo(B) < 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		B = new PropertyRegistration("Root", "aaa", "Inner1");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner2");
		B = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		assertTrue(A.compareTo(B) > 0);
		
		A = new PropertyRegistration("Root", "aaa", "Inner1", "Inner3");
		B = new PropertyRegistration("Root", "aaa", "Inner2");
		assertTrue(A.compareTo(B) < 0);
		
		//Specific test case
		A = new PropertyRegistration("org.yat.MyClass", "Evan", "Inner1", "Inner2");
		B = new PropertyRegistration("org.yat.MyClass", "George", "Inner3");
		assertTrue(A.compareTo(B) < 0);

	}

}
