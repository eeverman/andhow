package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.LoaderValues;
import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.internal.LoaderProblem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.yarnandtail.andhow.load.BaseForLoaderTests.SimpleParams.*;

public class FixedValueLoaderTest extends BaseForLoaderTests {

	@Test
	public void happyPathViaPropertyValues() {

		List<PropertyValue> props = new ArrayList();
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


		FixedValueLoader loader = new FixedValueLoader();
		loader.setPropertyValues(props);

		LoaderValues result = loader.load(appDef, appValuesBuilder);

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

		List<KeyObjectPair> kops = new ArrayList();
		kops.add(new KeyObjectPair(basePath + "STR_BOB", "test"));
		kops.add(new KeyObjectPair(basePath + "STR_NULL", "not_null"));
		kops.add(new KeyObjectPair(basePath + "STR_ENDS_WITH_XXX", "XXX"));
		kops.add(new KeyObjectPair(basePath + "LNG_TIME", 60L));	//As exact value type (Long)
		kops.add(new KeyObjectPair(basePath + "INT_NUMBER", "30"));	//Supply string for conversion
		kops.add(new KeyObjectPair(basePath + "DBL_NUMBER", 123.456D));	//As exact value type (Double)
		kops.add(new KeyObjectPair(basePath + "FLAG_TRUE", false));
		kops.add(new KeyObjectPair(basePath + "FLAG_FALSE", true));
		kops.add(new KeyObjectPair(basePath + "FLAG_NULL", Boolean.TRUE));


		FixedValueLoader loader = new FixedValueLoader();
		loader.setKeyObjectPairValues(kops);

		LoaderValues result = loader.load(appDef, appValuesBuilder);

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
	public void duplicatePropertyValuesCauseProblems() {
		List<PropertyValue> props = new ArrayList();
		props.add(new PropertyValue(STR_BOB, "test"));
		props.add(new PropertyValue(STR_BOB, "ignored because its a duplicate property entry"));

		FixedValueLoader loader = new FixedValueLoader();
		loader.setPropertyValues(props);

		LoaderValues result = loader.load(appDef, appValuesBuilder);

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
	public void duplicateKeyObjectPairsCauseProblems() throws ParsingException {
		String basePath = SimpleParams.class.getCanonicalName() + ".";

		List<KeyObjectPair> kops = new ArrayList();
		kops.add(new KeyObjectPair(basePath + "STR_BOB", "test"));
		kops.add(new KeyObjectPair(basePath + "STR_BOB", "ignored because its a duplicate property entry"));


		FixedValueLoader loader = new FixedValueLoader();
		loader.setKeyObjectPairValues(kops);

		LoaderValues result = loader.load(appDef, appValuesBuilder);

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
		List<PropertyValue> props = new ArrayList();
		props.add(new PropertyValue(STR_BOB, "test"));

		//Set based on KeyObjectPair
		String basePath = SimpleParams.class.getCanonicalName() + ".";
		List<KeyObjectPair> kops = new ArrayList();
		kops.add(new KeyObjectPair(basePath + "STR_BOB", "ignored because its a duplicate property entry"));

		FixedValueLoader loader = new FixedValueLoader();
		loader.setPropertyValues(props);
		loader.setKeyObjectPairValues(kops);

		LoaderValues result = loader.load(appDef, appValuesBuilder);

		//Make sure we have a duplicate problem reported
		assertEquals(1, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());	//No problems at an ind level
		Problem p = result.getProblems().get(0);
		assertTrue(p instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
		assertEquals(STR_BOB, ((LoaderProblem.DuplicatePropertyLoaderProblem) p).getBadValueCoord().getProperty());

		//The initial assignment should have still worked
		assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
	}
}
