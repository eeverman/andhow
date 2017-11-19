package org.yarnandtail.andhow.load;

import java.util.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.StaticPropertyConfigurationMutable;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.ValidatedValuesWithContextMutable;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 *
 * @author eeverman
 */
public class PropertyFileOnClasspathLoaderUnitTest {
	
	private static final String CLASSPATH_BASE = "/org/yarnandtail/andhow/load/PropertyFileOnClasspathLoaderUnitTest_SimpleParams";
	
	StaticPropertyConfigurationMutable appDef;
	ValidatedValuesWithContextMutable appValuesBuilder;
	
	public static interface TestProps {
		StrProp CLAZZ_PATH = StrProp.builder().mustBeNonNull().build();
	}
	
	public interface SimpleParams {
		//Strings
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();

		//Flags
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
		FlagProp FLAG_NULL = FlagProp.builder().build();
	}
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		appDef = new StaticPropertyConfigurationMutable(bns);
		
		GroupProxy simpleProxy = AndHowUtil.buildGroupProxy(SimpleParams.class);
		
		appDef.addProperty(AndHowUtil.buildGroupProxy(TestProps.class), TestProps.CLAZZ_PATH);

		
		appDef.addProperty(simpleProxy, SimpleParams.STR_BOB);
		appDef.addProperty(simpleProxy, SimpleParams.STR_NULL);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(simpleProxy, SimpleParams.FLAG_NULL);

	}
	
	@Test
	public void testHappyPath() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, CLASSPATH_BASE + "1.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertEquals("kvpBobValue", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("kvpNullValue", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	
	@Test
	public void testDuplicateEntries() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, CLASSPATH_BASE + "2.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertEquals("kvpBobValue", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("3", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testEmptyValues() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, CLASSPATH_BASE + "3.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testAllWhitespaceValues() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, CLASSPATH_BASE + "4.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testQuotedStringValues() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, CLASSPATH_BASE + "5.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getValue(SimpleParams.STR_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("", result.getValue(SimpleParams.STR_NULL));
	}
	
	@Test
	public void testPropFileLoaderWithUnrecognizedPropNames() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, CLASSPATH_BASE + "6.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		//These are the two bad property names in the file
		List<String> badPropNames = Arrays.asList(new String[] {
			"org.yarnandtail.andhow.load.PropertyFileOnClasspathLoaderUnitTest.SimpleParams.XXX",
			"org.yarnandtail.andhow.load.PropertyFileOnClasspathLoaderUnitTest.SimpleXXXXXX.STR_BOB"});
		
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
			
			LoaderProblem.UnknownPropertyLoaderProblem uplp = (LoaderProblem.UnknownPropertyLoaderProblem)lp;
			
			//The problem description should contain one of the bad names
			assertTrue(
				badPropNames.stream().anyMatch(n -> uplp.getProblemDescription().contains(n)));
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	@Test
	public void testPropFileLoaderWithMissingFile() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(1, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.SourceNotFoundLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	/**
	 * The loader itself is OK w/ not having its parameter specified - it just
	 * ignores.
	 */
	@Test
	public void testPropFileLoaderWithNoClasspathConfigured() {
		
		ArrayList<ValidatedValue> evl = new ArrayList();
		//evl.add(new ValidatedValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new KeyValuePairLoader(new String[]{}), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileOnClasspathLoader pfl = new PropertyFileOnClasspathLoader(TestProps.CLAZZ_PATH, true);
		
		LoaderValues result = pfl.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
	}

}
