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
public class AndHowCompileProcessorTest {


    @Test
    public void testCompileAnnotationProcessorOutput() throws Exception {
		
		final String CLASS_PACKAGE = "org.yarnandtail.andhow.compile";
		final String CLASS_NAME = CLASS_PACKAGE + ".PropertySample";
		final String CLASS_SOURCE_PATH = "/" + CLASS_NAME.replace(".", "/") + ".java";
		final String GEN_CLASS_NAME = CLASS_PACKAGE + ".$PropertySample_AndHowProps";
		
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final MemoryFileManager manager = new MemoryFileManager(compiler);
		TestClassLoader loader = new TestClassLoader(manager);

		List<String> options=new ArrayList();
		options.add("-verbose");
  
  
        Set<TestSource> input = new HashSet();
		String classContent = IOUtil.getUTF8ResourceAsString(CLASS_SOURCE_PATH);
        input.add(new TestSource(CLASS_NAME, JavaFileObject.Kind.SOURCE, classContent));

        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, options, null, input);
        task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
        task.call();
        
        Object genClass = loader.loadClass(GEN_CLASS_NAME).newInstance();
		String genSvsFile = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.service.PropertyRegistrar"), Charset.forName("UTF-8"));
        
        assertNotNull(genClass);
		
		PropertyRegistrar registrar = (PropertyRegistrar)genClass;
		
		//final String ROOT = "org.yarnandtail.andhow.compile.PropertySample";
		
		assertEquals(CLASS_NAME, registrar.getRootCanonicalName());
		List<PropertyRegistration> propRegs = registrar.getRegistrationList();
		
		assertEquals(CLASS_NAME + ".STRING", propRegs.get(0).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".STRING_PUB", propRegs.get(1).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PC.STRING", propRegs.get(2).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PC.STRING_PUB", propRegs.get(3).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PC.PC_PC.STRING", propRegs.get(4).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PC.PC_PI.STRING", propRegs.get(5).getCanonicalPropertyName());
		
		assertEquals(CLASS_NAME + ".PI.STRING", propRegs.get(6).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PI.PI_DC.STRING", propRegs.get(7).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PI.PI_DC.STRING_PUB", propRegs.get(8).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PI.PI_DI.STRING", propRegs.get(9).getCanonicalPropertyName());
		assertEquals(CLASS_NAME + ".PI.PI_DI.STRING_PUB", propRegs.get(10).getCanonicalPropertyName());
		
		//
		//Test the registration file
		assertNotNull(genSvsFile);
		assertEquals(GEN_CLASS_NAME, genSvsFile.trim());
    }

}
