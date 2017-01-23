package yarnandtail.andhow.load;

import java.util.*;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PropertyGroup.NameAndProperty;
import yarnandtail.andhow.internal.ConstructionDefinitionMutable;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;

/**
 *
 * @author eeverman
 */
public class SystemEnvironmentLoaderTest1 {
	
	TestConstructionDefinition appDef;
	ValueMapWithContextMutable appValuesBuilder;
	
	@Before
	public void init() throws Exception {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new TestConstructionDefinition();
		
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_BOB, bns.buildNames(SimpleParams.STR_BOB, SimpleParams.class, "STR_BOB"));
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_NULL, bns.buildNames(SimpleParams.STR_NULL, SimpleParams.class, "STR_NULL"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE, bns.buildNames(SimpleParams.FLAG_FALSE, SimpleParams.class, "FLAG_FALSE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE, bns.buildNames(SimpleParams.FLAG_TRUE, SimpleParams.class, "FLAG_TRUE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL, bns.buildNames(SimpleParams.FLAG_NULL, SimpleParams.class, "FLAG_NULL"));
		
	}
	
	
	protected String getPropName(Property p) throws Exception {
		return PropertyGroup.getCanonicalName(SimpleParams.class, p);
	}
	
	@Test
	public void testHappyPathUnix() throws Exception {
		
		
		appDef.addEnvVar(getPropName(SimpleParams.STR_BOB), "aaa");
		appDef.addEnvVar(getPropName(SimpleParams.STR_NULL), "bbb");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_FALSE), "t");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_TRUE), "f");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_NULL), "y");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("aaa", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bbb", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testHappyPathWindows() throws Exception {
		
		
		appDef.addEnvVar(getPropName(SimpleParams.STR_BOB).toUpperCase(), "aaa");
		appDef.addEnvVar(getPropName(SimpleParams.STR_NULL).toUpperCase(), "bbb");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_FALSE).toUpperCase(), "t");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_TRUE).toUpperCase(), "f");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_NULL).toUpperCase(), "y");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
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
		
		
		appDef.addEnvVar(getPropName(SimpleParams.STR_BOB), "");
		appDef.addEnvVar(getPropName(SimpleParams.STR_NULL), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_FALSE), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_TRUE), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_NULL), "");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testEmptyValuesWindows() throws Exception {
		
		
		appDef.addEnvVar(getPropName(SimpleParams.STR_BOB).toUpperCase(), "");
		appDef.addEnvVar(getPropName(SimpleParams.STR_NULL).toUpperCase(), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_FALSE).toUpperCase(), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_TRUE).toUpperCase(), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_NULL).toUpperCase(), "");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testAllWhitespaceValues() throws Exception {
		
		
		appDef.addEnvVar(getPropName(SimpleParams.STR_BOB), "\t\t\t\t");
		appDef.addEnvVar(getPropName(SimpleParams.STR_NULL), "\t\t\t\t");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_FALSE), "\t\t\t\t");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_TRUE), "\t\t\t\t");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_NULL), "\t\t\t\t");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		//String value coming from this loader do not require trimming by default
		assertEquals("\t\t\t\t", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("\t\t\t\t", result.getExplicitValue(SimpleParams.STR_NULL));
		
		//Non-string values still get trimmed
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testQuotedStringValues() throws Exception {
		
		
		appDef.addEnvVar(getPropName(SimpleParams.STR_BOB), "\"  two_spaces_&_two_tabs\t\t\" ");
		appDef.addEnvVar(getPropName(SimpleParams.STR_NULL), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_FALSE), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_TRUE), "");
		appDef.addEnvVar(getPropName(SimpleParams.FLAG_NULL), "");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertEquals("\"  two_spaces_&_two_tabs\t\t\" ", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("\"  two_spaces_&_two_tabs\t\t\" ", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertEquals("", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("", result.getEffectiveValue(SimpleParams.STR_NULL));
	}
	
	@Test
	public void testNoRecognizedValues() throws Exception {
		
		appDef.addEnvVar("XXX", "aaa");
		
		SystemEnviromentLoader spl = new SystemEnviromentLoader();
		
		LoaderValues result = spl.load(appDef, null, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getEffectiveValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.FALSE, result.getEffectiveValue(SimpleParams.FLAG_FALSE));
		assertNull(result.getEffectiveValue(SimpleParams.FLAG_NULL));
	}
	
	public static class TestConstructionDefinition extends ConstructionDefinitionMutable {
		
		HashMap<String, String> envVars = new HashMap();

		public TestConstructionDefinition() {
			super(new BasicNamingStrategy());
		}
		
		public void addEnvVar(String name, String value) {
			envVars.put(name, value);
		}
		
		@Override
		public Map<String, String> getSystemEnvironment() {
			HashMap<String, String> props = new HashMap();
			props.putAll(System.getenv());
			props.putAll(envVars);
			return Collections.unmodifiableMap(props);
		}
	}

	
}
