package org.yarnandtail.andhow;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.ConstructionProblem;
import org.yarnandtail.andhow.internal.RequirementProblem;
import org.yarnandtail.andhow.load.StringArgumentLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 *
 * @author eeverman
 */
public class AndHowTest extends AndHowTestBase {
	
	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	CaseInsensitiveNaming basicNaming = new CaseInsensitiveNaming();
	ArrayList<Class<?>> configPtGroups = new ArrayList();
	Map<Property<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	
	public static interface RequiredParams extends PropertyGroup {
		StrProp STR_BOB_R = StrProp.builder().defaultValue("Bob").mustBeNonNull().build();
		StrProp STR_NULL_R = StrProp.builder().mustBeNonNull().build();
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).mustBeNonNull().build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).mustBeNonNull().build();
		FlagProp FLAG_NULL = FlagProp.builder().mustBeNonNull().build();
	}
	
	@Before
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
			paramFullPath + "STR_BOB" + StringArgumentLoader.KVP_DELIMITER + "test",
			paramFullPath + "STR_NULL" + StringArgumentLoader.KVP_DELIMITER + "not_null",
			paramFullPath + "FLAG_TRUE" + StringArgumentLoader.KVP_DELIMITER + "false",
			paramFullPath + "FLAG_FALSE" + StringArgumentLoader.KVP_DELIMITER + "true",
			paramFullPath + "FLAG_NULL" + StringArgumentLoader.KVP_DELIMITER + "true"
		};
		
	}
	
	@Test
	public void testTheTest() {
		//This could be generalized to use the class.getCanonicalName(),
		//but this one place we make it explicit
		assertEquals("org.yarnandtail.andhow.SimpleParams.", paramFullPath);
	}
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		AndHow.builder()
				.namingStrategy(basicNaming)
				.groups(configPtGroups)
				.cmdLineArgs(cmdLineArgsWFullClassName)
				.reloadForNonPropduction(reloader);
		
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
	
	@Test
	public void testBlowingUpWithDuplicateLoaders() {
		
		List<Loader> loaders = new ArrayList();
		loaders.add(new StringArgumentLoader(cmdLineArgsWFullClassName));
		
		try {

			AndHow.builder()
				.loaders(loaders)
				.loader(loaders.get(0))
				.groups(configPtGroups)
				.reloadForNonPropduction(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(ConstructionProblem.class).size());
			assertTrue(ce.getProblems().filter(ConstructionProblem.class).get(0) instanceof ConstructionProblem.DuplicateLoader);
			
			ConstructionProblem.DuplicateLoader dl = (ConstructionProblem.DuplicateLoader)ce.getProblems().filter(ConstructionProblem.class).get(0);
			assertEquals(loaders.get(0), dl.getLoader());
		}
	}
	
	@Test
	public void testCmdLineLoaderMissingRequiredParamShouldThrowAConfigException() {

		try {
				AndHow.builder()
					.namingStrategy(basicNaming)
					.groups(configPtGroups)
					.group(RequiredParams.class)
					.cmdLineArgs(cmdLineArgsWFullClassName)
					.reloadForNonPropduction(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getProblems().filter(RequirementProblem.class).size());
			assertEquals(RequiredParams.STR_NULL_R, ce.getProblems().filter(RequirementProblem.class).get(0).getPropertyCoord().getProperty());
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testAttemptingToFetchAPropValueBeforeConfigurationShouldThrowARuntimeException() {
		reloader.destroy();
		String shouldFail = SimpleParams.STR_BOB.getValue();
	}
	

}
