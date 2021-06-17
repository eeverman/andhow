package org.yarnandtail.andhow.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class PropertyRegistrationListTest {
	

	/**
	 * Test of add method, of class PropertyRegistrationList.
	 */
	@Test
	public void testStartingWithRoot() {
		String className = "org.yat.MyClass";
		
		PropertyRegistrationList list = new PropertyRegistrationList(className);
		
		list.add("Andy");
		list.add("Bob");
		list.add("Cindy", "Inner1");
		list.add("Doug");
		list.add("Evan", "Inner1", "Inner2");
		list.add("Flyn");
		list.add("George", "Inner3");
		list.add("Henry");
		list.add("Ivy", new String[0]);
		list.add("Jay");
		
		assertEquals(className + ".Andy", list.get(0).getCanonicalPropertyName());
		assertEquals(className + ".Bob", list.get(1).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Cindy", list.get(2).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Doug", list.get(3).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Evan", list.get(4).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Flyn", list.get(5).getCanonicalPropertyName());
		assertEquals(className + ".Inner3.George", list.get(6).getCanonicalPropertyName());
		assertEquals(className + ".Inner3.Henry", list.get(7).getCanonicalPropertyName());
		assertEquals(className + ".Ivy", list.get(8).getCanonicalPropertyName());
		assertEquals(className + ".Jay", list.get(9).getCanonicalPropertyName());
		
		//
		//Check sorted order
		list.sort();
		
		assertEquals(className + ".Andy", list.get(0).getCanonicalPropertyName());
		assertEquals(className + ".Bob", list.get(1).getCanonicalPropertyName());
		assertEquals(className + ".Ivy", list.get(2).getCanonicalPropertyName());
		assertEquals(className + ".Jay", list.get(3).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Cindy", list.get(4).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Doug", list.get(5).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Evan", list.get(6).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Flyn", list.get(7).getCanonicalPropertyName());
		assertEquals(className + ".Inner3.George", list.get(8).getCanonicalPropertyName());
		assertEquals(className + ".Inner3.Henry", list.get(9).getCanonicalPropertyName());

	}
	
	@Test
	public void testStartingWithNested() {
		String className = "org.yat.MyClass";
		
		PropertyRegistrationList list = new PropertyRegistrationList(className);
		
		list.add("Andy", "Inner1", "Inner2");
		list.add("Bob");
		list.add("Cindy", "Inner3");
		list.add("Doug", new String[0]);
		list.add("Evan");

		assertEquals(className + ".Inner1.Inner2.Andy", list.get(0).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Bob", list.get(1).getCanonicalPropertyName());
		assertEquals(className + ".Inner3.Cindy", list.get(2).getCanonicalPropertyName());
		assertEquals(className + ".Doug", list.get(3).getCanonicalPropertyName());
		assertEquals(className + ".Evan", list.get(4).getCanonicalPropertyName());
		
		//
		//Check sorted order
		list.sort();
		
		assertEquals(className + ".Doug", list.get(0).getCanonicalPropertyName());
		assertEquals(className + ".Evan", list.get(1).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Andy", list.get(2).getCanonicalPropertyName());
		assertEquals(className + ".Inner1.Inner2.Bob", list.get(3).getCanonicalPropertyName());
		assertEquals(className + ".Inner3.Cindy", list.get(4).getCanonicalPropertyName());

	}

	
}
