package org.yarnandtail.andhow.util;

import org.yarnandtail.andhow.util.NameUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Naming support for Properties.
 * 
 * @author ericeverman
 */
public class NameUtilTest {
	
	//Used for a characterization test of Java naming
	private interface TestInterface {}
	
	@Test
	public void basicCharacterizationTestOfJavaNames_UnderstandThisBeforeWorkingOnNames() throws ClassNotFoundException {
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest", NameUtilTest.class.getCanonicalName());
		
		//NOTE:  class.getAndHowName() returns the dot separated AndHow canonical style name
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest.TestInterface", TestInterface.class.getCanonicalName());
		
		//NOTE:  class.getName() returns the Java dollar sign separated inner class style paths
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest$TestInterface", TestInterface.class.getName());
		
		assertEquals(TestInterface.class, Class.forName("org.yarnandtail.andhow.util.NameUtilTest$TestInterface"));
		
		//This would fail w/ a ClassNotFoundException because Java needs the dollar sign at the inner class
		//assertEquals(TestInterface.class, Class.forName("org.yarnandtail.andhow.util.NameUtilTest.TestInterface"));
		
	}


	//
	//Test names w/ arrays for inner path
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathArray_DollarNamedProperty_NoArray() {
		assertEquals(
				"org.yat.Root.Prop",
				NameUtil.getAndHowName("org.yat.Root", "Prop")
		);
	}
	
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathArray_DollarNamedProperty_EmptyArray() {
		assertEquals(
				"org.yat.$Root.$Prop",
				NameUtil.getAndHowName("org.yat.$Root", "$Prop", new String[0])
		);
	}
	
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathArray() {
		assertEquals(
				"org.yat.Root.A.B.C.Prop",
				NameUtil.getAndHowName("org.yat.Root", "Prop", "A", "B", "C")
		);
	}
	
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathArray_DollarNamedProperty() {
		assertEquals(
				"org.yat.$Root.A.B.C.$Prop",
				NameUtil.getAndHowName("org.yat.$Root", "$Prop", "A", "B", "C")
		);
	}
	
	//
	//Test names w/ Lists for inner path
	
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathList_DollarNamedProperty_EmptyList() {
		assertEquals(
				"org.yat.$Root.$Prop",
				NameUtil.getAndHowName("org.yat.$Root", "$Prop", list())
		);
	}
	
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathList() {
		assertEquals(
				"org.yat.Root.A.B.C.Prop",
				NameUtil.getAndHowName("org.yat.Root", "Prop", list("A", "B", "C"))
		);
	}
	
	@Test
	public void testGetAndHowName_forPropertyWithInnerPathList_DollarNamedProperty() {
		assertEquals(
				"org.yat.$Root.A.B.C.$Prop",
				NameUtil.getAndHowName("org.yat.$Root", "$Prop", list("A", "B", "C"))
		);
	}

	/**
	 * Test of getJavaName method, of class NameUtil.
	 */
	@Test
	public void testGetJavaName_forGroupWithInnerPathArray() {
		
		assertEquals(
				"a.b.c.MyClass$a$b$c",
				NameUtil.getJavaName("a.b.c.MyClass", new String[] {"a", "b", "c"})
		);
		
		assertEquals(
				"MyClass$a$b$c",
				NameUtil.getJavaName("MyClass", new String[] {"a", "b", "c"})
		);
		
		assertEquals(
				"a.b.c.MyClass",
				NameUtil.getJavaName("a.b.c.MyClass", new String[] {})
		);
		
		assertEquals(
				"a.b.c.MyClass",
				NameUtil.getJavaName("a.b.c.MyClass", (String[]) null)
		);
		
		assertEquals(
				"a.b.c.MyClass$$a$b$$c",
				NameUtil.getJavaName("a.b.c.MyClass", new String[] {"$a", "b", "$c"})
		);
	}

	/**
	 * Test of getJavaName method, of class NameUtil.
	 */
	@Test
	public void testGetJavaName_forGroupWithInnerPathList() {
		assertEquals("a.b.c.MyClass$a$b$c",
				NameUtil.getJavaName("a.b.c.MyClass", list("a", "b", "c"))
		);
		
		assertEquals("MyClass$a$b$c",
				NameUtil.getJavaName("MyClass", list("a", "b", "c"))
		);
		
		assertEquals("a.b.c.MyClass",
				NameUtil.getJavaName("a.b.c.MyClass", list())
		);
		
		assertEquals("a.b.c.MyClass",
				NameUtil.getJavaName("a.b.c.MyClass", (List) null)
		);
		
		assertEquals("a.b.c.MyClass$$a$b$$c",
				NameUtil.getJavaName("a.b.c.MyClass", list("$a", "b", "$c"))
		);
	}
	

	/**
	 * Test of getAndHowName method, of class NameUtil.
	 */
	@Test
	public void testAndHowName_forPropertyNameList() {
		assertEquals("a.b.c.MyClass.a.b.c",
				NameUtil.getAndHowName("a.b.c.MyClass", new String[] {"a", "b", "c"})
		);
		
		assertEquals("MyClass.a.b.c",
				NameUtil.getAndHowName("MyClass", new String[] {"a", "b", "c"})
		);
		
		assertEquals("a.b.c.MyClass",
				NameUtil.getAndHowName("a.b.c.MyClass", new String[] {})
		);
		
		assertEquals("a.b.c.MyClass",
				NameUtil.getAndHowName("a.b.c.MyClass", null)
		);
		
		
		assertEquals("a.b.c.MyClass.$a.b.$c",
				NameUtil.getAndHowName("a.b.c.MyClass", new String[] {"$a", "b", "$c"})
		);
	}
	
	/**
	 * Test of getAndHowName method, of class NameUtil.
	 */
	@Test
	public void testAndHowName_forGroup() {
		
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest.InnerInterface1.InnerClass2",
				NameUtil.getAndHowName(InnerInterface1.InnerClass2.class));
		
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest",
				NameUtil.getAndHowName(NameUtilTest.class));
	}
	
	@Test
	public void testGetJavaName_forGroup() {
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest$InnerInterface1$InnerClass2",
				NameUtil.getJavaName(InnerInterface1.InnerClass2.class));
		
		assertEquals("org.yarnandtail.andhow.util.NameUtilTest",
				NameUtil.getJavaName(NameUtilTest.class));
	}
	
	
	private List<String> list(String... array) {
		return Arrays.asList(array);
	}

	
	private interface InnerInterface1 {
		class InnerClass2 {
			
		}
		
	}
}
