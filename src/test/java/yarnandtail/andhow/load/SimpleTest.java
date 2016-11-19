package yarnandtail.andhow.load;

import java.util.ArrayList;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.SimpleParams;
import java.util.HashMap;
import java.util.List;
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
		Map<String, ConfigPoint<?>> points = new HashMap();
		points.put(SimpleParams.KVP_BOB.getBaseAliases().get(0), SimpleParams.KVP_BOB);
		points.put(SimpleParams.KVP_NULL.getBaseAliases().get(0), SimpleParams.KVP_NULL);
		points.put(SimpleParams.FLAG_FALSE.getBaseAliases().get(0), SimpleParams.FLAG_FALSE);
		points.put(SimpleParams.FLAG_TRUE.getBaseAliases().get(0), SimpleParams.FLAG_TRUE);
		points.put(SimpleParams.FLAG_NULL.getBaseAliases().get(0), SimpleParams.FLAG_NULL);
		
		loaderState.setRegisteredConfigPoints(points);
	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseNames() {
		List<String> args = new ArrayList();
		args.add(SimpleParams.KVP_BOB.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "test");
		args.add(SimpleParams.KVP_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "not_null");
		args.add(SimpleParams.FLAG_TRUE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "false");
		args.add(SimpleParams.FLAG_FALSE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true");
		args.add(SimpleParams.FLAG_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true");
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
