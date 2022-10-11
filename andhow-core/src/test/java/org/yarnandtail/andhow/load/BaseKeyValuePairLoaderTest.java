package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;

/**
 * Note:  This directly tests a single loader, so it is not possible to
 * test for missing required values.  Loaders can't know if a value is missing -
 * that only can be figured out after all loaders are complete.
 *
 */
public class BaseKeyValuePairLoaderTest extends BaseForLoaderTests {

	private MyKVPLoader loader;

	@BeforeEach
	public void initLoader() {
		loader = new MyKVPLoader();
	}

	@Test
	public void happyPathAsList() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB=test");
		args.add(basePath + "STR_NULL=not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX=XXX");
		args.add(basePath + "FLAG_TRUE=false");
		args.add(basePath + "FLAG_FALSE=true");
		args.add(basePath + "FLAG_NULL=true");
		

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);
		
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
		args.add(basePath + "STR_BOB=test");
		args.add(basePath + "STR_NULL=not_null");
		args.add(basePath + "STR_ENDS_WITH_XXX=something_XXX");
		args.add(basePath + "FLAG_TRUE=false");
		args.add(basePath + "FLAG_FALSE=true");
		args.add(basePath + "FLAG_NULL=true");


		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);
		
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
		args.add(basePath + "STR_BOB=");
		args.add(basePath + "STR_NULL=");
		args.add(basePath + "FLAG_TRUE=");
		args.add(basePath + "FLAG_FALSE=");
		args.add(basePath + "FLAG_NULL=");

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);
		
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
		args.add(basePath + "STR_BOB=test");

		loader.setKeyValuePairs(args);
		loader.setKeyValuePairs((List<String>) null);

		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);


		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
	}

	@Test
	public void setKeyValuePairsWithEmptyShouldReplaceValues() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<String> args = new ArrayList();
		args.add(basePath + "STR_BOB=test");

		loader.setKeyValuePairs(args);
		loader.setKeyValuePairs(Collections.emptyList());

		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
	}

	@Test
	public void invalidPropertyValuesAreNotCheckedByTheLoader() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_ENDS_WITH_XXX=something_YYY");

		loader.setKeyValuePairs(args);


		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);

		assertEquals("something_YYY", result.getValue(SimpleParams.STR_ENDS_WITH_XXX));
		assertEquals(0, result.getProblems().size());
	}
	
	@Test
	public void shouldCreateAProblemForDuplicateValues() {
		
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		
		List<String> args = new ArrayList();
		args.add(basePath + "STR_NULL=1");
		args.add(basePath + "STR_NULL=2");
		args.add(basePath + "STR_NULL=3");
		args.add(basePath + "FLAG_NULL=true");
		args.add(basePath + "FLAG_NULL=false");

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);
		
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
		args.add(basePath + "XXX=1");
		args.add(basePath + "YYY=2");

		loader.setKeyValuePairs(args);
		
		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);

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
		args.add(basePath + "XXX=1");
		args.add(basePath + "YYY=2");

		loader.setUnknownPropertyAProblem(false);
		loader.setKeyValuePairs(args);

		LoaderValues result = loader.load(appDef, loadEnv, appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void getSpecificLoadDescriptionTest() {
		String expectedDescription = "string key value pairs";

		assertEquals(expectedDescription, loader.getSpecificLoadDescription());
	}

	/**
	 * A concrete implementation to verify behaviour of BaseKeyValuePairLoader
	 */
	protected class MyKVPLoader extends BaseKeyValuePairLoader {

		protected List<String> keyValuePairs;

		public void setKeyValuePairs(List<String> keyValuePairs) {
			this.keyValuePairs = keyValuePairs == null ? null : new ArrayList<>(keyValuePairs);
		}

		@Override
		public LoaderValues load(final PropertyConfigurationInternal runtimeDef,
				final LoaderEnvironment environment, final ValidatedValuesWithContext existingValues) {

			return load(runtimeDef, keyValuePairs, KVP_DELIMITER);
		}
	}

}
