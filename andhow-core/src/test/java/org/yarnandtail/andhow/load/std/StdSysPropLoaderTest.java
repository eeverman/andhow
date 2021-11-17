package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationMutable;
import org.yarnandtail.andhow.internal.ValidatedValuesWithContextMutable;
import org.yarnandtail.andhow.junit5.ext.RestoreSysPropsAfterEachTestExt;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.util.NameUtil;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RestoreSysPropsAfterEachTestExt.class)
public class StdSysPropLoaderTest {

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

		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();

		appDef = new PropertyConfigurationMutable(bns);

		simpleProxy = AndHowUtil.buildGroupProxy(SimpleParams.class);

		appDef.addProperty(simpleProxy, SimpleParams.STR_BOB);
		appDef.addProperty(simpleProxy, SimpleParams.STR_NULL);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_NULL);

	}

	protected String getPropName(Property p) throws Exception {
		return NameUtil.getAndHowName(SimpleParams.class, p);
	}

	@Test
	public void verifyBasicGettersAndSetters() {
		StdSysPropLoader loader = new StdSysPropLoader();

		assertTrue(loader instanceof ReadLoader);
		assertEquals("java.lang.System.getProperties()", loader.getSpecificLoadDescription());
		assertEquals("SystemProperty", loader.getLoaderType());
		assertTrue(loader.isTrimmingRequiredForStringValues());
		assertFalse(loader.isUnknownPropertyAProblem());
	}

	@Test
	public void testHappyPath() throws Exception {


		System.setProperty(getPropName(SimpleParams.STR_BOB), "aaa");
		System.setProperty(getPropName(SimpleParams.STR_NULL), "bbb");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "t");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "f");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "y");

		StdSysPropLoader spl = new StdSysPropLoader();

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
	public void testEmptyValues() throws Exception {

		System.setProperty(getPropName(SimpleParams.STR_BOB), "");
		System.setProperty(getPropName(SimpleParams.STR_NULL), "");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "");

		StdSysPropLoader spl = new StdSysPropLoader();

		LoaderValues result = spl.load(appDef, appValuesBuilder);

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

		System.setProperty(getPropName(SimpleParams.STR_BOB), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.STR_NULL), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "\t\t\t\t");

		StdSysPropLoader spl = new StdSysPropLoader();

		LoaderValues result = spl.load(appDef, appValuesBuilder);

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

		System.setProperty(getPropName(SimpleParams.STR_BOB), "\"  two_spaces_&_two_tabs\t\t\" ");
		System.setProperty(getPropName(SimpleParams.STR_NULL), "");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "");

		StdSysPropLoader spl = new StdSysPropLoader();

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

		System.setProperty("XXX", "aaa");

		StdSysPropLoader spl = new StdSysPropLoader();

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
