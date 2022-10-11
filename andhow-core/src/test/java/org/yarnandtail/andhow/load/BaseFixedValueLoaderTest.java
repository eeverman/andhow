package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.yarnandtail.andhow.load.BaseForLoaderTests.SimpleParams.*;

public class BaseFixedValueLoaderTest extends BaseForLoaderTests {

	protected MyFixedLoader loader;

	@BeforeEach
	public void beforeEach() {
		loader = new MyFixedLoader();
	}


	@Test
	public void happyPathViaPropertyValues() {

		List<PropertyValue<?>> props = new ArrayList();
		props.add(new PropertyValue(STR_BOB, "test"));
		props.add(new PropertyValue(STR_NULL, "not_null"));
		props.add(new PropertyValue(STR_ENDS_WITH_XXX, "XXX"));

		//spaces on non-string values should be trimmed and not matter
		props.add(new PropertyValue(LNG_TIME, "60"));		//as string
		props.add(new PropertyValue(INT_NUMBER, 30));	//As exact value type (int)
		props.add(new PropertyValue(DBL_NUMBER, "123.456D"));	//as string
		props.add(new PropertyValue(FLAG_TRUE, " false "));
		props.add(new PropertyValue(FLAG_FALSE, " true "));
		props.add(new PropertyValue(FLAG_NULL, " true "));

		loadEnv.setFixedPropertyValues(props);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("XXX", result.getExplicitValue(STR_ENDS_WITH_XXX));
		assertEquals(60L, result.getExplicitValue(LNG_TIME));
		assertEquals(30, result.getExplicitValue(INT_NUMBER));
		assertEquals(Boolean.FALSE, result.getExplicitValue(FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_NULL));
	}

	@Test
	public void happyPathViaKeyObjectPairs() throws ParsingException {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		Map<String, Object> kops = new HashMap<>();
		kops.put(basePath + "STR_BOB", "test");
		kops.put(basePath + "STR_NULL", "not_null");
		kops.put(basePath + "STR_ENDS_WITH_XXX", "XXX");
		kops.put(basePath + "LNG_TIME", 60L);	//As exact value type (Long)
		kops.put(basePath + "INT_NUMBER", "30");	//Supply string for conversion
		kops.put(basePath + "DBL_NUMBER", 123.456D);	//As exact value type (Double)
		kops.put(basePath + "FLAG_TRUE", false);
		kops.put(basePath + "FLAG_FALSE", true);
		kops.put(basePath + "FLAG_NULL", Boolean.TRUE);


		loadEnv.setFixedNamedValues(kops);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);
		
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(STR_BOB));
		assertEquals("not_null", result.getExplicitValue(STR_NULL));
		assertEquals("XXX", result.getExplicitValue(STR_ENDS_WITH_XXX));
		assertEquals(60L, result.getExplicitValue(LNG_TIME));
		assertEquals(30, result.getExplicitValue(INT_NUMBER));
		assertEquals(Boolean.FALSE, result.getExplicitValue(FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_NULL));
	}


	@Test
	public void keyObjectPairsNonStringValueShouldBeTrimmed() throws ParsingException {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		Map<String, Object> kops = new HashMap<>();
		kops.put(basePath + "STR_BOB", " test ");	// preserve space
		kops.put(basePath + "STR_NULL", " not_null ");	// preserve space
		kops.put(basePath + "STR_ENDS_WITH_XXX", " XXX");	// preserve space
		kops.put(basePath + "LNG_TIME", "  60  ");	// remove space
		kops.put(basePath + "INT_NUMBER", "  30  ");	// remove space
		kops.put(basePath + "DBL_NUMBER", "  123.456D  ");	// remove space
		kops.put(basePath + "FLAG_TRUE", " false "); // remove space
		kops.put(basePath + "FLAG_FALSE", "  true  "); // remove space
		kops.put(basePath + "FLAG_NULL", "  "); // remove space


		loadEnv.setFixedNamedValues(kops);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals(" test ", result.getExplicitValue(STR_BOB));
		assertEquals(" not_null ", result.getExplicitValue(STR_NULL));
		assertEquals(" XXX", result.getExplicitValue(STR_ENDS_WITH_XXX));
		assertEquals(60L, result.getExplicitValue(LNG_TIME));
		assertEquals(30, result.getExplicitValue(INT_NUMBER));
		assertEquals(123.456D, result.getExplicitValue(DBL_NUMBER), .00001D);
		assertEquals(Boolean.FALSE, result.getExplicitValue(FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_FALSE));
		assertNull(result.getExplicitValue(FLAG_NULL));
	}

	@Test
	public void duplicatePropertyValuesCauseProblems() {
		List<PropertyValue<?>> props = new ArrayList();
		props.add(new PropertyValue(STR_BOB, "test"));
		props.add(new PropertyValue(STR_BOB, "ignored because its a duplicate property entry"));

		loadEnv.setFixedPropertyValues(props);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		//Make sure we have a duplicate problem reported
		assertEquals(1, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());	//No problems at an ind level
		Problem p = result.getProblems().get(0);
		assertTrue(p instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		assertEquals(STR_BOB, ((LoaderProblem.DuplicatePropertyLoaderProblem) p).getBadValueCoord().getProperty());

		//The initial assignment should have still worked
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
	}

	@Test
	public void duplicateBetweenPVsAndKopsCauseProblems() throws ParsingException {

		//Set based on PropertyValues
		List<PropertyValue<?>> props = new ArrayList();
		props.add(new PropertyValue(STR_BOB, "test"));

		//Set based on KeyObjectPair
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		Map<String, Object> kops = new HashMap<>();
		kops.put(basePath + "STR_BOB", "ignored because its a duplicate property entry");

		loadEnv.setFixedNamedValues(kops);
		loadEnv.setFixedPropertyValues(props);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		//Make sure we have a duplicate problem reported
		assertEquals(1, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());	//No problems at an ind level
		Problem p = result.getProblems().get(0);
		assertTrue(p instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		assertEquals(STR_BOB, ((LoaderProblem.DuplicatePropertyLoaderProblem) p).getBadValueCoord().getProperty());

		//The initial assignment should have still worked
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
	}

	@Test
	public void happyPathViaPropertyValuesAsArrayAndNoStateKeptTest() {

		List<PropertyValue<?>> props = new ArrayList<>();
		props.add(new PropertyValue(STR_BOB, "test"));
		props.add(new PropertyValue(STR_NULL, "not_null"));
		props.add(new PropertyValue(STR_ENDS_WITH_XXX, "XXX"));

		//spaces on non-string values should be trimmed and not matter
		props.add(new PropertyValue(LNG_TIME, "60"));		//as string
		props.add(new PropertyValue(INT_NUMBER, 30));	//As exact value type (int)
		props.add(new PropertyValue(DBL_NUMBER, "123.456D"));	//as string
		props.add(new PropertyValue(FLAG_TRUE, " false "));
		props.add(new PropertyValue(FLAG_FALSE, " true "));
		props.add(new PropertyValue(FLAG_NULL, " true "));

		loadEnv.setFixedPropertyValues(props);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
		assertEquals("XXX", result.getExplicitValue(STR_ENDS_WITH_XXX));
		assertEquals(60L, result.getExplicitValue(LNG_TIME));
		assertEquals(30, result.getExplicitValue(INT_NUMBER));
		assertEquals(Boolean.FALSE, result.getExplicitValue(FLAG_TRUE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_FALSE));
		assertEquals(Boolean.TRUE, result.getExplicitValue(FLAG_NULL));

		// set as null
		loadEnv.setFixedPropertyValues(null);
		result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));

		// release resources should not error
		loader.releaseResources();
	}

	@Test
	public void setPropertyValuesAsNullTest() {

		loadEnv.setFixedPropertyValues(null);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));

		// release resources when values set to null
		loader.releaseResources();
	}

	@Test
	public void setPropertyValuesAsEmptyList() {

		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<PropertyValue<?>> props = new ArrayList();
		props.add(new PropertyValue(STR_BOB, "test"));
		props.add(new PropertyValue(STR_NULL, "not_null"));
		props.add(new PropertyValue(STR_ENDS_WITH_XXX, "XXX"));

		loadEnv.setFixedPropertyValues(props);
		loadEnv.setFixedPropertyValues(Collections.emptyList());
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
	}

	@Test
	public void setNamedValuesAsNullTest() throws ParsingException {

		loadEnv.setFixedNamedValues(null);
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));

		// release resources when kops set to null
		loader.releaseResources();
	}

	@Test
	public void setNamedValuesAsEmptyList() throws ParsingException {

		loadEnv.setFixedNamedValues(Collections.emptyMap());
		LoaderValues result = loader.load(appDef, loadEnv.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

		assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
		assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
		assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
		assertNull(result.getValue(SimpleParams.STR_NULL));
	}

	// Temp class to make a non-abstract class
	public static class MyFixedLoader extends BaseFixedValueLoader {
		@Override
		public LoaderValues load(final PropertyConfigurationInternal runtimeDef,
				final LoaderEnvironment environment, final ValidatedValuesWithContext existingValues) {

			return load(runtimeDef, environment.getFixedNamedValues(), environment.getFixedPropertyValues());
		}
	}
}
