package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Note:  This directly tests a single loader so it is not possible to
 * test for missing required values.  Loaders can't know if a value is missing -
 * that only can be figured out after all loaders are complete.
 *
 */
public class KeyValuePairLoaderTest extends BaseForLoaderTests {

	@Test
	public void testCmdLineLoaderHappyPathAsList() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "XXX");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");
		
		
		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("XXX", result.getExplicitValue(SimpleParams.STR_ENDS_WITH_XXX));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	
	@Test
	public void testCmdLineLoaderHappyPathAsArray() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "something_XXX");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");
		
		
		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args.toArray(new String[5]));
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("something_XXX", result.getExplicitValue(SimpleParams.STR_ENDS_WITH_XXX));
		assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}
	

	@Test
	public void testCmdLineLoaderEmptyValues() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "");
		
		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void testCmdLineLoaderNullValues() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "something_XXX");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");

		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);

		cll.setKeyValuePairs((List<String>) null);
		LoaderValues result = cll.load(appDef, appValuesBuilder);


		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
	}

	@Test
	public void testCmdLineLoaderEmptyValueList() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "something_XXX");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");

		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);

		cll.setKeyValuePairs(Collections.emptyList());
		LoaderValues result = cll.load(appDef, appValuesBuilder);


		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
	}

	@Test
	public void testInvalidPropertyValuesAreNotCheckedByLoaders() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "something_YYY");
		
		
		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
	}
	
	@Test
	public void testCmdLineLoaderDuplicateValuesAndSpaces() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "1");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "2");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "3");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "false");
		
		
		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(3, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	@Test
	public void testCmdLineLoaderWithUnknownProperties() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "XXX" + KeyValuePairLoader.KVP_DELIMITER + "1");
		args.add(basePath + "YYY" + KeyValuePairLoader.KVP_DELIMITER + "2");
		
		
		KeyValuePairLoader cll = new KeyValuePairLoader();
		cll.setKeyValuePairs(args);
		
		LoaderValues result = cll.load(appDef, appValuesBuilder);
		
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void getSpecificLoadDescriptionTest() {
		String expectedDescription = "string key value pairs";

		KeyValuePairLoader cll = new KeyValuePairLoader();

		assertEquals(expectedDescription, cll.getSpecificLoadDescription());
	}

}
