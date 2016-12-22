package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.property.FlagProp;
import yarnandtail.andhow.property.StrProp;

/**
 *
 * @author eeverman
 */
public class AndHowTest extends AndHowTestBase {
	
	String paramFullPath = SimpleParams.class.getCanonicalName() + ".";
	BasicNamingStrategy basicNaming = new BasicNamingStrategy();
	List<Loader> loaders = new ArrayList();
	ArrayList<Class<? extends PropertyGroup>> configPtGroups = new ArrayList();
	Map<Property<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	
	public static interface RequiredParams extends PropertyGroup {
		StrProp KVP_BOB = StrProp.builder().defaultValue("Bob").required().build();
		StrProp KVP_NULL = StrProp.builder().required().build();
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).required().build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).required().build();
		FlagProp FLAG_NULL = FlagProp.builder().required().build();
	}
	
	@Before
	public void setup() {
		
		loaders.clear();
		loaders.add(new CmdLineLoader());
		
		configPtGroups.clear();
		configPtGroups.add(SimpleParams.class);
		
		startVals.clear();
		startVals.put(SimpleParams.KVP_BOB, "test");
		startVals.put(SimpleParams.KVP_NULL, "not_null");
		startVals.put(SimpleParams.FLAG_TRUE, Boolean.FALSE);
		startVals.put(SimpleParams.FLAG_FALSE, Boolean.TRUE);
		startVals.put(SimpleParams.FLAG_NULL, Boolean.TRUE);
		
		cmdLineArgsWFullClassName = new String[] {
			paramFullPath + "KVP_BOB" + AndHow.KVP_DELIMITER + "test",
			paramFullPath + "KVP_NULL" + AndHow.KVP_DELIMITER + "not_null",
			paramFullPath + "FLAG_TRUE" + AndHow.KVP_DELIMITER + "false",
			paramFullPath + "FLAG_FALSE" + AndHow.KVP_DELIMITER + "true",
			paramFullPath + "FLAG_NULL" + AndHow.KVP_DELIMITER + "true"
		};
		
	}
	
	@Test
	public void testTheTest() {
		//This could be generalized to use the class.getCanonicalName(),
		//but this one place we make it explicit
		assertEquals("yarnandtail.andhow.SimpleParams.", paramFullPath);
	}
	
	@Test
	public void testForcingValuesViaAppConfig() {
		
		AndHow.builder().namingStrategy(basicNaming)
				.loader(new CmdLineLoader())
				.group(SimpleParams.class)
				.forceValue(SimpleParams.KVP_BOB, "test")
				.forceValue(SimpleParams.KVP_NULL, "not_null")
				.forceValue(SimpleParams.FLAG_TRUE, Boolean.FALSE)
				.forceValue(SimpleParams.FLAG_FALSE, Boolean.TRUE)
				.forceValue(SimpleParams.FLAG_NULL, Boolean.TRUE)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		
		
		List<Property<?>> regPts = AndHow.instance().getProperties();
		
		assertTrue(regPts.contains(SimpleParams.KVP_BOB));
		assertTrue(regPts.contains(SimpleParams.KVP_NULL));
		assertTrue(regPts.contains(SimpleParams.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParams.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testDefaultValuesViaLoadingWithNoUserValuesSet() {
		
		AndHow.builder().namingStrategy(basicNaming)
				.loader(new CmdLineLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		assertEquals("bob", SimpleParams.KVP_BOB.getValue());
		assertNull(SimpleParams.KVP_NULL.getValue());
		assertTrue(SimpleParams.FLAG_TRUE.getValue());
		assertFalse(SimpleParams.FLAG_FALSE.getValue());
		assertFalse(SimpleParams.FLAG_NULL.getValue());
		
		//Test for the presense of the registered param after the reset
		List<Property<?>> regPts = AndHow.instance().getProperties();
		assertTrue(regPts.contains(SimpleParams.KVP_BOB));
		assertTrue(regPts.contains(SimpleParams.KVP_NULL));
		assertTrue(regPts.contains(SimpleParams.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParams.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParams.FLAG_NULL));
	}
	
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		AndHow.builder()
				.namingStrategy(basicNaming)
				.loaders(loaders)
				.groups(configPtGroups)
				.cmdLineArgs(cmdLineArgsWFullClassName)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
	}
	
	@Test
	public void testBlowingUpWithDuplicateLoaders() {
		
		try {

			AndHow.builder()
				.loaders(loaders)
				.loader(loaders.get(0))
				.groups(configPtGroups)
				.reloadForNonPropduction(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getConstructionProblems().size());
			assertTrue(ce.getConstructionProblems().get(0) instanceof ConstructionProblem.DuplicateLoader);
			
			ConstructionProblem.DuplicateLoader dl = (ConstructionProblem.DuplicateLoader)ce.getConstructionProblems().get(0);
			assertEquals(loaders.get(0), dl.getLoader());
		}
	}
	
	@Test
	public void testCmdLineLoaderMissingRequiredParamShouldThrowAConfigException() {

		try {
				AndHow.builder()
					.namingStrategy(basicNaming)
					.loaders(loaders)
					.groups(configPtGroups)
					.group(RequiredParams.class)
					.cmdLineArgs(cmdLineArgsWFullClassName)
					.reloadForNonPropduction(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(2, ce.getRequirementProblems().size());
			assertEquals(RequiredParams.KVP_NULL, ce.getRequirementProblems().get(0).getPropertyCoord().getProperty());
			assertEquals(RequiredParams.FLAG_NULL, ce.getRequirementProblems().get(1).getPropertyCoord().getProperty());
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testAttemptingToFetchAPropValueBeforeConfigurationShouldThrowARuntimeException() {
		reloader.destroy();
		String shouldFail = SimpleParams.KVP_BOB.getValue();
	}
	

}
