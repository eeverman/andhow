package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.AndHow;
import yarnandtail.andhow.LoaderProblem;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.internal.ConstructionDefinitionMutable;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.internal.ValueMapWithContextMutable;
import yarnandtail.andhow.SimpleParams;

/**
 *
 * @author eeverman
 */
public class CmdLineLoaderTest {
	
	ConstructionDefinitionMutable appDef;
	ValueMapWithContextMutable appValuesBuilder;

	@Before
	public void init() throws Exception {
		appValuesBuilder = new ValueMapWithContextMutable();
		
		BasicNamingStrategy bns = new BasicNamingStrategy();
		
		appDef = new ConstructionDefinitionMutable(bns);
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_BOB);
		appDef.addProperty(SimpleParams.class, SimpleParams.STR_NULL);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_FALSE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_TRUE);
		appDef.addProperty(SimpleParams.class, SimpleParams.FLAG_NULL);

	}
	
	@Test
	public void testCmdLineLoaderHappyPath() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + AndHow.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + AndHow.KVP_DELIMITER + "not_null");
		args.add(basePath + "FLAG_TRUE" + AndHow.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + AndHow.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + AndHow.KVP_DELIMITER + "true");
		
		
		CmdLineLoader cll = new CmdLineLoader();
		
		LoaderValues result = cll.load(appDef, args, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	

	@Test
	public void testCmdLineLoaderEmptyValues() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + AndHow.KVP_DELIMITER + "");
		args.add(basePath + "STR_NULL" + AndHow.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_TRUE" + AndHow.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_FALSE" + AndHow.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_NULL" + AndHow.KVP_DELIMITER + "");
		
		CmdLineLoader cll = new CmdLineLoader();
		
		LoaderValues result = cll.load(appDef, args, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getEffectiveValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getEffectiveValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testCmdLineLoaderDuplicateValuesAndSpaces() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_NULL" + AndHow.KVP_DELIMITER + "1");
		args.add(basePath + "STR_NULL" + AndHow.KVP_DELIMITER + "2");
		args.add(basePath + "STR_NULL" + AndHow.KVP_DELIMITER + "3");
		args.add(basePath + "FLAG_NULL" + AndHow.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + AndHow.KVP_DELIMITER + "false");
		
		
		CmdLineLoader cll = new CmdLineLoader();
		
		LoaderValues result = cll.load(appDef, args, appValuesBuilder);
		
		assertEquals(3, result.getProblems().size());
		for (LoaderProblem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
	}
	
	@Test
	public void testCmdLineLoaderWithUnknownProperties() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "XXX" + AndHow.KVP_DELIMITER + "1");
		args.add(basePath + "YYY" + AndHow.KVP_DELIMITER + "2");
		
		
		CmdLineLoader cll = new CmdLineLoader();
		
		LoaderValues result = cll.load(appDef, args, appValuesBuilder);
		
		assertEquals(2, result.getProblems().size());
		for (LoaderProblem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasIssues()).count());
		
	}

	

}
