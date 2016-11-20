package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.load.CmdLineLoader;

/**
 *
 * @author eeverman
 */
public class AppConfigTest {
	
	String paramFullPath = SimpleParamsWAlias.class.getCanonicalName() + ".";
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
		configPtGroups.add(SimpleParamsWAlias.class);
		
		startVals.clear();
		startVals.put(SimpleParamsWAlias.KVP_BOB, "test");
		startVals.put(SimpleParamsWAlias.KVP_NULL, "not_null");
		startVals.put(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE);
		startVals.put(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE);
		startVals.put(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE);
		
		cmdLineArgsWFullClassName = new String[] {
			paramFullPath + "KVP_BOB" + CmdLineLoader.KVP_DELIMITER + "test",
			paramFullPath + "KVP_NULL" + CmdLineLoader.KVP_DELIMITER + "not_null",
			paramFullPath + "FLAG_TRUE" + CmdLineLoader.KVP_DELIMITER + "false",
			paramFullPath + "FLAG_FALSE" + CmdLineLoader.KVP_DELIMITER + "true",
			paramFullPath + "FLAG_NULL" + CmdLineLoader.KVP_DELIMITER + "true"
		};
		
		cmdLineArgsWExplicitName = new String[] {
			paramFullPath + SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "test",
			paramFullPath + SimpleParamsWAlias.KVP_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "not_null",
			paramFullPath + SimpleParamsWAlias.FLAG_TRUE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "false",
			paramFullPath + SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true",
			paramFullPath + SimpleParamsWAlias.FLAG_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true"
		};
	}
	
	@Test
	public void testTheTest() {
		//test the test
		assertEquals("yarnandtail.andhow.SimpleParamsWAlias.", paramFullPath);
	}
	
	@Test
	public void testForcingValuesViaAppConfig() {
		
		AppConfig.reset(loaders, configPtGroups, null, startVals);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
		
		
		List<ConfigPoint<?>> regPts = AppConfig.instance().getPoints();
		
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_BOB));
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_NULL));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_NULL));
	}
	
	@Test
	public void testDefaultValuesViaLoadingWithNoUserValuesSet() {
		
		AppConfig.reset(loaders, configPtGroups, null, null);
		
		assertEquals("bob", SimpleParamsWAlias.KVP_BOB.getValue());
		assertNull(SimpleParamsWAlias.KVP_NULL.getValue());
		assertTrue(SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertFalse(SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertNull(SimpleParamsWAlias.FLAG_NULL.getValue());
		
		//Test for the presense of the registered param after the reset
		List<ConfigPoint<?>> regPts = AppConfig.instance().getPoints();
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_BOB));
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_NULL));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_NULL));
	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseName() {
		AppConfig.reset(loaders, configPtGroups, cmdLineArgsWExplicitName, null);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
	}
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		AppConfig.reset(loaders, configPtGroups, cmdLineArgsWFullClassName, null);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
	}
	

}
