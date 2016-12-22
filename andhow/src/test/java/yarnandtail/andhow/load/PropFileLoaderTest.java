package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;
import yarnandtail.andhow.SimpleParams;

/**
 *
 * @author eeverman
 */
public class PropFileLoaderTest {
	
	RuntimeDefinition appDef;
	ValueMapWithContextMutable appValuesBuilder;
	
	@Before
	public void init() {
		
		appValuesBuilder = new ValueMapWithContextMutable();
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

		
		appDef.addProperty(SimpleParams.class, SimpleParams.KVP_BOB, bns.buildNames(SimpleParams.KVP_BOB, SimpleParams.class, "KVP_BOB"));
		appDef.addProperty(SimpleParams.class, SimpleParams.KVP_NULL, bns.buildNames(SimpleParams.KVP_NULL, SimpleParams.class, "KVP_NULL"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE, bns.buildNames(SimpleParams.FLAG_FALSE, SimpleParams.class, "FLAG_FALSE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE, bns.buildNames(SimpleParams.FLAG_TRUE, SimpleParams.class, "FLAG_TRUE"));
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL, bns.buildNames(SimpleParams.FLAG_NULL, SimpleParams.class, "FLAG_NULL"));

	}
	
	@Test
	public void testPropFileLoader() {
		
		String basePath = PropFileLoader.CONFIG.class.getCanonicalName() + ".";
		
		
		
		ArrayList<PropertyValue> evl = new ArrayList();
		evl.add(new PropertyValue(PropFileLoader.CONFIG.CLASSPATH_PATH, "/yarnandtail/andhow/load/SimpleParams1.properties"));
		LoaderValues existing = new LoaderValues(new CmdLineLoader(), evl, Collections.emptyList());
		appValuesBuilder.addValues(existing);
		
		PropFileLoader cll = new PropFileLoader();
		
		LoaderValues result = cll.load(appDef, null, appValuesBuilder);
		
		assertEquals("kvpBobValue", result.getExplicitValue(SimpleParams.KVP_BOB));
		assertEquals("kvpNullValue", result.getExplicitValue(SimpleParams.KVP_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	

}
