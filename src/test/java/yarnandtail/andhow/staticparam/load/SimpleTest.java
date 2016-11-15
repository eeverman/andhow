package yarnandtail.andhow.staticparam.load;

import yarnandtail.andhow.staticparam.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author eeverman
 */
public class SimpleTest {
	
	TestLoaderState loaderState = new TestLoaderState();
	
	@Before
	public void init() {
		Map<String, ConfigPoint> points = new HashMap();
		points.put(SimpleParams.KVP_BOB.getExplicitBaseName(), SimpleParams.KVP_BOB);
		points.put(SimpleParams.KVP_NULL.getExplicitBaseName(), SimpleParams.KVP_NULL);
		points.put(SimpleParams.FLAG_FALSE.getExplicitBaseName(), SimpleParams.FLAG_FALSE);
		points.put(SimpleParams.FLAG_TRUE.getExplicitBaseName(), SimpleParams.FLAG_TRUE);
		points.put(SimpleParams.FLAG_NULL.getExplicitBaseName(), SimpleParams.FLAG_NULL);
		
		loaderState.setRegisteredConfigPoints(points);
	}
	
	@Test
	public void testCmdLineLoader() {
		String[] args = new String[5];
		args[0] = SimpleParams.KVP_BOB.getExplicitBaseName() + CmdLineLoader.KVP_DELIMITER + "test";
		args[1] = SimpleParams.KVP_NULL.getExplicitBaseName() + CmdLineLoader.KVP_DELIMITER + "not_null";
		args[2] = SimpleParams.FLAG_TRUE.getExplicitBaseName() + CmdLineLoader.KVP_DELIMITER + "false";
		args[3] = SimpleParams.FLAG_FALSE.getExplicitBaseName() + CmdLineLoader.KVP_DELIMITER + "true";
		args[4] = SimpleParams.FLAG_NULL.getExplicitBaseName() + CmdLineLoader.KVP_DELIMITER + "true";
		loaderState.setCmdLineArgs(args);
		
		
		CmdLineLoader cll = new CmdLineLoader();
		
		Map<ConfigPoint<?>, Object> result = cll.load(loaderState);
		
		assertEquals("test", result.get(SimpleParams.KVP_BOB));
		assertEquals("not_null", result.get(SimpleParams.KVP_NULL));
		assertEquals(Boolean.FALSE, result.get(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.get(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.get(SimpleParams.FLAG_NULL));
	}

	

}
