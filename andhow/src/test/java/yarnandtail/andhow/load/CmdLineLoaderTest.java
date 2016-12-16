package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.AndHow;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.SimpleParamsWAlias;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;

/**
 *
 * @author eeverman
 */
public class CmdLineLoaderTest {
	
	RuntimeDefinition appDef;
	ValueMapWithContextMutable appValuesBuilder;

	@Before
	public void init() {
		appValuesBuilder = new ValueMapWithContextMutable();
		
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new RuntimeDefinition();
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_BOB, bns.buildNames(SimpleParamsWAlias.KVP_BOB, SimpleParamsWAlias.class, "KVP_BOB"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.KVP_NULL, bns.buildNames(SimpleParamsWAlias.KVP_NULL, SimpleParamsWAlias.class, "KVP_NULL"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_FALSE, bns.buildNames(SimpleParamsWAlias.FLAG_FALSE, SimpleParamsWAlias.class, "FLAG_FALSE"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_TRUE, bns.buildNames(SimpleParamsWAlias.FLAG_TRUE, SimpleParamsWAlias.class, "FLAG_TRUE"));
		appDef.addProperty(SimpleParamsWAlias.class, SimpleParamsWAlias.FLAG_NULL, bns.buildNames(SimpleParamsWAlias.FLAG_NULL, SimpleParamsWAlias.class, "FLAG_NULL"));

	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseNames() {
		
		String basePath = SimpleParamsWAlias.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "test");
		args.add(basePath + SimpleParamsWAlias.KVP_NULL.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "not_null");
		args.add(basePath + SimpleParamsWAlias.FLAG_TRUE.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "false");
		args.add(basePath + SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "true");
		args.add(basePath + SimpleParamsWAlias.FLAG_NULL.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "true");
		
		
		CmdLineLoader cll = new CmdLineLoader();
		
		LoaderValues result = cll.load(appDef, args, appValuesBuilder);
		
		assertEquals("test", result.getExplicitValue(SimpleParamsWAlias.KVP_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParamsWAlias.KVP_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParamsWAlias.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParamsWAlias.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParamsWAlias.FLAG_NULL));
	}

	

}
