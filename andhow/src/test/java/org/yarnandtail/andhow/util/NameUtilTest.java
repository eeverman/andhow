package org.yarnandtail.andhow.util;

import org.yarnandtail.andhow.util.NameUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Naming support for Properties.
 * 
 * @author ericeverman
 */
public class NameUtilTest {
	

	//
	//Test names w/ arrays for inner path
	@Test
	public void testGetCanonicalPropertyName_NonNestedProperty() {
		assertEquals(
				"org.yat.Root.Prop",
				NameUtil.getCanonicalPropertyName("org.yat.Root", "Prop")
		);
	}
	
	@Test
	public void testGetCanonicalPropertyName_PropertyWithDollarSignInName() {
		assertEquals(
				"org.yat.$Root.$Prop",
				NameUtil.getCanonicalPropertyName("org.yat.$Root", "$Prop", new String[0])
		);
	}
	
	@Test
	public void testGetCanonicalPropertyName_NestedProperty() {
		assertEquals(
				"org.yat.Root.A.B.C.Prop",
				NameUtil.getCanonicalPropertyName("org.yat.Root", "Prop", "A", "B", "C")
		);
	}
	
	@Test
	public void testGetCanonicalPropertyName_NestedPropertyWithDollarNames() {
		assertEquals(
				"org.yat.$Root.A.B.C.$Prop",
				NameUtil.getCanonicalPropertyName("org.yat.$Root", "$Prop", "A", "B", "C")
		);
	}
	
	//
	//Test names w/ Lists for inner path
	
	@Test
	public void testGetCanonicalPropertyName_List_PropertyWithDollarSignInName() {
		assertEquals(
				"org.yat.$Root.$Prop",
				NameUtil.getCanonicalPropertyName("org.yat.$Root", "$Prop", list())
		);
	}
	
	@Test
	public void testGetCanonicalPropertyName_List_NestedProperty() {
		assertEquals(
				"org.yat.Root.A.B.C.Prop",
				NameUtil.getCanonicalPropertyName("org.yat.Root", "Prop", list("A", "B", "C"))
		);
	}
	
	@Test
	public void testGetCanonicalPropertyName_List_NestedPropertyWithDollarNames() {
		assertEquals(
				"org.yat.$Root.A.B.C.$Prop",
				NameUtil.getCanonicalPropertyName("org.yat.$Root", "$Prop", list("A", "B", "C"))
		);
	}

	/**
	 * Test of getJavaCanonicalParentName method, of class NameUtil.
	 */
	@Test
	public void testGetJavaCanonicalParentName_StringArr() {
		
		assertEquals(
				"a.b.c.MyClass$a$b$c",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", new String[] {"a", "b", "c"})
		);
		
		assertEquals(
				"MyClass$a$b$c",
				NameUtil.getJavaCanonicalParentName("MyClass", new String[] {"a", "b", "c"})
		);
		
		assertEquals(
				"a.b.c.MyClass",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", new String[] {})
		);
		
		assertEquals(
				"a.b.c.MyClass",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", (String[]) null)
		);
		
		assertEquals(
				"a.b.c.MyClass$$a$b$$c",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", new String[] {"$a", "b", "$c"})
		);
	}

	/**
	 * Test of getJavaCanonicalParentName method, of class NameUtil.
	 */
	@Test
	public void testGetJavaCanonicalParentName_StringList() {
		assertEquals(
				"a.b.c.MyClass$a$b$c",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", list("a", "b", "c"))
		);
		
		assertEquals(
				"MyClass$a$b$c",
				NameUtil.getJavaCanonicalParentName("MyClass", list("a", "b", "c"))
		);
		
		assertEquals(
				"a.b.c.MyClass",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", list())
		);
		
		assertEquals(
				"a.b.c.MyClass",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", (List) null)
		);
		
		assertEquals(
				"a.b.c.MyClass$$a$b$$c",
				NameUtil.getJavaCanonicalParentName("a.b.c.MyClass", list("$a", "b", "$c"))
		);
	}
	
	private List<String> list(String... array) {
		return Arrays.asList(array);
	}
}
