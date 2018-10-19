package org.yarnandtail.andhow.compile;

import org.yarnandtail.compile.*;
import org.yarnandtail.andhow.service.PropertyRegistrar;
import org.yarnandtail.andhow.service.PropertyRegistration;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.tools.*;
import org.junit.Test;
import org.yarnandtail.andhow.util.IOUtil;

import static org.junit.Assert.*;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
 * @author ericeverman
 */
public class AndHowCompileProcessor_InitTest {

	//
	//Names of classes in the resources directory, used for compile testing of
	//initiation self discovery.  The 'A' class extends the 'Abstract' one for each.
	protected static final String AndHowInitAbstract_NAME = "org.yarnandtail.andhow.compile.AndHowInitAbstract";
	protected static final String AndHowInitA_NAME = "org.yarnandtail.andhow.compile.AndHowInitA";
	protected static final String AndHowInitB_NAME = "org.yarnandtail.andhow.compile.AndHowInitB";
	protected static final String AndHowTestInitAbstract_NAME = "org.yarnandtail.andhow.compile.AndHowTestInitAbstract";
	protected static final String AndHowTestInitA_NAME = "org.yarnandtail.andhow.compile.AndHowTestInitA";
	protected static final String AndHowTestInitB_NAME = "org.yarnandtail.andhow.compile.AndHowTestInitB";
	

    @Test
    public void testServiceRegistrationOfOneProdAndOneTestInit() throws Exception {
		
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final MemoryFileManager manager = new MemoryFileManager(compiler);
		TestClassLoader loader = new TestClassLoader(manager);

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
  
        Set<TestSource> input = new HashSet();
		input.add(new TestSource(AndHowInitAbstract_NAME));	//abstract should be ignored
        input.add(new TestSource(AndHowInitA_NAME));
		input.add(new TestSource(AndHowTestInitAbstract_NAME));
		input.add(new TestSource(AndHowTestInitA_NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowInit"), Charset.forName("UTF-8"));
   		String testInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowTestInit"), Charset.forName("UTF-8"));
     

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(AndHowInitA_NAME, prodInitSvs.trim());
		assertNotNull(testInitSvs);
		assertEquals(AndHowTestInitA_NAME, testInitSvs.trim());
    }
	

    @Test
    public void testServiceRegistrationOfOneProdInit() throws Exception {
		
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final MemoryFileManager manager = new MemoryFileManager(compiler);
		TestClassLoader loader = new TestClassLoader(manager);

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
  
        Set<TestSource> input = new HashSet();
		input.add(new TestSource(AndHowInitAbstract_NAME));
        input.add(new TestSource(AndHowInitA_NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowInit"), Charset.forName("UTF-8"));     

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(AndHowInitA_NAME, prodInitSvs.trim());
    }
	
    @Test
    public void testServiceRegistrationOfOneTestInit() throws Exception {
		
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final MemoryFileManager manager = new MemoryFileManager(compiler);
		TestClassLoader loader = new TestClassLoader(manager);

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
  
        Set<TestSource> input = new HashSet();
		input.add(new TestSource(AndHowTestInitAbstract_NAME));
		input.add(new TestSource(AndHowTestInitA_NAME));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
   		String testInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowTestInit"), Charset.forName("UTF-8"));
     

		//
		//Test the initiation files
		assertNotNull(testInitSvs);
		assertEquals(AndHowTestInitA_NAME, testInitSvs.trim());
    }
	

    @Test
    public void testServiceRegistrationOfAndHowInitWithTooManyProdInstances() throws Exception {
		
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			final MemoryFileManager manager = new MemoryFileManager(compiler);
			TestClassLoader loader = new TestClassLoader(manager);

			List<String> options=new ArrayList();
			//options.add("-verbose");


			Set<TestSource> input = new HashSet();
			input.add(new TestSource(AndHowInitAbstract_NAME));
			input.add(new TestSource(AndHowInitA_NAME));
			input.add(new TestSource(AndHowInitB_NAME));

			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();

		} catch (RuntimeException e) {
			TooManyInitClassesException tmi = null;
			
			if (e instanceof TooManyInitClassesException) {
				tmi = (TooManyInitClassesException)e;
			} else if (e.getCause() != null && e.getCause() instanceof TooManyInitClassesException) {
				tmi = (TooManyInitClassesException) e.getCause();
			}
			
			if (tmi != null) {
				assertEquals(2, tmi.getInstanceNames().size());
				assertTrue(tmi.getInstanceNames().contains(AndHowInitA_NAME));
				assertTrue(tmi.getInstanceNames().contains(AndHowInitB_NAME));
			} else {
				fail("Expecting the exception to be TooManyInitClassesException or caused by it.");
			}
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


			Set<TestSource> input = new HashSet();
			input.add(new TestSource(AndHowTestInitAbstract_NAME));
			input.add(new TestSource(AndHowTestInitA_NAME));
			input.add(new TestSource(AndHowTestInitB_NAME));

			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();

		} catch (RuntimeException e) {
			TooManyInitClassesException tmi = null;
			
			if (e instanceof TooManyInitClassesException) {
				tmi = (TooManyInitClassesException)e;
			} else if (e.getCause() != null && e.getCause() instanceof TooManyInitClassesException) {
				tmi = (TooManyInitClassesException) e.getCause();
			}
			
			if (tmi != null) {
				assertEquals(2, tmi.getInstanceNames().size());
				assertTrue(tmi.getInstanceNames().contains(AndHowTestInitA_NAME));
				assertTrue(tmi.getInstanceNames().contains(AndHowTestInitB_NAME));
			} else {
				fail("Expecting the exception to be TooManyInitClassesException or caused by it.");
			}
		}	
    }
	
}
