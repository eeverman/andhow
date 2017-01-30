package yarnandtail.andhow.load;

import yarnandtail.andhow.internal.LoaderProblem;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import yarnandtail.andhow.*;
import yarnandtail.andhow.internal.ConstructionDefinitionMutable;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;

/**
 *
 * @author eeverman
 */
public class PropFileLoaderTest {
	
	ConstructionDefinitionMutable appDef;
	ValueMapWithContextMutable appValuesBuilder;
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new ConstructionDefinitionMutable(bns);
		
		appDef.addProperty(PropFileLoader.CONFIG.class, PropFileLoader.CONFIG.CLASSPATH_PATH);
		appDef.addProperty(PropFileLoader.CONFIG.class, PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH);
		appDef.addProperty(PropFileLoader.CONFIG.class,	PropFileLoader.CONFIG.FILESYSTEM_PATH);

		
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_BOB);
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_NULL);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL);

	}
	
	@Test
	public void testHappyPath() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams1.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
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
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams2.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
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
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams3.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
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
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams4.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
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
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams5.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
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
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams6.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, new ProblemList<Problem>());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}

}
