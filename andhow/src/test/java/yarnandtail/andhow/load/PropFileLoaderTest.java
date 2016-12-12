package yarnandtail.andhow.load;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.LoaderException;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.SimpleParamsWAlias;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;

/**
 *
 * @author eeverman
 */
public class PropFileLoaderTest {
	
	RuntimeDefinition appDef;
	ValueMapWithContextMutable appValuesBuilder;
	ArrayList<LoaderException> loaderExceptions;
	
	@Before
	public void init() {
		
		appValuesBuilder = new ValueMapWithContextMutable();
		loaderExceptions = new ArrayList();
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new RuntimeDefinition();
		
		appDef.addProperty(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.CLASSPATH_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.CLASSPATH_PATH, PropFileLoader.CONFIG.class, "CLASSPATH_PATH"));
		appDef.addProperty(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.EXECUTABLE_RELATIVE_PATH, PropFileLoader.CONFIG.class, "EXECUTABLE_RELATIVE_PATH"));
		appDef.addProperty(PropFileLoader.CONFIG.class, 
				PropFileLoader.CONFIG.FILESYSTEM_PATH, 
				bns.buildNames(PropFileLoader.CONFIG.FILESYSTEM_PATH, PropFileLoader.CONFIG.class, "FILESYSTEM_PATH"));

		
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_NULL, bns.buildNames(SimpleParamsWAlias.KVP_NULL, SimpleParamsWAlias.class, "KVP_NULL"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_TRUE, bns.buildNames(SimpleParamsWAlias.FLAG_TRUE, SimpleParamsWAlias.class, "FLAG_TRUE"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_NULL, bns.buildNames(SimpleParamsWAlias.FLAG_NULL, SimpleParamsWAlias.class, "FLAG_NULL"));

	}
	
	@Test
	public void testPropFileLoader() {
		
		String basePath = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
		
		
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/example.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl);
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder, loaderExceptions);
		
		assertEquals("kvpBobValue", result.getExplicitValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("kvpNullValue", result.getExplicitValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParamsWAlias.FLAG_NULL));
	}

	

}
