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

/**
 *
 * @author eeverman
 */
public class AndHowTest extends AndHowTestBase {
	
	String paramFullPath = SimpleParamsWAlias.class.getCanonicalName() + ".";
	BasicNamingStrategy basicNaming = new BasicNamingStrategy();
	List<Loader> loaders = new ArrayList();
	ArrayList<Class<? extends PropertyGroup>> configPtGroups = new ArrayList();
	Map<Property<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	
	@Before
	public void setup() {
		
		loaders.clear();
		loaders.add(new CmdLineLoader());
		
		configPtGroups.clear();
		configPtGroups.add(SimpleParamsWAlias.class);
		
		startVals.clear();
		startVals.put(SimpleParamsWAlias.KVP_BOB, "test");
		startVals.put(SimpleParamsWAlias.KVP_NULL, "not_null");
		startVals.put(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE);
		startVals.put(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE);
		startVals.put(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE);
		
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
		//test the test
		assertEquals("yarnandtail.andhow.SimpleParamsWAlias.", paramFullPath);
	}
	
	@Test
	public void testForcingValuesViaAppConfig() {
		
		AndHow.builder().namingStrategy(basicNaming)
				.loader(new CmdLineLoader())
				.group(SimpleParamsWAlias.class)
				.forceValue(SimpleParamsWAlias.KVP_BOB, "test")
				.forceValue(SimpleParamsWAlias.KVP_NULL, "not_null")
				.forceValue(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE)
				.forceValue(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE)
				.forceValue(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
		
		
		List<Property<?>> regPts = AndHow.instance().getProperties();
		
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_BOB));
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_NULL));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_NULL));
	}
	
	@Test
	public void testDefaultValuesViaLoadingWithNoUserValuesSet() {
		
		AndHow.builder().namingStrategy(basicNaming)
				.loader(new CmdLineLoader())
				.group(SimpleParamsWAlias.class)
				.reloadForNonPropduction(reloader);
		
		assertEquals("bob", SimpleParamsWAlias.KVP_BOB.getValue());
		assertNull(SimpleParamsWAlias.KVP_NULL.getValue());
		assertTrue(SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertFalse(SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertFalse(SimpleParamsWAlias.FLAG_NULL.getValue());
		
		//Test for the presense of the registered param after the reset
		List<Property<?>> regPts = AndHow.instance().getProperties();
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_BOB));
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_NULL));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_NULL));
	}
	
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		AndHow.builder()
				.namingStrategy(basicNaming)
				.loaders(loaders)
				.groups(configPtGroups)
				.cmdLineArgs(cmdLineArgsWFullClassName)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
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
					.group(SimpleParamsNoAliasRequired.class)
					.cmdLineArgs(cmdLineArgsWFullClassName)
					.reloadForNonPropduction(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(2, ce.getRequirementProblems().size());
			assertEquals(SimpleParamsNoAliasRequired.KVP_NULL, ce.getRequirementProblems().get(0).getPropertyCoord().getProperty());
			assertEquals(SimpleParamsNoAliasRequired.FLAG_NULL, ce.getRequirementProblems().get(1).getPropertyCoord().getProperty());
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testAttemptingToFetchAPropValueBeforeConfigurationShouldThrowARuntimeException() {
		reloader.destroy();
		String shouldFail = SimpleParamsWAlias.KVP_BOB.getValue();
	}
	

}
