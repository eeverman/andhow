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
import yarnandtail.andhow.AppConfigDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class CmdLineLoaderTest {
	
	TestLoaderState loaderState = new TestLoaderState();
	
	@Before
	public void init() {
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		appDef.addPoint(SimpleParams.class, SimpleParams.KVP_BOB, bns.buildNames(SimpleParams.KVP_BOB, SimpleParams.class, "KVP_BOB"));
		appDef.addPoint(SimpleParams.class, SimpleParams.KVP_NULL, bns.buildNames(SimpleParams.KVP_NULL, SimpleParams.class, "KVP_NULL"));
		appDef.addPoint(SimpleParams.class, SimpleParams.FLAG_FALSE, bns.buildNames(SimpleParams.FLAG_FALSE, SimpleParams.class, "FLAG_FALSE"));
		appDef.addPoint(SimpleParams.class, SimpleParams.FLAG_TRUE, bns.buildNames(SimpleParams.FLAG_TRUE, SimpleParams.class, "FLAG_TRUE"));
		appDef.addPoint(SimpleParams.class, SimpleParams.FLAG_NULL, bns.buildNames(SimpleParams.FLAG_NULL, SimpleParams.class, "FLAG_NULL"));

		loaderState.setAppConfigDef(appDef);
	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseNames() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + SimpleParams.KVP_BOB.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "test");
		args.add(basePath + SimpleParams.KVP_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + SimpleParams.FLAG_TRUE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "false");
		args.add(basePath + SimpleParams.FLAG_FALSE.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true");
		args.add(basePath + SimpleParams.FLAG_NULL.getBaseAliases().get(0) + CmdLineLoader.KVP_DELIMITER + "true");
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
