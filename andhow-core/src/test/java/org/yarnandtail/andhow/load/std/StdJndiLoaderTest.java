package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.junit5.EnableJndiUtil;
import org.yarnandtail.andhow.load.util.JndiContextSupplier;
import org.yarnandtail.andhow.load.util.LoaderEnvironmentBuilder;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.sample.JndiLoaderSamplePrinter;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.util.NameUtil;

import javax.naming.Name;
import javax.naming.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StdJndiLoaderTest {


	PropertyConfigurationMutable appDef;
	ValidatedValuesWithContextMutable appValuesBuilder;
	StdJndiLoader loader;

	// Mocks
	Context context;
	JndiContextWrapper wrap;
	LoaderEnvironment le;

	/**
	 * Binds a value to a lookup name for a mocked context.
	 *
	 * This assumes that ony the lookup(String) method is used on the Context, not lookup(Name), though
	 * it would be easy to do both (AndHow only looks up via String).
	 *
	 * @param context A mocked JNDI Context.  Must be previously mocked by Mockito.
	 * @param name The name to bind
	 * @param value The value to bind to the name.
	 * @throws NamingException
	 */
	public static void mockJndiBind(Context context, String name, Object value) throws NamingException {
		Mockito.when(context.lookup(Mockito.eq(name))).thenReturn(value);
	}

	@BeforeEach
	public void init() throws Exception {

		loader = new StdJndiLoader();

		appValuesBuilder = new ValidatedValuesWithContextMutable();

		appDef = new PropertyConfigurationMutable(new CaseInsensitiveNaming());

		GroupProxy proxy = AndHowUtil.buildGroupProxy(SimpleParams.class);

		appDef.addProperty(proxy, SimpleParams.STR_BOB);
		appDef.addProperty(proxy, SimpleParams.STR_NULL);
		appDef.addProperty(proxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(proxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(proxy, SimpleParams.FLAG_NULL);
		appDef.addProperty(proxy, SimpleParams.INT_TEN);
		appDef.addProperty(proxy, SimpleParams.INT_NULL);
		appDef.addProperty(proxy, SimpleParams.LNG_TEN);
		appDef.addProperty(proxy, SimpleParams.LNG_NULL);
		appDef.addProperty(proxy, SimpleParams.LDT_NULL);
		appDef.addProperty(proxy, SimpleParams.LDT_2007_10_01);

		context = Mockito.mock(Context.class);
		wrap = Mockito.mock(JndiContextWrapper.class);
		le = Mockito.mock(LoaderEnvironment.class);

		Mockito.when(wrap.getContext()).thenReturn(context);
		Mockito.when(le.getJndiContext()).thenReturn(wrap);
	}

	@Test
	public void reflexiveValuesReturnExpectedValues() {
		assertTrue(loader instanceof LookupLoader);
		assertEquals("JNDI properties in the system-wide JNDI context", loader.getSpecificLoadDescription());
		assertNull(loader.getLoaderDialect());
		assertEquals("JNDI", loader.getLoaderType());
		assertFalse(loader.isFlaggable());
		assertFalse(loader.isTrimmingRequiredForStringValues());
		assertEquals(StdJndiLoader.CONFIG.class, loader.getClassConfig());
		assertTrue(loader.getConfigSamplePrinter() instanceof JndiLoaderSamplePrinter);
		assertTrue(loader.getInstanceConfig().isEmpty());
		assertFalse(loader.isFailedEnvironmentAProblem());
		loader.releaseResources();	// should cause no error
	}

	@Test
	public void setFailedEnvironmentAProblemWorks() {
		loader.setFailedEnvironmentAProblem(true);
		assertTrue(loader.isFailedEnvironmentAProblem());
		loader.setFailedEnvironmentAProblem(false);
		assertFalse(loader.isFailedEnvironmentAProblem());
	}

	@Test
	public void testSplit() {

		//This is the default
		List<String> result = loader.split("java:comp/env/, \"\",");
		assertEquals(2, result.size());
		assertEquals("java:comp/env/", result.get(0));
		assertEquals("", result.get(1));

		//Should leave prefix completely unmodified (not add a trailing slash)
		result = loader.split(" comp/env , , \"_\",x/y/z");
		assertEquals(3, result.size());
		assertEquals("comp/env", result.get(0));
		assertEquals("_", result.get(1));
		assertEquals("x/y/z", result.get(2));

		//Should be able to indicate an empty string and whitespace w/ double quotes.
		result = loader.split(" comp/env , \"\" , \" \"");
		assertEquals(3, result.size());
		assertEquals("comp/env", result.get(0));
		assertEquals("", result.get(1));
		assertEquals(" ", result.get(2));
	}


	@Test
	public void noProblemIfJndiNotPresentByDefault() throws Exception {

		LoaderEnvironmentBuilder leb = new LoaderEnvironmentBuilder();
		leb.setJndiContextSupplier(new JndiContextSupplier.EmptyJndiContextSupplier());

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void isProblemIfJndiNotPresentAndSetToRequired() throws Exception {

		LoaderEnvironmentBuilder leb = new LoaderEnvironmentBuilder();
		leb.setJndiContextSupplier(new JndiContextSupplier.EmptyJndiContextSupplier());

		loader.setFailedEnvironmentAProblem(true);	// should create a Problem now w/ no JNDI context

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(1, result.getProblems().size());
		assertTrue(result.getProblems().get(0) instanceof LoaderProblem.JndiContextMissing);
	}

	@Test
	public void notAnErrorIfJndiContextReturnsNullForLookup() throws NamingException {

		Mockito.when(context.lookup(Mockito.anyString())).thenReturn(null);
		Mockito.when(context.lookup(Mockito.any(Name.class))).thenReturn(null);

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	/* this duplicates the test of the same name in StdJndiLoaderIT, but ensures that the
	 * unit test results gotten here match the results from the IT test.
	 */
	@Test
	public void testHappyPathFromStringsCompEnvAsURIs() throws Exception {

		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/STR_BOB", "test");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/STR_NULL", "not_null");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/FLAG_TRUE", "false");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/FLAG_FALSE", "true");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/FLAG_NULL", "TRUE");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/INT_TEN", "-999");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/INT_NULL", "999");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/LNG_TEN", "-999");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/LNG_NULL", "999");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/LDT_2007_10_01", "2007-11-02T00:00");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/LDT_NULL", "2007-11-02T00:00");

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);

		assertEquals("test", result.getValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getValue(SimpleParams.STR_NULL));
		assertEquals(false, result.getValue(SimpleParams.FLAG_TRUE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_FALSE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_NULL));
		assertEquals(-999, result.getValue(SimpleParams.INT_TEN));
		assertEquals(999, result.getValue(SimpleParams.INT_NULL));
		assertEquals(-999, result.getValue(SimpleParams.LNG_TEN));
		assertEquals(999, result.getValue(SimpleParams.LNG_NULL));
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), result.getValue(SimpleParams.LDT_2007_10_01));
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), result.getValue(SimpleParams.LDT_NULL));
	}

	@Test
	public void testHappyPathFromStringsCompEnvAsClasspath() throws Exception {

		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.STR_BOB", "test");
		mockJndiBind(context, "java:org.yarnandtail.andhow.SimpleParams.STR_NULL", "not_null");
		mockJndiBind(context, "org.yarnandtail.andhow.SimpleParams.FLAG_TRUE", "false");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.FLAG_FALSE", "true");
		mockJndiBind(context, "java:org.yarnandtail.andhow.SimpleParams.FLAG_NULL", "TRUE");
		mockJndiBind(context, "org.yarnandtail.andhow.SimpleParams.INT_TEN", "-999");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.INT_NULL", "999");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.LNG_TEN", "-999");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.LNG_NULL", "999");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.LDT_2007_10_01", "2007-11-02T00:00");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.LDT_NULL", "2007-11-02T00:00");

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);

		assertEquals("test", result.getValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getValue(SimpleParams.STR_NULL));
		assertEquals(false, result.getValue(SimpleParams.FLAG_TRUE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_FALSE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_NULL));
		assertEquals(-999, result.getValue(SimpleParams.INT_TEN));
		assertEquals(999, result.getValue(SimpleParams.INT_NULL));
		assertEquals(-999, result.getValue(SimpleParams.LNG_TEN));
		assertEquals(999, result.getValue(SimpleParams.LNG_NULL));
		assertEquals(-999, result.getValue(SimpleParams.LNG_TEN));
		assertEquals(999, result.getValue(SimpleParams.LNG_NULL));
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), result.getValue(SimpleParams.LDT_2007_10_01));
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), result.getValue(SimpleParams.LDT_NULL));
	}

	@Test
	public void testHappyPathFromObjectsCompEnv() throws Exception {
		
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/STR_BOB", "test");
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/STR_NULL", "not_null");
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.FLAG_TRUE", Boolean.FALSE);
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.FLAG_FALSE", Boolean.TRUE);
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/FLAG_NULL", Boolean.TRUE);
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/INT_TEN", Integer.valueOf(-999));
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.INT_NULL", Integer.valueOf(999));
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/LNG_TEN", Long.valueOf(-999));
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.LNG_NULL", Long.valueOf(999));
		mockJndiBind(context, "java:comp/env/org/yarnandtail/andhow/SimpleParams/LDT_2007_10_01", LocalDateTime.parse("2007-11-02T00:00"));
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.LDT_NULL", LocalDateTime.parse("2007-11-02T00:00"));

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);

		assertEquals("test", result.getValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getValue(SimpleParams.STR_NULL));
		assertEquals(false, result.getValue(SimpleParams.FLAG_TRUE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_FALSE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_NULL));
		assertEquals(-999, result.getValue(SimpleParams.INT_TEN));
		assertEquals(999, result.getValue(SimpleParams.INT_NULL));
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), result.getValue(SimpleParams.LDT_2007_10_01));
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), result.getValue(SimpleParams.LDT_NULL));
	}

	@Test
	public void testHappyPathFromStringsFromAddedNonStdPaths() throws Exception {

		mockJndiBind(context, "java:/test/org/yarnandtail/andhow/SimpleParams/STR_BOB", "test");
		mockJndiBind(context, "java:/test/org.yarnandtail.andhow.SimpleParams.STR_NULL", "not_null");
		mockJndiBind(context, "java:test/org.yarnandtail.andhow.SimpleParams.FLAG_TRUE", "false");
		mockJndiBind(context, "java:test/org/yarnandtail/andhow/SimpleParams/FLAG_FALSE", "true");
		mockJndiBind(context, "java:test/org.yarnandtail.andhow.SimpleParams.FLAG_NULL", "TRUE");
		mockJndiBind(context, "java:myapp/root/org.yarnandtail.andhow.SimpleParams.INT_TEN", "-999");
		//This should still work
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.INT_NULL", "999");

		//
		// Add ADDED_JNDI_ROOTS property value to the list of already set values.
		ArrayList<ValidatedValue> values = new ArrayList();
		values.add(
				new ValidatedValue(
						StdJndiLoader.CONFIG.ADDED_JNDI_ROOTS,
						"java:/test/,    java:test/  ,   java:myapp/root/"));
		ProblemList<Problem> problems = new ProblemList();
		appValuesBuilder.addValues(new LoaderValues(new StdMainStringArgsLoader(), values, problems));

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);


		assertEquals("test", result.getValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getValue(SimpleParams.STR_NULL));
		assertEquals(false, result.getValue(SimpleParams.FLAG_TRUE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_FALSE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_NULL));
		assertEquals(-999, result.getValue(SimpleParams.INT_TEN));
		assertEquals(999, result.getValue(SimpleParams.INT_NULL));
	}

	@Test
	public void testHappyPathFromStringsFromAddedAndReplacementNonStdPaths() throws Exception {


		mockJndiBind(context, "java:zip/org/yarnandtail/andhow/SimpleParams/STR_BOB", "test");
		mockJndiBind(context, "java:xy/z/org.yarnandtail.andhow.SimpleParams.STR_NULL", "not_null");
		mockJndiBind(context, "java:/test/org/yarnandtail/andhow/SimpleParams/FLAG_TRUE", "false");
		mockJndiBind(context, "java:test/org.yarnandtail.andhow.SimpleParams.FLAG_FALSE", "true");
		mockJndiBind(context, "java:test/org.yarnandtail.andhow.SimpleParams.FLAG_NULL", "TRUE");
		mockJndiBind(context, "java:myapp/root/org.yarnandtail.andhow.SimpleParams.INT_TEN", "-999");
		//This should NOT work
		mockJndiBind(context, "java:comp/env/org.yarnandtail.andhow.SimpleParams.INT_NULL", "999");


		//
		// Add STANDARD_JNDI_ROOTS and ADDED_JNDI_ROOTS property value to the list of already set values.
		ArrayList<ValidatedValue> values = new ArrayList();
		values.add(
				new ValidatedValue(
						StdJndiLoader.CONFIG.STANDARD_JNDI_ROOTS, "java:zip/,java:xy/z/"));
		values.add(
				new ValidatedValue(
						StdJndiLoader.CONFIG.ADDED_JNDI_ROOTS,
						"java:/test/  ,  ,java:test/ , java:myapp/root/"));
		ProblemList<Problem> problems = new ProblemList();
		appValuesBuilder.addValues(new LoaderValues(new StdMainStringArgsLoader(), values, problems));

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);

		assertEquals("test", result.getValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getValue(SimpleParams.STR_NULL));
		assertEquals(false, result.getValue(SimpleParams.FLAG_TRUE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_FALSE));
		assertEquals(true, result.getValue(SimpleParams.FLAG_NULL));
		assertEquals(-999, result.getValue(SimpleParams.INT_TEN));
		assertNull(result.getValue(SimpleParams.INT_NULL));
	}
}
