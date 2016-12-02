package yarnandtail.andhow.load;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.LoaderException;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.appconfig.AppConfigDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.SimpleParamsWAlias;
import yarnandtail.andhow.appconfig.AppConfigStructuredValuesBuilder;

/**
 *
 * @author eeverman
 */
public class PropFileLoaderTest {
	
	AppConfigDefinition appDef;
	AppConfigStructuredValuesBuilder appValuesBuilder;
	ArrayList<LoaderException> loaderExceptions;
	
	@Before
	public void init() {
		
		appValuesBuilder = new AppConfigStructuredValuesBuilder();
		loaderExceptions = new ArrayList();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new AppConfigDefinition();
		
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

	}
	
	@Test
	public void testPropFileLoader() {
		
		String basePath = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
		
		
		
		ArrayList<PointValue> evl = new ArrayList();
		evl.add(new PointValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/example.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl);
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder, loaderExceptions);
		
		assertEquals("kvpBobValue", result.getValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("kvpNullValue", result.getValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(Boolean.FALSE, result.getValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getValue(SimpleParamsWAlias.FLAG_NULL));
	}

	

}
