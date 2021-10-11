package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.util.IOUtil;
import org.yarnandtail.compile.*;

import javax.tools.*;
import java.nio.charset.Charset;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
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

		List<String> options = new ArrayList();
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

		assertEquals(0,
				diagnostics.getDiagnostics().stream().filter(
						d -> isError(d)
				).count(),
				"Should be no warn/errors");

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(fullName(pkg, AndHowInitA_NAME), prodInitSvs.trim());
		assertNotNull(testInitSvs);
		assertEquals(fullName(pkg, AndHowTestInitA_NAME), testInitSvs.trim());
	}


	@Test
	public void testServiceRegistrationOfOneProdInit() throws Exception {

		List<String> options = new ArrayList();
		//options.add("-verbose");

		sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME));
		sources.add(buildTestSource(pkg, AndHowInitA_NAME));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		String prodInitSvs = IOUtil.toString(loader.getResourceAsStream(INIT_SVS_PATH), Charset.forName("UTF-8"));

		assertEquals(0,
				diagnostics.getDiagnostics().stream().filter(
						d -> isError(d)
				).count(),
				"Should be no warn/errors");

		//
		//Test the initiation files
		assertNotNull(prodInitSvs);
		assertEquals(fullName(pkg, AndHowInitA_NAME), prodInitSvs.trim());
	}

	@Test
	public void testServiceRegistrationOfOneTestInit() throws Exception {

		List<String> options = new ArrayList();
		//options.add("-verbose");

		sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
		sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		String testInitSvs = IOUtil.toString(loader.getResourceAsStream(TEST_INIT_SVS_PATH), Charset.forName("UTF-8"));

		assertEquals(0,
				diagnostics.getDiagnostics().stream().filter(
						d -> isError(d)
				).count(),
				"Should be no warn/errors");


		//
		//Test the initiation files
		assertNotNull(testInitSvs);
		assertEquals(fullName(pkg, AndHowTestInitA_NAME), testInitSvs.trim());
	}

	@Test
	public void testServiceRegistrationWithTooManyInitInstances() throws Exception {

			List<String> options = new ArrayList();
			//options.add("-verbose");

			sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowInitB_NAME));

			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();


		List<Diagnostic<? extends JavaFileObject>> diags = diagnostics.getDiagnostics();

		// uncomment to see the diagnostic messages
//		for (Diagnostic<?> d : diags) {
//			System.out.println(d.toString());
//		}

		assertEquals(3 , diags.size());

		assertEquals(Diagnostic.Kind.NOTE, diags.get(0).getKind());
		assertThat(diags.get(0).getMessage(Locale.getDefault()), startsWith(
				"AndHowCompileProcessor: Found java source version:"));

		assertEquals(Diagnostic.Kind.ERROR, diags.get(1).getKind());
		assertThat(diags.get(1).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: " +
				"AndHow Property definition or Init class errors prevented compilation. Each of the following (1) errors must be fixed"));

		assertEquals(Diagnostic.Kind.ERROR, diags.get(2).getKind());
		assertThat(diags.get(2).getMessage(Locale.getDefault()), matchesRegex("AndHowCompileProcessor: " +
				"Multiple \\(2\\) implementations of org\\.yarnandtail\\.andhow\\.AndHowInit were found, " +
				"but only one is allowed\\.  Implementations found: org\\.yarnandtail\\.andhow\\.compile\\.AndHowInit[AB], " +
				"org\\.yarnandtail\\.andhow\\.compile\\.AndHowInit[AB]"));

	}


	@Test
	public void testServiceRegistrationWithTooManyTestInstances() throws Exception {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		final MemoryFileManager manager = new MemoryFileManager(compiler);
		TestClassLoader loader = new TestClassLoader(manager);

		List<String> options = new ArrayList();
		//options.add("-verbose");

		sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
		sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));
		sources.add(buildTestSource(pkg, AndHowTestInitB_NAME));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		List<Diagnostic<? extends JavaFileObject>> diags = diagnostics.getDiagnostics();

		// uncomment to see the diagnostic messages
//		for (Diagnostic<?> d : diags) {
//			System.out.println(d.toString());
//		}

		assertEquals(3 , diags.size());

		assertEquals(Diagnostic.Kind.NOTE, diags.get(0).getKind());
		assertThat(diags.get(0).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: Found java source version:"));

		assertEquals(Diagnostic.Kind.ERROR, diags.get(1).getKind());
		assertThat(diags.get(1).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: " +
				"AndHow Property definition or Init class errors prevented compilation. Each of the following (1) errors must be fixed"));

		assertEquals(Diagnostic.Kind.ERROR, diags.get(2).getKind());

		//The order of listed classes is not stable, so regex allows them to be in any order
		assertThat(diags.get(2).getMessage(Locale.getDefault()), matchesRegex("AndHowCompileProcessor: " +
				"Multiple \\(2\\) implementations of org.yarnandtail.andhow.AndHowTestInit were found, " +
				"but only one is allowed\\.  Implementations found: org\\.yarnandtail\\.andhow\\.compile\\.AndHowTestInit[AB], " +
				"org.yarnandtail.andhow.compile.AndHowTestInit[AB]"));

	}


	@Test
	public void testServiceRegistrationWithTooManyAndNowInitClassesAndBadProperties() throws Exception {

			List<String> options = new ArrayList();
			//options.add("-verbose");

			sources.add(buildTestSource(pkg, AndHowInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowInitB_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitAbstract_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitA_NAME));
			sources.add(buildTestSource(pkg, AndHowTestInitB_NAME));
			sources.add(buildTestSource(pkg, "BadProps_1"));
			sources.add(buildTestSource(pkg, "BadProps_2"));

			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
			task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
			task.call();

		List<Diagnostic<? extends JavaFileObject>> diags = diagnostics.getDiagnostics();

		// uncomment to see the diagnostic messages
//		for (Diagnostic<?> d : diags) {
//			System.out.println(d.toString());
//		}

		assertEquals(11 , diags.size());

		assertEquals(Diagnostic.Kind.NOTE, diags.get(0).getKind());
		assertThat(diags.get(0).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: Found java source version:"));

		assertEquals(Diagnostic.Kind.ERROR, diags.get(1).getKind());
		assertThat(diags.get(1).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: " +
				"AndHow Property definition or Init class errors prevented compilation. Each of the following (9) errors must be fixed"));

		//More general assertions about the 9 errors - create a list w/ the first two diagnostics removed
		List<Diagnostic<? extends JavaFileObject>> diagErrs = new ArrayList<>();
		diagErrs.addAll(diags);
		diagErrs.remove(diags.get(0));
		diagErrs.remove(diags.get(1));

		assertEquals(9,
				diagErrs.stream().filter(d -> d.getKind().equals(Diagnostic.Kind.ERROR)).count(),
				"All should be errors");

		assertEquals(7,
				diagErrs.stream().filter(d -> d.getMessage(Locale.getDefault()).matches(
						".+The AndHow Property '.+' in the class 'org.*' must be declared as.*")).count());

		assertEquals(1,
				diagErrs.stream().filter(d -> d.getMessage(Locale.getDefault()).matches(
						".+Multiple \\(2\\) implementations of org\\.yarnandtail\\.andhow\\.AndHowInit were found.*")).count());

		assertEquals(1,
				diagErrs.stream().filter(d -> d.getMessage(Locale.getDefault()).matches(
						".+Multiple \\(2\\) implementations of org\\.yarnandtail\\.andhow\\.AndHowTestInit were found.*")).count());

	}

}
