package org.yarnandtail.andhow;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.load.KeyValuePairLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test is a minimal unit teat because this class is not really testable
 * as a unit test.
 * <br>
 * See the AndHowSystem Test sub-project for better test coverage.
 * 
 * @author ericeverman
 */
public class AndHowTest extends AndHowCoreTestBase {
	

	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	CaseInsensitiveNaming basicNaming = new CaseInsensitiveNaming();
	ArrayList<Class<?>> configPtGroups = new ArrayList();
	Map<Property<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	
	public static interface RequiredParams {
		StrProp STR_BOB_R = StrProp.builder().defaultValue("Bob").mustBeNonNull().build();
		StrProp STR_NULL_R = StrProp.builder().mustBeNonNull().mustStartWith("XYZ").build();
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).mustBeNonNull().build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).mustBeNonNull().build();
		FlagProp FLAG_NULL = FlagProp.builder().mustBeNonNull().build();
	}
	
	@BeforeEach
	public void setup() throws Exception {
		
		configPtGroups.clear();
		configPtGroups.add(SimpleParams.class);
		
		startVals.clear();
		startVals.put(SimpleParams.STR_BOB, "test");
		startVals.put(SimpleParams.STR_NULL, "not_null");
		startVals.put(SimpleParams.FLAG_TRUE, Boolean.FALSE);
		startVals.put(SimpleParams.FLAG_FALSE, Boolean.TRUE);
		startVals.put(SimpleParams.FLAG_NULL, Boolean.TRUE);
		
		cmdLineArgsWFullClassName = new String[] {
			paramFullPath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test",
			paramFullPath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null",
			paramFullPath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false",
			paramFullPath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true",
			paramFullPath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true"
		};
		
	}

	/*
	 * TODO:  There is no AndHowInit instance on the classpath, so this test only tests
	 * findConfig creating a default Config instance.  A separate maven module with a
	 * separate classpath is needed to test behaviour when there is a AndHowInit on
	 * the path.
	 */
	@Test
	public void testFindConfig() {
		AndHowConfiguration<? extends AndHowConfiguration> config1 = AndHow.findConfig();
		AndHowConfiguration<? extends AndHowConfiguration> config2 = AndHow.findConfig();
		assertSame(config1, config2, "Should return the same instance each time");
		assertFalse(AndHow.isInitialize(), "findConfig should not force initialization");
	}
	
	@Test
	public void testInitialization() {
		Long startTime = System.currentTimeMillis();
		
		AndHow.Initialization init = new AndHow.Initialization();
		
		Long endTime = System.currentTimeMillis();
		
		assertEquals(this.getClass().getName(), init.getStackTrace()[0].getClassName());
		assertEquals("testInitialization", init.getStackTrace()[0].getMethodName());
		assertTrue(startTime <= init.getTimeStamp());
		assertTrue(endTime >= init.getTimeStamp());
	}
	
	
	@Test
	public void testIsInitialize() {
		assertFalse(AndHow.isInitialize());
	}
	
	@Test
	public void testTheTest() {
		//This could be generalized to use the class.getCanonicalName(),
		//but this one place we make it explicit
		assertEquals("org.yarnandtail.andhow.SimpleParams.", paramFullPath);
	}
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		
		AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.groups(configPtGroups)
				.setCmdLineArgs(cmdLineArgsWFullClassName);
		
		AndHow.instance(config);
		
		assertTrue(AndHow.getInitializationTrace().length > 0);
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(10), SimpleParams.INT_TEN.getValue());
		assertNull(SimpleParams.INT_NULL.getValue());
		assertEquals(new Long(10), SimpleParams.LNG_TEN.getValue());
		assertNull(SimpleParams.LNG_NULL.getValue());
		assertEquals(new Double(10), SimpleParams.DBL_TEN.getValue());
		assertNull(SimpleParams.DBL_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-10-01T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertNull(SimpleParams.LDT_NULL.getValue());

	}
	
	/**
	 * This is really testing how the NonProductionConfig works - how can this be
	 * targeted to the init config?
	 */
	@Test
	public void testBlowingUpWithDuplicateLoaders() {
		
		KeyValuePairLoader kvpl = new KeyValuePairLoader();
		kvpl.setKeyValuePairs(cmdLineArgsWFullClassName);
		
		try {

			AndHowConfiguration config = AndHowCoreTestConfig.instance()
				.setLoaders(kvpl, kvpl)
				.groups(configPtGroups);
			
			AndHow.instance(config);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(ConstructionProblem.class).size());
			assertTrue(ce.getProblems().filter(ConstructionProblem.class).get(0) instanceof ConstructionProblem.DuplicateLoader);
			
			ConstructionProblem.DuplicateLoader dl = (ConstructionProblem.DuplicateLoader)ce.getProblems().filter(ConstructionProblem.class).get(0);
			assertEquals(kvpl, dl.getLoader());
			assertTrue(ce.getSampleDirectory().length() > 0);
			
			File sampleDir = new File(ce.getSampleDirectory());
			assertTrue(sampleDir.exists());
			assertTrue(sampleDir.listFiles().length > 0);
		}
	}
	
	@Test
	public void testCmdLineLoaderMissingRequiredParamShouldThrowAConfigException() {
		
		try {
				AndHowConfiguration config = AndHowCoreTestConfig.instance()
					.groups(configPtGroups)
					.group(RequiredParams.class)
					.setCmdLineArgs(cmdLineArgsWFullClassName);
				
				AndHow.instance(config);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(RequirementProblem.class).size());
			assertEquals(RequiredParams.STR_NULL_R, ce.getProblems().filter(RequirementProblem.class).get(0).getPropertyCoord().getProperty());
		}
	}
	
	@Test
	public void testInvalidValuesShouldCauseValidationException() {
		
		String baseName = AndHowTest.class.getCanonicalName();
		baseName += "." + RequiredParams.class.getSimpleName() + ".";
		
		try {
				AndHowConfiguration config = AndHowCoreTestConfig.instance()
					.group(RequiredParams.class)
					.addCmdLineArg(baseName + "STR_NULL_R", "zzz")
					.addCmdLineArg(baseName + "FLAG_NULL", "present");
				
				AndHow.instance(config);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(ValueProblem.class).size());
			assertEquals(RequiredParams.STR_NULL_R, ce.getProblems().filter(ValueProblem.class).get(0).getBadValueCoord().getProperty());
		}
	}
	

	
}
