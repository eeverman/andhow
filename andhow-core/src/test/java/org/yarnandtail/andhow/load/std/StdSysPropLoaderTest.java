package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationMutable;
import org.yarnandtail.andhow.internal.ValidatedValuesWithContextMutable;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterEachTestExt;
import org.yarnandtail.andhow.load.LoaderEnvironmentBuilder;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.util.NameUtil;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RestoreSysPropsAfterEachTestExt.class)
public class StdSysPropLoaderTest {

	StdSysPropLoader loader;
	LoaderEnvironmentBuilder leb;
	HashMap<String, String> sysProps = new HashMap();
	PropertyConfigurationMutable appDef;
	ValidatedValuesWithContextMutable appValuesBuilder;
	GroupProxy simpleProxy;

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

		loader = new StdSysPropLoader();
		leb = new LoaderEnvironmentBuilder();

		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();

		appDef = new PropertyConfigurationMutable(bns);

		simpleProxy = AndHowUtil.buildGroupProxy(SimpleParams.class);

		appDef.addProperty(simpleProxy, SimpleParams.STR_BOB);
		appDef.addProperty(simpleProxy, SimpleParams.STR_NULL);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_NULL);

		sysProps = new HashMap();
		sysProps.putAll(System.getenv());	// Just to have some other values set
	}

	protected String getPropName(Property p) throws Exception {
		return NameUtil.getAndHowName(SimpleParams.class, p);
	}

	@Test
	public void reflexiveValuesReturnExpectedValues() {
		assertTrue(loader instanceof ReadLoader);
		assertEquals("java.lang.System.getProperties()", loader.getSpecificLoadDescription());
		assertNull(loader.getLoaderDialect());
		assertEquals("SystemProperty", loader.getLoaderType());
		assertFalse(loader.isFlaggable());
		assertFalse(loader.isUnknownPropertyAProblem());
		assertTrue(loader.isTrimmingRequiredForStringValues());
		assertNull(loader.getClassConfig());
		assertNull(loader.getConfigSamplePrinter());
		assertTrue(loader.getInstanceConfig().isEmpty());
		loader.releaseResources();	// should cause no error
	}

	@Test
	public void testHappyPath() throws Exception {

		sysProps.put(getPropName(SimpleParams.STR_BOB), "aaa");
		sysProps.put(getPropName(SimpleParams.STR_NULL), "bbb");
		sysProps.put(getPropName(SimpleParams.FLAG_FALSE), "t");
		sysProps.put(getPropName(SimpleParams.FLAG_TRUE), "f");
		sysProps.put(getPropName(SimpleParams.FLAG_NULL), "y");

		leb.setSysProps(sysProps);

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertEquals("aaa", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bbb", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testEmptyValues() throws Exception {

		sysProps.put(getPropName(SimpleParams.STR_BOB), "");
		sysProps.put(getPropName(SimpleParams.STR_NULL), "");
		sysProps.put(getPropName(SimpleParams.FLAG_FALSE), "");
		sysProps.put(getPropName(SimpleParams.FLAG_TRUE), "");
		sysProps.put(getPropName(SimpleParams.FLAG_NULL), "");

		leb.setSysProps(sysProps);

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testAllWhitespaceValues() throws Exception {

		sysProps.put(getPropName(SimpleParams.STR_BOB), "\t\t\t\t");
		sysProps.put(getPropName(SimpleParams.STR_NULL), "\t\t\t\t");
		sysProps.put(getPropName(SimpleParams.FLAG_FALSE), "\t\t\t\t");
		sysProps.put(getPropName(SimpleParams.FLAG_TRUE), "\t\t\t\t");
		sysProps.put(getPropName(SimpleParams.FLAG_NULL), "\t\t\t\t");

		leb.setSysProps(sysProps);

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		//String value coming from this loader are trimmed
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));

		//Non-string values still get trimmed
		assertNull(result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testQuotedStringValues() throws Exception {

		sysProps.put(getPropName(SimpleParams.STR_BOB), "\"  two_spaces_&_two_tabs\t\t\" ");
		sysProps.put(getPropName(SimpleParams.STR_NULL), "");
		sysProps.put(getPropName(SimpleParams.FLAG_FALSE), "");
		sysProps.put(getPropName(SimpleParams.FLAG_TRUE), "");
		sysProps.put(getPropName(SimpleParams.FLAG_NULL), "");

		leb.setSysProps(sysProps);

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());

		assertEquals("  two_spaces_&_two_tabs\t\t", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
	}

	@Test
	public void testNoRecognizedValues() throws Exception {

		sysProps.put("XXX", "aaa");

		leb.setSysProps(sysProps);

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

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
