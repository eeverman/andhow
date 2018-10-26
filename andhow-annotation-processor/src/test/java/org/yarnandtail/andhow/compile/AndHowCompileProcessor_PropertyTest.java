package org.yarnandtail.andhow.compile;

import org.yarnandtail.compile.*;
import org.yarnandtail.andhow.service.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.tools.*;
import org.junit.Test;
import org.yarnandtail.andhow.util.IOUtil;
import static org.yarnandtail.andhow.compile.CompileProblem.*;

import static org.junit.Assert.*;
import org.junit.Before;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
 * @author ericeverman
 */
public class AndHowCompileProcessor_PropertyTest extends AndHowCompileProcessorTestBase {
	
	/** Shortcut to this package */
	static final String pkg = AndHowCompileProcessor_PropertyTest.class.getPackage().getName();
	

		
    @Test
    public void testComplexNestedPropertySampleClass() throws Exception {
		
		String classSimpleName = "PropertySample";

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
        sources.add(buildTestSource(pkg, classSimpleName));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, sources);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
        Object genClass = loader.loadClass(genName(pkg, classSimpleName)).newInstance();
		String genSvsFile = IOUtil.toString(
				loader.getResourceAsStream(REGISTRAR_SVS_PATH), Charset.forName("UTF-8"));
        
        assertNotNull(genClass);
		
		PropertyRegistrar registrar = (PropertyRegistrar)genClass;
		
		//final String ROOT = "org.yarnandtail.andhow.compile.PropertySample";
		
		assertEquals(fullName(pkg, classSimpleName), registrar.getRootCanonicalName());
		List<PropertyRegistration> propRegs = registrar.getRegistrationList();
		
		assertEquals(fullName(pkg, classSimpleName) + ".STRING", propRegs.get(0).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".STRING_PUB", propRegs.get(1).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PC.STRING", propRegs.get(2).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PC.STRING_PUB", propRegs.get(3).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PC.PC_PC.STRING", propRegs.get(4).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PC.PC_PI.STRING", propRegs.get(5).getCanonicalPropertyName());
		
		assertEquals(fullName(pkg, classSimpleName) + ".PI.STRING", propRegs.get(6).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PI.PI_DC.STRING", propRegs.get(7).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PI.PI_DC.STRING_PUB", propRegs.get(8).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PI.PI_DI.STRING", propRegs.get(9).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".PI.PI_DI.STRING_PUB", propRegs.get(10).getCanonicalPropertyName());
		
		//
		//Test the registration file
		assertNotNull(genSvsFile);
		assertEquals(genName(pkg, classSimpleName), genSvsFile.trim());
    }
	

    @Test
    public void testSimpleHappyPathClass() throws Exception {
		
		String classSimpleName = "HappyPathProps";

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
        sources.add(buildTestSource(pkg, classSimpleName));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, sources);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
        Object genClass = loader.loadClass(genName(pkg, classSimpleName)).newInstance();
		String genSvsFile = IOUtil.toString(
				loader.getResourceAsStream(REGISTRAR_SVS_PATH), Charset.forName("UTF-8"));
        
        assertNotNull(genClass);
		
		PropertyRegistrar registrar = (PropertyRegistrar)genClass;
		
		//final String ROOT = "org.yarnandtail.andhow.compile.PropertySample";
		
		assertEquals(fullName(pkg, classSimpleName), registrar.getRootCanonicalName());
		List<PropertyRegistration> propRegs = registrar.getRegistrationList();
		
		assertEquals(fullName(pkg, classSimpleName) + ".STR_1", propRegs.get(0).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".STR_2", propRegs.get(1).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".INNER.STR_1", propRegs.get(2).getCanonicalPropertyName());
		assertEquals(fullName(pkg, classSimpleName) + ".INNER.STR_2", propRegs.get(3).getCanonicalPropertyName());
		
		//
		//Test the registration file
		assertNotNull(genSvsFile);
		assertEquals(genName(pkg, classSimpleName), genSvsFile.trim());
    }
	

    @Test
    public void testMissingStaticAndFinalModifiersOnProperties() throws Exception {
		
		List<String> options=new ArrayList();
		//options.add("-verbose");
  
        sources.add(buildTestSource(pkg, "BadProps_1"));
		sources.add(buildTestSource(pkg, "BadProps_2"));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, sources);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		
		try {
			task.call();
			fail("This should have thrown an exception");
		} catch (RuntimeException e) {
			
			assertTrue(e.getCause() instanceof AndHowCompileException);
			
			AndHowCompileException ace = (AndHowCompileException) e.getCause();
			
			String CLASSNAME = "org.yarnandtail.andhow.compile.BadProps_1";
			String INNER_CLASSNAME = CLASSNAME + "$INNER_CLASS";
			
			//Expected CompileProblems
			//All problems for both classes should be reported in one exception
			CompileProblem prob1 = new PropMissingFinal(CLASSNAME, "STR_1");
			CompileProblem prob2 = new PropMissingStatic(CLASSNAME, "STR_2");
			CompileProblem prob3 = new PropMissingStaticFinal(CLASSNAME, "STR_3");
			CompileProblem prob4 = new PropMissingFinal(INNER_CLASSNAME, "STR_1");
			CompileProblem prob5 = new PropMissingStatic(INNER_CLASSNAME, "STR_2");
			CompileProblem prob6 = new PropMissingStaticFinal(INNER_CLASSNAME, "STR_3");
			
			//In 2nd class
			CompileProblem prob7 = new PropMissingFinal(
					"org.yarnandtail.andhow.compile.BadProps_2", "STR_1");
			
			assertEquals(7, ace.getProblems().size());
			assertTrue(ace.getProblems().contains(prob1));
			assertTrue(ace.getProblems().contains(prob2));
			assertTrue(ace.getProblems().contains(prob3));
			assertTrue(ace.getProblems().contains(prob4));
			assertTrue(ace.getProblems().contains(prob5));
			assertTrue(ace.getProblems().contains(prob6));
			assertTrue(ace.getProblems().contains(prob7));
			
		} catch (Throwable t) {
			fail("This should have thrown an AndHowCompileException");
		}
        
    }
	

}
