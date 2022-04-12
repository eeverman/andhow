package org.yarnandtail.andhow.compile;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.compile.CompileProblem.PropMissingFinal;
import org.yarnandtail.andhow.compile.CompileProblem.PropMissingStatic;
import org.yarnandtail.andhow.compile.CompileProblem.PropMissingStaticFinal;
import org.yarnandtail.andhow.service.PropertyRegistrar;
import org.yarnandtail.andhow.service.PropertyRegistration;
import org.yarnandtail.andhow.util.IOUtil;
import org.yarnandtail.compile.AndHowCompileProcessorTestBase;

import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * A lot of this code was borrowed from here:
 * https://gist.github.com/johncarl81/46306590cbdde5a3003f
 */
public class AndHowCompileProcessor_PropertyTest extends AndHowCompileProcessorTestBase {

	/**
	 * Shortcut to this package
	 */
	static final String pkg = AndHowCompileProcessor_PropertyTest.class.getPackage().getName();


	@Test
	public void testComplexNestedPropertySampleClass() throws Exception {

		String classSimpleName = "PropertySample";

		List<String> options = new ArrayList();
		//options.add("-verbose");

		sources.add(buildTestSource(pkg, classSimpleName));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		Object genClass = loader.loadClass(genName(pkg, classSimpleName)).getDeclaredConstructor().newInstance();
		String genSvsFile = IOUtil.toString(
				loader.getResourceAsStream(REGISTRAR_SVS_PATH), Charset.forName("UTF-8"));

		assertEquals(0,
				diagnostics.getDiagnostics().stream().filter(
						d -> isError(d)
				).count(),
				"Should be no warn/errors");

		assertNotNull(genClass);

		PropertyRegistrar registrar = (PropertyRegistrar) genClass;

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

		List<String> options = new ArrayList();
		//options.add("-verbose");

		sources.add(buildTestSource(pkg, classSimpleName));

		JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, sources);
		task.setProcessors(Collections.singleton(new AndHowCompileProcessor()));
		task.call();

		Object genClass = loader.loadClass(genName(pkg, classSimpleName)).getDeclaredConstructor().newInstance();
		String genSvsFile = IOUtil.toString(
				loader.getResourceAsStream(REGISTRAR_SVS_PATH), Charset.forName("UTF-8"));

		assertEquals(0,
				diagnostics.getDiagnostics().stream().filter(
						d -> isError(d)
				).count(),
				"Should be no warn/errors");

		assertNotNull(genClass);

		PropertyRegistrar registrar = (PropertyRegistrar) genClass;

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

		List<String> options = new ArrayList();
		//options.add("-verbose");

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

		assertEquals(9 , diags.size());

		assertEquals(Diagnostic.Kind.NOTE, diags.get(0).getKind());
		assertThat(diags.get(0).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: Found java source version:"));

		assertEquals(Diagnostic.Kind.ERROR, diags.get(1).getKind());
		assertThat(diags.get(1).getMessage(Locale.getDefault()), startsWith("AndHowCompileProcessor: " +
				"AndHow Property definition or Init class errors prevented compilation. Each of the following (7) errors must be fixed"));

		//More general assertions about the 7 errors - create a list w/ the first two diagnostics removed
		List<Diagnostic<? extends JavaFileObject>> diagErrs = new ArrayList<>();
		diagErrs.addAll(diags);
		diagErrs.remove(diags.get(0));
		diagErrs.remove(diags.get(1));

		assertEquals(7, diagErrs.stream().filter(d -> d.getKind().equals(Diagnostic.Kind.ERROR)).count());
		assertEquals(7,
				diagErrs.stream().filter(d -> d.getMessage(Locale.getDefault()).matches(
				".+The AndHow Property '.+' in the class 'org.*' must be declared as.*")).count());

	}


}
