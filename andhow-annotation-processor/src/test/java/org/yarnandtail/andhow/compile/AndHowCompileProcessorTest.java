package org.yarnandtail.andhow.compile;

import org.yarnandtail.compile.*;
import org.yarnandtail.andhow.service.PropertyRegistrar;
import org.yarnandtail.andhow.service.PropertyRegistration;
import java.io.*;
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
 *
 * @author ericeverman
 */
public class AndHowCompileProcessorTest {

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
	public void testCompileAnnotationProcessorOutput() throws Exception {

		final String CLASS_PACKAGE = "org.yarnandtail.andhow.compile";
		final String CLASS_NAME = CLASS_PACKAGE + ".PropertySample";
		final String CLASS_SOURCE_PATH = "/" + CLASS_NAME.replace(".", "/") + ".java";
		final String GEN_CLASS_NAME = CLASS_PACKAGE + ".$PropertySample_AndHowProps";

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final MemoryFileManager manager = new MemoryFileManager(compiler);
		final DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector();
		final TestClassLoader loader = new TestClassLoader(manager);

		List<String> options = new ArrayList();
		//options.add("-verbose");

		Set<TestSource> input = new HashSet();
		String classContent = IOUtil.getUTF8ResourceAsString(CLASS_SOURCE_PATH);
		input.add(new TestSource(CLASS_NAME, JavaFileObject.Kind.SOURCE, classContent));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, dc, options, null, input);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		Object genClass = loader.loadClass(GEN_CLASS_NAME).newInstance();
		String genSvsFile = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.service.PropertyRegistrar"), Charset.forName("UTF-8"));

		assertNotNull(genClass);

		PropertyRegistrar registrar = (PropertyRegistrar) genClass;

		//final String ROOT = "org.yarnandtail.andhow.compile.PropertySample";
		assertEquals(CLASS_NAME, registrar.getRootCanonicalName());
		List<PropertyRegistration> propRegs = registrar.getRegistrationList();

		assertEquals("There should be no warn/errs from the compiler", 0, dc.getDiagnostics().size());

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

	@Test
	public void testServiceRegistrationOfOneProdAndOneTestInit() throws Exception {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final MemoryFileManager manager = new MemoryFileManager(compiler);
		final DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector();
		final TestClassLoader loader = new TestClassLoader(manager);

		List<String> options = new ArrayList();
		//options.add("-verbose");

		Set<TestSource> input = new HashSet();
		input.add(new TestSource(AndHowInitAbstract_NAME));	//abstract should be ignored
		input.add(new TestSource(AndHowInitA_NAME));
		input.add(new TestSource(AndHowTestInitAbstract_NAME));
		input.add(new TestSource(AndHowTestInitA_NAME));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, dc, options, null, input);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowInit"), Charset.forName("UTF-8"));
		String testInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowTestInit"), Charset.forName("UTF-8"));

		assertEquals("There should be no warn/errs from the compiler", 0, dc.getDiagnostics().size());

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(AndHowInitA_NAME, prodInitSvs.trim());
		assertNotNull(testInitSvs);
		assertEquals(AndHowTestInitA_NAME, testInitSvs.trim());
	}

	@Test
	public void testServiceRegistrationOfOneProdInit() throws Exception {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final MemoryFileManager manager = new MemoryFileManager(compiler);
		final DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector();
		final TestClassLoader loader = new TestClassLoader(manager);

		List<String> options = new ArrayList();
		//options.add("-verbose");

		Set<TestSource> input = new HashSet();
		input.add(new TestSource(AndHowInitAbstract_NAME));
		input.add(new TestSource(AndHowInitA_NAME));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, dc, options, null, input);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowInit"), Charset.forName("UTF-8"));

		assertEquals("There should be no warn/errs from the compiler", 0, dc.getDiagnostics().size());

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(AndHowInitA_NAME, prodInitSvs.trim());
	}

	@Test
	public void testServiceRegistrationOfOneTestInit() throws Exception {

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final MemoryFileManager manager = new MemoryFileManager(compiler);
		final DiagnosticCollector<JavaFileObject> dc = new DiagnosticCollector();
		final TestClassLoader loader = new TestClassLoader(manager);

		List<String> options = new ArrayList();
		//options.add("-verbose");

		Set<TestSource> input = new HashSet();
		input.add(new TestSource(AndHowTestInitAbstract_NAME));
		input.add(new TestSource(AndHowTestInitA_NAME));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, dc, options, null, input);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		String testInitSvs = IOUtil.toString(loader.getResourceAsStream("/META-INF/services/org.yarnandtail.andhow.AndHowTestInit"), Charset.forName("UTF-8"));

		assertEquals("There should be no warn/errs from the compiler", 0, dc.getDiagnostics().size());

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

			List<String> options = new ArrayList();
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
				tmi = (TooManyInitClassesException) e;
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

			List<String> options = new ArrayList();
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
				tmi = (TooManyInitClassesException) e;
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
