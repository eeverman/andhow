package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.service.PropertyRegistrationList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		cu.addProperty(PROP1_NAME, true, true);	//0
		
		{
			//1st inner class
			cu.pushType(INNER1_SIMP_NAME, true);
			cu.addProperty(PROP1_NAME, true, true);	//1

			{
				//2nd inner class
				cu.pushType(INNER2_SIMP_NAME, true);
				
				//Check inner path order
				assertEquals(2, cu.getInnerPath().size());
				assertEquals(INNER1_SIMP_NAME, cu.getInnerPath().get(0).getName());
				assertEquals(INNER2_SIMP_NAME, cu.getInnerPath().get(1).getName());
				assertEquals(INNER1_SIMP_NAME, cu.getInnerPathNames().get(0));
				assertEquals(INNER2_SIMP_NAME, cu.getInnerPathNames().get(1));
				//
				
				cu.addProperty(PROP1_NAME, true, true);	//2
				cu.addProperty(PROP2_NAME, true, true);	//3
				cu.popType();
				
				//Check we have the right inner path remaining
				assertEquals(1, cu.getInnerPath().size());
				assertEquals(INNER1_SIMP_NAME, cu.getInnerPathNames().get(0));
			}

			//one more at 1st inner level
			cu.addProperty(PROP2_NAME, true, true);	//4
			cu.popType();
		}
		
		//one more at root level
		cu.addProperty(PROP2_NAME, true, true);	//5
		
		PropertyRegistrationList list = cu.getRegistrations();
		
		assertEquals(6, list.size());
		assertFalse(cu.hasProblems());
		assertEquals(ROOT_QUAL_NAME, list.getRootCanonicalName());
		
		
		//Checking all properties
		assertEquals(ROOT_QUAL_NAME + "." + PROP1_NAME, list.get(0).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME, list.get(0).getCanonicalRootName());
		assertEquals(ROOT_QUAL_NAME, list.get(0).getJavaCanonicalParentName());
		//
		assertEquals(ROOT_QUAL_NAME + "." + INNER1_SIMP_NAME + "." + PROP1_NAME, list.get(1).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME, list.get(1).getCanonicalRootName());
		assertEquals(ROOT_QUAL_NAME + "$" + INNER1_SIMP_NAME, list.get(1).getJavaCanonicalParentName());
		//
		assertEquals(ROOT_QUAL_NAME + "." + INNER1_SIMP_NAME + "." + INNER2_SIMP_NAME + "." + PROP1_NAME, list.get(2).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME + "$" + INNER1_SIMP_NAME + "$" + INNER2_SIMP_NAME, list.get(2).getJavaCanonicalParentName());
		//
		assertEquals(ROOT_QUAL_NAME + "." + INNER1_SIMP_NAME + "." + INNER2_SIMP_NAME + "." + PROP2_NAME, list.get(3).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME + "$" + INNER1_SIMP_NAME + "$" + INNER2_SIMP_NAME, list.get(3).getJavaCanonicalParentName());
		//
		assertEquals(ROOT_QUAL_NAME + "." + INNER1_SIMP_NAME + "." + PROP2_NAME, list.get(4).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME + "$" + INNER1_SIMP_NAME, list.get(4).getJavaCanonicalParentName());
		//
		assertEquals(ROOT_QUAL_NAME + "." + PROP2_NAME, list.get(5).getCanonicalPropertyName());
		assertEquals(ROOT_QUAL_NAME, list.get(5).getCanonicalRootName());
		assertEquals(ROOT_QUAL_NAME, list.get(5).getJavaCanonicalParentName());

		//
		assertThrows(RuntimeException.class, () -> cu.popType(), "No more types left on stack");
	}
	
	@Test
	public void testGetPackageName() {
		CompileUnit cu = new CompileUnit("org.big.comp.MyClass");
		assertEquals("org.big.comp", cu.getRootPackageName());
		
		cu = new CompileUnit("a.MyClass");
		assertEquals("a", cu.getRootPackageName());
		
		cu = new CompileUnit("a.b.MyClass");
		assertEquals("a.b", cu.getRootPackageName());
		
		cu = new CompileUnit("MyClass");
		assertNull(cu.getRootPackageName());
	}
	
	@Test
	public void testGetRootSimpleName() {
		CompileUnit cu = new CompileUnit("org.big.comp.MyClass");
		assertEquals("MyClass", cu.getRootSimpleName());
		
		cu = new CompileUnit("a.MyClass");
		assertEquals("MyClass", cu.getRootSimpleName());
		
		cu = new CompileUnit("a.b.MyClass");
		assertEquals("MyClass", cu.getRootSimpleName());
		
		cu = new CompileUnit("MyClass");
		assertEquals("MyClass", cu.getRootSimpleName());
	}

	
}
