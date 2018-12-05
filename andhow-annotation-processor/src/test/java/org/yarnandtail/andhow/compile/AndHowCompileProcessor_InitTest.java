package org.yarnandtail.andhow.compile;

import org.yarnandtail.compile.*;
import static org.yarnandtail.andhow.compile.CompileProblem.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.tools.*;
import javax.tools.Diagnostic.Kind;
import org.junit.Test;
import org.yarnandtail.andhow.util.IOUtil;

import static org.junit.Assert.*;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
 * @author ericeverman
 */
public class AndHowCompileProcessor_InitTest extends AndHowCompileProcessorTestBase {

	static final String pkg = AndHowCompileProcessor_PropertyTest.class.getPackage().getName();
	
	//
	//Names of classes in the resources directory, used for compile testing of
	//initiation self discovery.  The 'A' class extends the 'Abstract' one for each.
	protected static final String AndHowInitAbstract_NAME = "AndHowInitAbstract";
	protected static final String AndHowInitA_NAME = "AndHowInitA";
	protected static final String AndHowInitB_NAME = "AndHowInitB";
	protected static final String AndHowTestInitAbstract_NAME = "AndHowTestInitAbstract";
	protected static final String AndHowTestInitA_NAME = "AndHowTestInitA";
	protected static final String AndHowTestInitB_NAME = "AndHowTestInitB";

    @Test
    public void testServiceRegistrationOfOneProdAndOneTestInit() throws Exception {

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
		sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME)); //Abstract should be igored
		sources.add(buildTestSource(pkg, AndHowInitA_NAME));
		sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
		sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream(INIT_SVS_PATH), Charset.forName("UTF-8"));
   		String testInitSvs = IOUtil.toString(loader.getResourceAsStream(TEST_INIT_SVS_PATH), Charset.forName("UTF-8"));
     
		assertEquals("Should be no warn/errors", 0, diagnostics.getDiagnostics().stream().filter(
				d -> d.getKind().equals(Kind.ERROR) || d.getKind().equals(Kind.WARNING)).count());

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(fullName(pkg, AndHowInitA_NAME), prodInitSvs.trim());
		assertNotNull(testInitSvs);
		assertEquals(fullName(pkg, AndHowTestInitA_NAME), testInitSvs.trim());
    }
	

    @Test
    public void testServiceRegistrationOfOneProdInit() throws Exception {

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
		sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME));
		sources.add(buildTestSource(pkg, AndHowInitA_NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream(INIT_SVS_PATH), Charset.forName("UTF-8"));     

		assertEquals("Should be no warn/errors", 0, diagnostics.getDiagnostics().stream().filter(
				d -> d.getKind().equals(Kind.ERROR) || d.getKind().equals(Kind.WARNING)).count());

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(fullName(pkg, AndHowInitA_NAME), prodInitSvs.trim());
    }
	
    @Test
    public void testServiceRegistrationOfOneTestInit() throws Exception {

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
		sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
		sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
   		String testInitSvs = IOUtil.toString(loader.getResourceAsStream(TEST_INIT_SVS_PATH), Charset.forName("UTF-8"));
     
		assertEquals("Should be no warn/errors", 0, diagnostics.getDiagnostics().stream().filter(
				d -> d.getKind().equals(Kind.ERROR) || d.getKind().equals(Kind.WARNING)).count());
		

		//
		//Test the initiation files
		assertNotNull(testInitSvs);
		assertEquals(fullName(pkg, AndHowTestInitA_NAME), testInitSvs.trim());
    }
	

    @Test
    public void testServiceRegistrationOfAndHowInitWithTooManyProdInstances() throws Exception {
		
		try {

			List<String> options=new ArrayList();
			//options.add("-verbose");

			sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowInitB_NAME));

			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, sources);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();
			
			fail("Should have thrown an exception");

		} catch (RuntimeException e) {
			
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof AndHowCompileException);
			
			AndHowCompileException ce = (AndHowCompileException) e.getCause();
			
			assertEquals(1, ce.getProblems().size());
			assertTrue(ce.getProblems().get(0) instanceof TooManyInitClasses);
			
			TooManyInitClasses tmi = (TooManyInitClasses) ce.getProblems().get(0);
			
			assertEquals(2, tmi.getInstanceNames().size());
			assertTrue(tmi.getInstanceNames().contains(fullName(pkg, AndHowInitA_NAME)));
			assertTrue(tmi.getInstanceNames().contains(fullName(pkg, AndHowInitB_NAME)));
		}	
    }
	

    @Test
    public void testServiceRegistrationOfAndHowInitWithTooManyTestInstances() throws Exception {
		
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			final MemoryFileManager manager = new MemoryFileManager(compiler);
			TestClassLoader loader = new TestClassLoader(manager);

			List<String> options=new ArrayList();
			//options.add("-verbose");

			sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitB_NAME));

			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, sources);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();

			fail("Should have thrown an exception");
			
		} catch (RuntimeException e) {
			
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof AndHowCompileException);
			
			AndHowCompileException ce = (AndHowCompileException) e.getCause();
			
			assertEquals(1, ce.getProblems().size());
			assertTrue(ce.getProblems().get(0) instanceof TooManyInitClasses);
			
			TooManyInitClasses tmi = (TooManyInitClasses) ce.getProblems().get(0);
			
			assertEquals(2, tmi.getInstanceNames().size());
			assertTrue(tmi.getInstanceNames().contains(fullName(pkg, AndHowTestInitA_NAME)));
			assertTrue(tmi.getInstanceNames().contains(fullName(pkg, AndHowTestInitB_NAME)));

		}	
    }
	

    @Test
    public void testServiceRegistrationOfAndHowInitWithTooManyInstAndBadProperties() throws Exception {
		
		try {

			List<String> options=new ArrayList();
			//options.add("-verbose");

			sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowInitB_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitB_NAME));
			sources.add(buildTestSource(pkg, "BadProps_1"));
			sources.add(buildTestSource(pkg, "BadProps_2"));
		
			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, sources);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();
			
			fail("Should have thrown an exception");

		} catch (RuntimeException e) {
			
			assertNotNull(e.getCause());
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
			

			assertEquals(9, ace.getProblems().size());
			assertEquals(2,
					ace.getProblems().stream().filter(i -> i instanceof TooManyInitClasses).count()
			);
			assertTrue(ace.getProblems().contains(prob1));
			assertTrue(ace.getProblems().contains(prob2));
			assertTrue(ace.getProblems().contains(prob3));
			assertTrue(ace.getProblems().contains(prob4));
			assertTrue(ace.getProblems().contains(prob5));
			assertTrue(ace.getProblems().contains(prob6));
			assertTrue(ace.getProblems().contains(prob7));
		}	
    }
	
}
