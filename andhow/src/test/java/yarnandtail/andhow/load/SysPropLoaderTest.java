package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.LoaderProblem;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.PropertyGroup.NameAndProperty;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;
import yarnandtail.andhow.SimpleParams;

/**
 *
 * @author eeverman
 */
public class SysPropLoaderTest {
	
	RuntimeDefinition appDef;
	ValueMapWithContextMutable appValuesBuilder;
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new RuntimeDefinition();
		
		appDef.addProperty(SimpleParams.class, SimpleParams.KVP_BOB, bns.buildNames(SimpleParams.KVP_BOB, SimpleParams.class, "KVP_BOB"));
		appDef.addProperty(SimpleParams.class, SimpleParams.KVP_NULL, bns.buildNames(SimpleParams.KVP_NULL, SimpleParams.class, "KVP_NULL"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE, bns.buildNames(SimpleParams.FLAG_FALSE, SimpleParams.class, "FLAG_FALSE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE, bns.buildNames(SimpleParams.FLAG_TRUE, SimpleParams.class, "FLAG_TRUE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL, bns.buildNames(SimpleParams.FLAG_NULL, SimpleParams.class, "FLAG_NULL"));

		//Clear all known system properties
		for (NameAndProperty nap : PropertyGroup.getProperties(SimpleParams.class)) {
			System.clearProperty(nap.canonName);
		}
		
		System.clearProperty("XXX");	//used in one test
		
	}
	
	@After
	public void post() throws Exception {
		//Clear all known system properties
		for (NameAndProperty nap : PropertyGroup.getProperties(SimpleParams.class)) {
			System.clearProperty(nap.canonName);
		}
		System.clearProperty("XXX");	//used in one test
	}
	
	protected String getPropName(Property p) throws Exception {
		return PropertyGroup.getCanonicalName(SimpleParams.class, p);
	}
	
	@Test
	public void testHappyPath() throws Exception {
		
		
		System.setProperty(getPropName(SimpleParams.KVP_BOB), "aaa");
		System.setProperty(getPropName(SimpleParams.KVP_NULL), "bbb");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "t");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "f");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "y");
		
		SysPropLoader spl = new SysPropLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("aaa", result.getExplicitValue(SimpleParams.KVP_BOB));
		assertEquals("bbb", result.getExplicitValue(SimpleParams.KVP_NULL));
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
	public void testEmptyValues() throws Exception {
		
		
		System.setProperty(getPropName(SimpleParams.KVP_BOB), "");
		System.setProperty(getPropName(SimpleParams.KVP_NULL), "");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "");
		
		SysPropLoader spl = new SysPropLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("", result.getExplicitValue(SimpleParams.KVP_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.KVP_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testAllWhitespaceValues() throws Exception {
		
		
		System.setProperty(getPropName(SimpleParams.KVP_BOB), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.KVP_NULL), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "\t\t\t\t");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "\t\t\t\t");
		
		SysPropLoader spl = new SysPropLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		//String value coming from this loader do not require trimming by default
		assertEquals("\t\t\t\t", result.getExplicitValue(SimpleParams.KVP_BOB));
		assertEquals("\t\t\t\t", result.getExplicitValue(SimpleParams.KVP_NULL));
		
		//Non-string values still get trimmed
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testQuotedStringValues() throws Exception {
		
		
		System.setProperty(getPropName(SimpleParams.KVP_BOB), "\"  two_spaces_&_two_tabs\t\t\" ");
		System.setProperty(getPropName(SimpleParams.KVP_NULL), "");
		System.setProperty(getPropName(SimpleParams.FLAG_FALSE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_TRUE), "");
		System.setProperty(getPropName(SimpleParams.FLAG_NULL), "");
		
		SysPropLoader spl = new SysPropLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("\"  two_spaces_&_two_tabs\t\t\" ", result.getExplicitValue(SimpleParams.KVP_BOB));
		assertEquals("\"  two_spaces_&_two_tabs\t\t\" ", result.getEffectiveValue(SimpleParams.KVP_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.KVP_NULL));
		assertEquals("", result.getEffectiveValue(SimpleParams.KVP_NULL));
	}
	
	@Test
	public void testNoRecognizedValues() throws Exception {
		
		System.setProperty("XXX", "aaa");
		
		SysPropLoader spl = new SysPropLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.KVP_BOB));
		assertEquals("bob", result.getEffectiveValue(SimpleParams.KVP_BOB));
		assertNull(result.getExplicitValue(SimpleParams.KVP_NULL));
		assertEquals(Boolean.TRUE, result.getEffectiveValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.FALSE, result.getEffectiveValue(SimpleParams.FLAG_FALSE));
		assertNull(result.getEffectiveValue(SimpleParams.FLAG_NULL));
	}

	
}
