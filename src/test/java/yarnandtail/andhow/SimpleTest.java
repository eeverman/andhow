package yarnandtail.andhow;

import java.util.ArrayList;
import yarnandtail.andhow.SimpleParams;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.AppConfig;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author eeverman
 */
public class SimpleTest {
	
	ArrayList<Class<? extends ConfigPointGroup>> configPtGroups = new ArrayList();
	HashMap<ConfigPoint, String> startVals = new HashMap();
	
	@Before
	public void setup() {
		
		configPtGroups.clear();
		configPtGroups.add(SimpleParams.class);
		
		startVals.clear();
		startVals.put(SimpleParams.KVP_BOB, "test");
		startVals.put(SimpleParams.KVP_NULL, "not_null");
		startVals.put(SimpleParams.FLAG_TRUE, "false");
		startVals.put(SimpleParams.FLAG_FALSE, "true");
		startVals.put(SimpleParams.FLAG_NULL, "true");
	}
	
	@Test
	public void testAssingingValues() {
		
		AppConfig.reset(configPtGroups, startVals);
		
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		
		
		List<ConfigPoint> regPts = AppConfig.instance().getRegisteredConfigPoints();
		
		assertTrue(regPts.contains(SimpleParams.KVP_BOB));
		assertTrue(regPts.contains(SimpleParams.KVP_NULL));
		assertTrue(regPts.contains(SimpleParams.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParams.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParams.FLAG_NULL));
		
		
	}
	
	@Test
	public void testDefaultValues() {
		
		AppConfig.reset(configPtGroups, null);
		
		assertEquals("bob", SimpleParams.KVP_BOB.getValue());
		assertNull(SimpleParams.KVP_NULL.getValue());
		assertTrue(SimpleParams.FLAG_TRUE.getValue());
		assertFalse(SimpleParams.FLAG_FALSE.getValue());
		assertNull(SimpleParams.FLAG_NULL.getValue());
		
		//Test for the presense of the registered param after the reset
		List<ConfigPoint> regPts = AppConfig.instance().getRegisteredConfigPoints();
		assertTrue(regPts.contains(SimpleParams.KVP_BOB));
		assertTrue(regPts.contains(SimpleParams.KVP_NULL));
		assertTrue(regPts.contains(SimpleParams.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParams.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParams.FLAG_NULL));
	}
	

}
