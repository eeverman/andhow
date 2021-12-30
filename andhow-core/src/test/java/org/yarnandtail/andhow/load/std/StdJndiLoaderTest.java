package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.junit5.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.sample.JndiLoaderSamplePrinter;
import org.yarnandtail.andhow.util.NameUtil;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@KillAndHowBeforeEachTest
public class StdJndiLoaderTest {

	CaseInsensitiveNaming bns;

	public interface ValidParams {
		//Strings
		StrProp STR_XXX = StrProp.builder().endsWith("XXX").build();
		IntProp INT_TEN = IntProp.builder().greaterThan(10).build();

	}

	private StdJndiLoader loader;

	@BeforeEach
	public void initLoader() {
		loader = new StdJndiLoader();
		bns = new CaseInsensitiveNaming();
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
	@EnableJndiForThisTestMethod
	public void testHappyPathFromStringsCompEnvAsURIs() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "org/yarnandtail/andhow/SimpleParams/");
		//


		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null");
		jndi.bind("" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE)), "false");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE)), "true");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL)), "TRUE");
		jndi.bind("" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN)), "-999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL)), "999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_TEN)), "-999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_NULL)), "999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_2007_10_01)), "2007-11-02T00:00");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_NULL)), "2007-11-02T00:00");


		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-999, SimpleParams.INT_TEN.getValue());
		assertEquals(999, SimpleParams.INT_NULL.getValue());
		assertEquals(-999, SimpleParams.LNG_TEN.getValue());
		assertEquals(999, SimpleParams.LNG_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testHappyPathFromStringsCompEnvAsClasspath() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB), "test");
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE), "false");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE), "true");
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL), "999");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_TEN), "-999");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_NULL), "999");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_2007_10_01), "2007-11-02T00:00");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_NULL), "2007-11-02T00:00");


		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-999, SimpleParams.INT_TEN.getValue());
		assertEquals(999, SimpleParams.INT_NULL.getValue());
		assertEquals(-999, SimpleParams.LNG_TEN.getValue());
		assertEquals(999, SimpleParams.LNG_NULL.getValue());
		assertEquals(-999, SimpleParams.LNG_TEN.getValue());
		assertEquals(999, SimpleParams.LNG_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testHappyPathFromStringsFromAddedNonStdPaths() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:test/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:/test/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:myapp/root/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:/test/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:/test/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:test/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE), "false");
		jndi.bind("java:test/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE)), "true");
		jndi.bind("java:test/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("java:myapp/root/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		//This should still work
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL), "999");


		AndHowConfiguration config = AndHowTestConfig.instance()
				.addFixedValue(StdJndiLoader.CONFIG.ADDED_JNDI_ROOTS, "java:/test/,    java:test/  ,   java:myapp/root/")
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-999, SimpleParams.INT_TEN.getValue());
		assertEquals(999, SimpleParams.INT_NULL.getValue());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testHappyPathFromStringsFromAddedAndReplacementNonStdPaths() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:zip/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:xy/z/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:/test/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:test/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:myapp/root/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:zip/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:xy/z/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:/test/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE)), "false");
		jndi.bind("java:test/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE), "true");
		jndi.bind("java:test/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("java:myapp/root/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		//This should NOT work
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL), "999");



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addFixedValue(StdJndiLoader.CONFIG.STANDARD_JNDI_ROOTS, "java:zip/,java:xy/z/")
				.addFixedValue(StdJndiLoader.CONFIG.ADDED_JNDI_ROOTS, "java:/test/  ,  ,java:test/ , java:myapp/root/")
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-999, SimpleParams.INT_TEN.getValue());
		assertNull(SimpleParams.INT_NULL.getValue());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testHappyPathFromObjectsCompEnv() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null");
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE), Boolean.FALSE);
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE), Boolean.TRUE);
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL)), Boolean.TRUE);
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN)), Integer.valueOf(-999));
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL), Integer.valueOf(999));
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_TEN)), Long.valueOf(-999));
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_NULL), Long.valueOf(999));
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_2007_10_01)), LocalDateTime.parse("2007-11-02T00:00"));
		jndi.bind("java:comp/env/" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_NULL), LocalDateTime.parse("2007-11-02T00:00"));



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-999, SimpleParams.INT_TEN.getValue());
		assertEquals(999, SimpleParams.INT_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testHappyPathFromObjectsRoot() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		//

		//switching values slightly to make sure we are reading the correct ones
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB), "test2");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null2");
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE), Boolean.FALSE);
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE)), Boolean.TRUE);
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL), Boolean.TRUE);
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN), -9999);
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL)), 9999);



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);


		assertEquals("test2", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null2", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-9999, SimpleParams.INT_TEN.getValue());
		assertEquals(9999, SimpleParams.INT_NULL.getValue());
	}

	//
	//
	// Non-HappyPath
	//

	@Test
	@EnableJndiForThisTestMethod
	public void testDuplicateValues() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		//

		//switching values slightly to make sure we are reading the correct ones
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB), "test2");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB)), "not_null2");



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<LoaderProblem> lps = e.getProblems().filter(LoaderProblem.class);

		assertEquals(1, lps.size());
		assertTrue(lps.get(0) instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testObjectConversionErrors() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN), Long.valueOf(-9999));
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL)), Float.valueOf(22));
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_TEN), Integer.valueOf(-9999));



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());
		List<LoaderProblem> lps = e.getProblems().filter(LoaderProblem.class);

		assertEquals(3, lps.size());
		assertTrue(lps.get(0) instanceof LoaderProblem.ObjectConversionValueProblem);
		assertEquals(SimpleParams.INT_TEN, lps.get(0).getBadValueCoord().getProperty());
		assertTrue(lps.get(1) instanceof LoaderProblem.ObjectConversionValueProblem);
		assertEquals(SimpleParams.INT_NULL, lps.get(1).getBadValueCoord().getProperty());
		assertTrue(lps.get(2) instanceof LoaderProblem.ObjectConversionValueProblem);
		assertEquals(SimpleParams.LNG_TEN, lps.get(2).getBadValueCoord().getProperty());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testStringConversionErrors() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN), "234.567");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL)), "Apple");
		jndi.bind("java:" + NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_TEN), "234.567");


		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<LoaderProblem> vps = e.getProblems().filter(LoaderProblem.class);

		assertEquals(3, vps.size());
		assertTrue(vps.get(0) instanceof LoaderProblem.StringConversionLoaderProblem);
		assertEquals(SimpleParams.INT_TEN, vps.get(0).getBadValueCoord().getProperty());
		assertTrue(vps.get(1) instanceof LoaderProblem.StringConversionLoaderProblem);
		assertEquals(SimpleParams.INT_NULL, vps.get(1).getBadValueCoord().getProperty());
		assertTrue(vps.get(2) instanceof LoaderProblem.StringConversionLoaderProblem);
		assertEquals(SimpleParams.LNG_TEN, vps.get(2).getBadValueCoord().getProperty());
	}


	@Test
	@EnableJndiForThisTestMethod
	public void testValidationIsEnforcedWhenExactTypeUsed() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/load/std/StdJndiLoaderTest/ValidParams/");
		//

		jndi.bind("java:" + NameUtil.getAndHowName(ValidParams.class, ValidParams.INT_TEN), Integer.parseInt("9"));
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(ValidParams.class, ValidParams.STR_XXX)), "YYY");



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(ValidParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<Problem> vps = e.getProblems();

		assertEquals(2, vps.size());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testValidationIsEnforcedWhenConvertionUsed() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:" + NameUtil.getAndHowName(ValidParams.class, ValidParams.INT_TEN), "9");



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(ValidParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<Problem> vps = e.getProblems();

		assertEquals(1, vps.size());
		assertTrue(vps.get(0) instanceof ValueProblem);
		assertEquals(ValidParams.INT_TEN, ((ValueProblem) (vps.get(0))).getLoaderPropertyCoord().getProperty());
	}
}
