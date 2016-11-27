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
import yarnandtail.andhow.appconfig.AppConfigDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.SimpleParamsWAlias;

/**
 *
 * @author eeverman
 */
public class PropFileLoaderTest {
	
	TestLoaderState loaderState = new TestLoaderState();
	
	@Before
	public void init() {
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		AppConfigDefinition appDef = new AppConfigDefinition();
		
		appDef.addPoint(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.CLASSPATH_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.CLASSPATH_PATH, PropFileLoader.CONFIG.class, "CLASSPATH_PATH"));
		appDef.addPoint(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH, PropFileLoader.CONFIG.class, "EXECUTABLE_RELATIVE_PATH"));
		appDef.addPoint(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.FILESYSTEM_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.FILESYSTEM_PATH, PropFileLoader.CONFIG.class, "FILESYSTEM_PATH"));

		
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_NULL, bns.buildNames(SimpleParamsWAlias.KVP_NULL, SimpleParamsWAlias.class, "KVP_NULL"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_TRUE, bns.buildNames(SimpleParamsWAlias.FLAG_TRUE, SimpleParamsWAlias.class, "FLAG_TRUE"));
		appDef.addPoint(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_NULL, bns.buildNames(SimpleParamsWAlias.FLAG_NULL, SimpleParamsWAlias.class, "FLAG_NULL"));

		loaderState.setAppConfigDef(appDef);
	}
	
	@Test
	public void testPropFileLoader() {
		
		String basePath = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
		
		Map<ConfigPoint<?>, Object> existingValues = new HashMap();
		existingValues.put(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/example.properties");
		loaderState.getExistingValues().add(existingValues);
		
		
		PropFileLoader cll = new PropFileLoader();
		
		Map<ConfigPoint<?>, Object> result = cll.load(loaderState);
		
		assertEquals("kvpBobValue", result.get(SimpleParamsWAlias.KVP_BOB));
		assertEquals("kvpNullValue", result.get(SimpleParamsWAlias.KVP_NULL));
		assertEquals(Boolean.FALSE, result.get(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.get(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.get(SimpleParamsWAlias.FLAG_NULL));
	}

	

}
