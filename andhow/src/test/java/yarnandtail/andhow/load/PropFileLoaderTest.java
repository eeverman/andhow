package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.LoaderProblem;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;
import yarnandtail.andhow.SimpleParams;

/**
 *
 * @author eeverman
 */
public class PropFileLoaderTest {
	
	RuntimeDefinition appDef;
	ValueMapWithContextMutable appValuesBuilder;
	
	@Before
	public void init() {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new RuntimeDefinition();
		
		appDef.addProperty(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.CLASSPATH_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.CLASSPATH_PATH, PropFileLoader.CONFIG.class, "CLASSPATH_PATH"));
		appDef.addProperty(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH, PropFileLoader.CONFIG.class, "EXECUTABLE_RELATIVE_PATH"));
		appDef.addProperty(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.FILESYSTEM_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.FILESYSTEM_PATH, PropFileLoader.CONFIG.class, "FILESYSTEM_PATH"));

		
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_BOB, bns.buildNames(SimpleParams.STR_BOB, SimpleParams.class, "KVP_BOB"));
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_NULL, bns.buildNames(SimpleParams.STR_NULL, SimpleParams.class, "KVP_NULL"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE, bns.buildNames(SimpleParams.FLAG_FALSE, SimpleParams.class, "FLAG_FALSE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE, bns.buildNames(SimpleParams.FLAG_TRUE, SimpleParams.class, "FLAG_TRUE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL, bns.buildNames(SimpleParams.FLAG_NULL, SimpleParams.class, "FLAG_NULL"));

	}
	
	@Test
	public void testHappyPath() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams1.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
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
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
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
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
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
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
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
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("  two_spaces_&_two_tabs\t\t", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("", result.getEffectiveValue(SimpleParams.STR_NULL));
	}
	
	@Test
	public void testPropFileLoaderWithUnrecognizedPropNames() {
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams6.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals(2, result.getProblems().size());
		for (LoaderProblem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
	}

}
