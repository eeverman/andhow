package yarnandtail.andhow.load;

import java.util.ArrayList;
import yarnandtail.andhow.ConfigPoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.AppConfig;
import yarnandtail.andhow.AppConfigDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.SimpleParamsWAlias;

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
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_NULL, bns.buildNames(SimpleParamsWAlias.KVP_NULL, SimpleParamsWAlias.class, "KVP_NULL"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_TRUE, bns.buildNames(SimpleParamsWAlias.FLAG_TRUE, SimpleParamsWAlias.class, "FLAG_TRUE"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_NULL, bns.buildNames(SimpleParamsWAlias.FLAG_NULL, SimpleParamsWAlias.class, "FLAG_NULL"));

		loaderState.setAppConfigDef(appDef);
	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseNames() {
		
		String basePath = SimpleParamsWAlias.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0) + AppConfig.KVP_DELIMITER + "test");
		args.add(basePath + SimpleParamsWAlias.KVP_NULL.getBaseAliases().get(0) + AppConfig.KVP_DELIMITER + "not_null");
		args.add(basePath + SimpleParamsWAlias.FLAG_TRUE.getBaseAliases().get(0) + AppConfig.KVP_DELIMITER + "false");
		args.add(basePath + SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0) + AppConfig.KVP_DELIMITER + "true");
		args.add(basePath + SimpleParamsWAlias.FLAG_NULL.getBaseAliases().get(0) + AppConfig.KVP_DELIMITER + "true");
		loaderState.setCmdLineArgs(args);
		
		
		CmdLineLoader cll = new CmdLineLoader();
		
		Map<ConfigPoint<?>, Object> result = cll.load(loaderState);
		
		assertEquals("test", result.get(SimpleParamsWAlias.KVP_BOB));
		assertEquals("not_null", result.get(SimpleParamsWAlias.KVP_NULL));
		assertEquals(Boolean.FALSE, result.get(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.get(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.get(SimpleParamsWAlias.FLAG_NULL));
	}

	

}
