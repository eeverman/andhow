package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationMutable;
import org.yarnandtail.andhow.internal.ValidatedValuesWithContextMutable;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.util.NameUtil;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author eeverman
 */
public class StdEnvVarLoaderTest {

	PropertyConfigurationMutable appDef;
	ValidatedValuesWithContextMutable appValuesBuilder;
	HashMap<String, String> envVars = new HashMap();

	public interface SimpleParams {
		//Strings
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();

		//Flags
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
		FlagProp FLAG_NULL = FlagProp.builder().build();
	}

	@BeforeEach
	public void init() throws Exception {

		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();

		appDef = new PropertyConfigurationMutable(new CaseInsensitiveNaming());

		GroupProxy proxy = AndHowUtil.buildGroupProxy(SimpleParams.class);

		appDef.addProperty(proxy, SimpleParams.STR_BOB);
		appDef.addProperty(proxy, SimpleParams.STR_NULL);
		appDef.addProperty(proxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(proxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(proxy, SimpleParams.FLAG_NULL);

		envVars.clear();
		envVars.putAll(System.getenv());
	}


	protected String getPropName(Property p) throws Exception {
		return NameUtil.getAndHowName(SimpleParams.class, p);
	}

	@Test
	public void verifyBasicGettersAndSetters() {
		StdEnvVarLoader loader = new StdEnvVarLoader();

		assertTrue(loader instanceof ReadLoader);
		assertEquals("java.lang.System.getenv()", loader.getSpecificLoadDescription());
		assertEquals("EnvironmentVariable", loader.getLoaderType());
		assertTrue(loader.isTrimmingRequiredForStringValues());
		assertFalse(loader.isUnknownPropertyAProblem());
	}

	@Test
	public void testHappyPathUnix() throws Exception {


		envVars.put(getPropName(SimpleParams.STR_BOB), "aaa");
		envVars.put(getPropName(SimpleParams.STR_NULL), "bbb");
		envVars.put(getPropName(SimpleParams.FLAG_FALSE), "t");
		envVars.put(getPropName(SimpleParams.FLAG_TRUE), "f");
		envVars.put(getPropName(SimpleParams.FLAG_NULL), "y");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertEquals("aaa", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bbb", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testHappyPathWindows() throws Exception {


		envVars.put(getPropName(SimpleParams.STR_BOB).toUpperCase(), "aaa");
		envVars.put(getPropName(SimpleParams.STR_NULL).toUpperCase(), "bbb");
		envVars.put(getPropName(SimpleParams.FLAG_FALSE).toUpperCase(), "t");
		envVars.put(getPropName(SimpleParams.FLAG_TRUE).toUpperCase(), "f");
		envVars.put(getPropName(SimpleParams.FLAG_NULL).toUpperCase(), "y");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertEquals("aaa", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bbb", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	/*  The HashTable that System.properties uses does not allow null values, so
		no need (or way) to test nulls here. */
//	@Test
//	public void testNullValues() throws Exception {
//	}

	@Test
	public void testEmptyValuesUnix() throws Exception {

		envVars.put(getPropName(SimpleParams.STR_BOB), "");
		envVars.put(getPropName(SimpleParams.STR_NULL), "");
		envVars.put(getPropName(SimpleParams.FLAG_FALSE), "");
		envVars.put(getPropName(SimpleParams.FLAG_TRUE), "");
		envVars.put(getPropName(SimpleParams.FLAG_NULL), "");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testEmptyValuesWindows() throws Exception {


		envVars.put(getPropName(SimpleParams.STR_BOB).toUpperCase(), "");
		envVars.put(getPropName(SimpleParams.STR_NULL).toUpperCase(), "");
		envVars.put(getPropName(SimpleParams.FLAG_FALSE).toUpperCase(), "");
		envVars.put(getPropName(SimpleParams.FLAG_TRUE).toUpperCase(), "");
		envVars.put(getPropName(SimpleParams.FLAG_NULL).toUpperCase(), "");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testAllWhitespaceValues() throws Exception {


		envVars.put(getPropName(SimpleParams.STR_BOB), "\t\t\t\t");
		envVars.put(getPropName(SimpleParams.STR_NULL), "\t\t\t\t");
		envVars.put(getPropName(SimpleParams.FLAG_FALSE), "\t\t\t\t");
		envVars.put(getPropName(SimpleParams.FLAG_TRUE), "\t\t\t\t");
		envVars.put(getPropName(SimpleParams.FLAG_NULL), "\t\t\t\t");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		//String value coming from this loader do not require trimming by default
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));

		//Non-string values still get trimmed
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testQuotedStringValues() throws Exception {


		envVars.put(getPropName(SimpleParams.STR_BOB), "\"  two_spaces_&_two_tabs\t\t\" ");
		envVars.put(getPropName(SimpleParams.STR_NULL), "");
		envVars.put(getPropName(SimpleParams.FLAG_FALSE), "");
		envVars.put(getPropName(SimpleParams.FLAG_TRUE), "");
		envVars.put(getPropName(SimpleParams.FLAG_NULL), "");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertEquals("  two_spaces_&_two_tabs\t\t", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
	}

	@Test
	public void testNoRecognizedValues() throws Exception {

		envVars.put("XXX", "aaa");

		StdEnvVarLoader spl = new StdEnvVarLoader();
		spl.setMap(envVars);

		LoaderValues result = spl.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.FALSE, result.getValue(SimpleParams.FLAG_FALSE));
		assertFalse(result.getValue(SimpleParams.FLAG_NULL));
	}

}
