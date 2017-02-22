package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.LoaderValues;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.Problem;
import org.yarnandtail.andhow.ProblemList;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.internal.LoaderProblem;
import java.util.ArrayList;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.internal.ConstructionDefinitionMutable;
import org.yarnandtail.andhow.name.BasicNamingStrategy;
import org.yarnandtail.andhow.internal.ValueMapWithContextMutable;
import org.yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class PropertyFileFromClasspathLoaderUnitTest {
	
	ConstructionDefinitionMutable appDef;
	ValueMapWithContextMutable appValuesBuilder;
	
	public static interface TestProps extends PropertyGroup {
		StrProp CLAZZ_PATH = StrProp.builder().required().build();
	}
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new ConstructionDefinitionMutable(bns);
		
		appDef.addProperty(TestProps.class, TestProps.CLAZZ_PATH);

		
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_BOB);
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_NULL);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL);

	}
	
	@Test
	public void testHappyPath() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/SimpleParams1.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
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
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/SimpleParams2.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
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
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/SimpleParams3.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testAllWhitespaceValues() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/SimpleParams4.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testQuotedStringValues() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/SimpleParams5.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("", result.getEffectiveValue(SimpleParams.STR_NULL));
	}
	
	@Test
	public void testPropFileLoaderWithUnrecognizedPropNames() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/SimpleParams6.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	@Test
	public void testPropFileLoaderWithMissingFile() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
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
		
		ArrayList<PropertyValue> evl = new ArrayList();
		//evl.add(new PropertyValue(TestProps.CLAZZ_PATH, "/org/yarnandtail/andhow/load/XXXXXXX.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropertyFileFromClasspathLoader pfl = new PropertyFileFromClasspathLoader(TestProps.CLAZZ_PATH);
		
		LoaderValues result = pfl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
	}

}
