package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.load.std.StdMainStringArgsLoader;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * Note:  This directly tests a single loader, so it is not possible to
 * test for missing required values.  Loaders can't know if a value is missing -
 * that only can be figured out after all loaders are complete.
 *
 */
public class KeyValuePairLoaderTest extends BaseForLoaderTests {

	private KeyValuePairLoader loader;

	@BeforeEach
	public void initLoader() {
		loader = new KeyValuePairLoader();
	}

	@Test
	public void happyPathAsList() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "XXX");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");
		

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, appValuesBuilder);
		
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
	public void happyPathAsArray() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "something_XXX");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "false");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");


		loader.setKeyValuePairs(args.toArray(new String[5]));
		
		LoaderValues result = loader.load(appDef, appValuesBuilder);
		
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
	public void emptyValuesTrimAndAreNotFlags() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_TRUE" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_FALSE" + KeyValuePairLoader.KVP_DELIMITER + "");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "");

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_TRUE));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_FALSE));
		assertNull(result.getExplicitValue(SimpleParams.FLAG_NULL));
	}

	@Test
	public void setKeyValuePairsWithNullShouldReplaceValues() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");

		loader.setKeyValuePairs(args);
		loader.setKeyValuePairs((List<String>) null);

		LoaderValues result = loader.load(appDef, appValuesBuilder);


		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
	}

	@Test
	public void setKeyValuePairsWithEmptyShouldReplaceValues() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB" + KeyValuePairLoader.KVP_DELIMITER + "test");

		loader.setKeyValuePairs(args);
		loader.setKeyValuePairs(Collections.emptyList());

		LoaderValues result = loader.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
	}

	@Test
	public void invalidPropertyValuesAreNotCheckedByTheLoader() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_ENDS_WITH_XXX" + KeyValuePairLoader.KVP_DELIMITER + "something_YYY");

		loader.setKeyValuePairs(args);


		LoaderValues result = loader.load(appDef, appValuesBuilder);

		assertEquals("something_YYY", result.getValue(SimpleParams.STR_ENDS_WITH_XXX));
		assertEquals(0, result.getProblems().size());
	}
	
	@Test
	public void shouldCreateAProblemForDuplicateValues() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "1");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "2");
		args.add(basePath + "STR_NULL" + KeyValuePairLoader.KVP_DELIMITER + "3");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "true");
		args.add(basePath + "FLAG_NULL" + KeyValuePairLoader.KVP_DELIMITER + "false");

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, appValuesBuilder);
		
		assertEquals(3, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		
	}
	
	@Test
	public void shouldCreateAProblemForUnknownProperties() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "XXX" + KeyValuePairLoader.KVP_DELIMITER + "1");
		args.add(basePath + "YYY" + KeyValuePairLoader.KVP_DELIMITER + "2");

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, appValuesBuilder);

		assertTrue(loader.isUnknownPropertyAProblem());
		assertEquals(2, result.getProblems().size());
		for (Problem lp : result.getProblems()) {
			assertTrue(lp instanceof LoaderProblem.UnknownPropertyLoaderProblem);
		}
		
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void shouldNotCreateAProblemForUnknownPropertiesIfTurnedOff() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<String> args = new ArrayList();
		args.add(basePath + "XXX" + KeyValuePairLoader.KVP_DELIMITER + "1");
		args.add(basePath + "YYY" + KeyValuePairLoader.KVP_DELIMITER + "2");

		loader.setUnknownPropertyAProblem(false);
		loader.setKeyValuePairs(args);

		LoaderValues result = loader.load(appDef, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void getSpecificLoadDescriptionTest() {
		String expectedDescription = "string key value pairs";

		KeyValuePairLoader cll = new KeyValuePairLoader();

		assertEquals(expectedDescription, cll.getSpecificLoadDescription());
	}

}
