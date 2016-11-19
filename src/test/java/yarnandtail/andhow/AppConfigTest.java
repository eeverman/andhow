package yarnandtail.andhow;

import java.util.ArrayList;
import yarnandtail.andhow.SimpleParams;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.AppConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.load.CmdLineLoader;

/**
 *
 * @author eeverman
 */
public class AppConfigTest {
	
	List<Loader> loaders = new ArrayList();
	ArrayList<Class<? extends ConfigPointGroup>> configPtGroups = new ArrayList();
	HashMap<ConfigPoint<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	String[] cmdLineArgsWExplicitName = new String[0];
	
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
		
		String paramFullPath = SimpleParams.class.getCanonicalName();
		//test the test
		assertEquals("yarnandtail.andhow.SimpleParams", paramFullPath);
		cmdLineArgsWFullClassName = new String[] {
			paramFullPath + ".KVP_BOB" + CmdLineLoader.KVP_DELIMITER + "test",
			paramFullPath + ".KVP_NULL" + CmdLineLoader.KVP_DELIMITER + "not_null",
			paramFullPath + ".FLAG_TRUE" + CmdLineLoader.KVP_DELIMITER + "false",
			paramFullPath + ".FLAG_FALSE" + CmdLineLoader.KVP_DELIMITER + "true",
			paramFullPath + ".FLAG_NULL" + CmdLineLoader.KVP_DELIMITER + "true"
		};
		
		cmdLineArgsWExplicitName = new String[] {
			paramFullPath + SimpleParams.KVP_BOB.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "test",
			paramFullPath + SimpleParams.KVP_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "not_null",
			paramFullPath + SimpleParams.FLAG_TRUE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "false",
			paramFullPath + SimpleParams.FLAG_FALSE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true",
			paramFullPath + SimpleParams.FLAG_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true"
		};
	}
	
	@Test
	public void testForcingValuesViaAppConfig() {
		
		AppConfig.reset(loaders, configPtGroups, null, startVals);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		
		
		List<ConfigPoint<?>> regPts = AppConfig.instance().getPoints();
		
		assertTrue(regPts.contains(SimpleParams.KVP_BOB));
		assertTrue(regPts.contains(SimpleParams.KVP_NULL));
		assertTrue(regPts.contains(SimpleParams.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParams.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testDefaultValuesViaLoadingWithNoUserValuesSet() {
		
		AppConfig.reset(loaders, configPtGroups, null, null);
		
		assertEquals("bob", SimpleParams.KVP_BOB.getValue());
		assertNull(SimpleParams.KVP_NULL.getValue());
		assertTrue(SimpleParams.FLAG_TRUE.getValue());
		assertFalse(SimpleParams.FLAG_FALSE.getValue());
		assertNull(SimpleParams.FLAG_NULL.getValue());
		
		//Test for the presense of the registered param after the reset
		List<ConfigPoint<?>> regPts = AppConfig.instance().getPoints();
		assertTrue(regPts.contains(SimpleParams.KVP_BOB));
		assertTrue(regPts.contains(SimpleParams.KVP_NULL));
		assertTrue(regPts.contains(SimpleParams.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParams.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseName() {
		AppConfig.reset(loaders, configPtGroups, cmdLineArgsWExplicitName, null);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
	}
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		AppConfig.reset(loaders, configPtGroups, cmdLineArgsWFullClassName, null);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
	}
	

}
