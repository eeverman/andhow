package org.yarnandtail.andhow.compile;

import org.yarnandtail.andhow.compile.GlobalPropertyGroupStub;
import org.yarnandtail.andhow.compile.AndHowCompileProcessor;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.yarnandtail.andhow.compile.AndHowCompileProcessor.*;

/**
 *
 * @author ericeverman
 */
public class AndHowCompileProcessorTest {
	
	public AndHowCompileProcessorTest() {
	}

	/**
	 * Test of process method, of class AndHowCompileProcessor.
	 */
	@Test
	public void testGenerateClassString() {
		
		final String myPkg = "my.pkg";
		final String myClass = "MyClass";
		final String myInterface = "MyInterface";
		
		ProxyNameModel pnm = new ProxyNameModel(myPkg);
		pnm.add(myInterface);
		pnm.add(myClass);
		
		
		AndHowCompileProcessor acp = new AndHowCompileProcessor();
		
		String actual = acp.generateClassString(myPkg, pnm.getClassName(), myClass);
		System.out.println(actual);
		
		assertTrue(actual.contains("package " + myPkg));
		assertTrue(actual.contains("value=\"org.yarnandtail.andhow.compile.AndHowCompileProcessor\","));
		assertTrue(actual.contains("comments=\"Proxy for " + myPkg + "." + myClass));
		assertTrue(actual.contains("public class " + pnm.getClassName()));
		assertTrue(actual.contains("extends " + GlobalPropertyGroupStub.class.getCanonicalName()));
	}

	
	@Test
	public void testProxyNameModel() {
		ProxyNameModel pnm = new ProxyNameModel("a.b.c");
		
		//From leafy to top level
		pnm.add("MyConfig");
		pnm.add("MyInnerClass");
		pnm.add("MyClass");
		
		
		assertEquals("$GlobalPropGrpStub$MyClass$MyInnerClass$MyConfig", pnm.getClassName());
		assertEquals("a.b.c.$GlobalPropGrpStub$MyClass$MyInnerClass$MyConfig", pnm.getQualifiedName());
	}
	
}
