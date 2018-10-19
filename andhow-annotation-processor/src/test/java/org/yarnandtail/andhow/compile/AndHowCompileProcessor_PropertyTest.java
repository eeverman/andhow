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
import org.junit.Before;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
 * @author ericeverman
 */
public class AndHowCompileProcessor_PropertyTest {

	/** Classpath of the generated service file for AndHow property registration */
	static final String PROPERTY_REGISTRAR_CLASSPATH = 
			"/META-INF/services/org.yarnandtail.andhow.service.PropertyRegistrar";
	
	/** Shortcut to this package */
	static final String pkg = AndHowCompileProcessor_PropertyTest.class.getPackage().getName();

	JavaCompiler compiler;
	MemoryFileManager manager;
	TestClassLoader loader;
	
	@Before
	public void setupTest() {
		compiler = ToolProvider.getSystemJavaCompiler();
		manager = new MemoryFileManager(compiler);
		loader = new TestClassLoader(manager);
	}
	
	/**
	 * The source path to where to find this file on the classpath
	 * @param classPackage
	 * @param simpleClassName
	 * @return 
	 */
	public String srcPath(String classPackage, String simpleClassName) {
		return "/" + classPackage.replace(".", "/") + "/" + simpleClassName + ".java";
	}
	
	/**
	 * Build the canonical name of the generated class from the source class.
	 * 
	 * @param classPackage
	 * @param simpleClassName
	 * @return 
	 */
	public String genName(String classPackage, String simpleClassName) {
		return classPackage + ".$" + simpleClassName + "_AndHowProps";
	}
	
	/**
	 * Full canonical name of the class.
	 * @param classPackage
	 * @param simpleClassName
	 * @return 
	 */
	public String fullName(String classPackage, String simpleClassName) {
		return classPackage + "." + simpleClassName;
	}
	
	/**
	 * Builds a new TestSource object for a Java source file on the classpath
	 * @param classPackage
	 * @param simpleClassName
	 * @return
	 * @throws Exception 
	 */
	public TestSource buidTestSource(String classPackage, String simpleClassName) throws Exception {
		String classContent = IOUtil.getUTF8ResourceAsString(srcPath(pkg, simpleClassName));
        return new TestSource(fullName(pkg, simpleClassName), JavaFileObject.Kind.SOURCE, classContent);
	}
		
    @Test
    public void testComplexNestedPropertySampleClass() throws Exception {
		
		String classSimpleName = "PropertySample";

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
  
        Set<TestSource> input = new HashSet();
        input.add(buidTestSource(pkg, classSimpleName));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
        Object genClass = loader.loadClass(genName(pkg, classSimpleName)).newInstance();
		String genSvsFile = IOUtil.toString(
				loader.getResourceAsStream(PROPERTY_REGISTRAR_CLASSPATH), Charset.forName("UTF-8"));
        
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
  
  
        Set<TestSource> input = new HashSet();
        input.add(buidTestSource(pkg, classSimpleName));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
        Object genClass = loader.loadClass(genName(pkg, classSimpleName)).newInstance();
		String genSvsFile = IOUtil.toString(
				loader.getResourceAsStream(PROPERTY_REGISTRAR_CLASSPATH), Charset.forName("UTF-8"));
        
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
		
		String classSimpleName = "BadProps_1";

		List<String> options=new ArrayList();
		//options.add("-verbose");
  
  
        Set<TestSource> input = new HashSet();
        input.add(buidTestSource(pkg, classSimpleName));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		
		try {
			task.call();
			fail("This should have thrown an exception");
		} catch (RuntimeException e) {
			//add some testing here - would be good to have a custom exceptoin
			//so we can get details from it.
		}
        
    }
	

}
