package org.yarnandtail.andhow.compile;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ericeverman
 */
public class CompileUnitTest {
	
	private final String ROOT_QUAL_NAME = "org.big.comp.MyClass";
	private final String INNER1_SIMP_NAME = "Inner1";
	private final String INNER2_SIMP_NAME = "Inner2";
	
	private final String PROP1_NAME = "prop1";
	private final String PROP2_NAME = "prop2";
	
	public CompileUnitTest() {
	}

	/**
	 * Test of pushType method, of class CompileUnit.
	 */
	@Test
	public void testHappyPath() {
		
		
		
		CompileUnit cu = new CompileUnit(ROOT_QUAL_NAME);
		
		//root prop
		cu.foundProperty(new SimpleVariable(PROP1_NAME, true, true));
		
		//1st inner class
		cu.pushType(INNER1_SIMP_NAME, true);
		cu.foundProperty(new SimpleVariable(PROP1_NAME, true, true));
		
		//2nd inner class
		cu.pushType(INNER2_SIMP_NAME, true);
		cu.foundProperty(new SimpleVariable(PROP1_NAME, true, true));
		cu.foundProperty(new SimpleVariable(PROP2_NAME, true, true));
		
		//one more at 1st inner level
		cu.popType();
		cu.foundProperty(new SimpleVariable(PROP2_NAME, true, true));
		
		//one more at root level
		cu.popType();
		cu.foundProperty(new SimpleVariable(PROP2_NAME, true, true));
		
		PropertyRegistrationList list = cu.getRegistrations();
		
		assertEquals(6, list.size());
		assertFalse(cu.hasErrors());
		
		assertEquals(ROOT_QUAL_NAME, list.getRootCanonicalName());
		assertEquals(ROOT_QUAL_NAME + "." + PROP1_NAME, list.get(0).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME + "." + INNER1_SIMP_NAME + "." + PROP1_NAME, list.get(1).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME, list.get(1).getCanonicalRootName());
		assertEquals(ROOT_QUAL_NAME + "$" + INNER1_SIMP_NAME, list.get(1).getJavaCanonicalParentName());
	}

	/**
	 * Test of popType method, of class CompileUnit.
	 */
	@Test
	public void testPopType() {
	}

	/**
	 * Test of foundProperty method, of class CompileUnit.
	 */
	@Test
	public void testFoundProperty() {
	}

	/**
	 * Test of getInnerPath method, of class CompileUnit.
	 */
	@Test
	public void testGetInnerPath() {
	}

	/**
	 * Test of getInnerPathNames method, of class CompileUnit.
	 */
	@Test
	public void testGetInnerPathNames() {
	}

	/**
	 * Test of addPropertyError method, of class CompileUnit.
	 */
	@Test
	public void testAddPropertyError() {
	}

	/**
	 * Test of getCanonicalRootName method, of class CompileUnit.
	 */
	@Test
	public void testGetCanonicalRootName() {
	}

	/**
	 * Test of getRegistrations method, of class CompileUnit.
	 */
	@Test
	public void testGetRegistrations() {
	}

	/**
	 * Test of getErrors method, of class CompileUnit.
	 */
	@Test
	public void testGetErrors() {
	}

	/**
	 * Test of hasErrors method, of class CompileUnit.
	 */
	@Test
	public void testHasErrors() {
	}

	/**
	 * Test of hasRegistrations method, of class CompileUnit.
	 */
	@Test
	public void testHasRegistrations() {
	}
	
}
