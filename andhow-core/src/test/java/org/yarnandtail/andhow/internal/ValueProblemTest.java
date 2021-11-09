package org.yarnandtail.andhow.internal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.load.std.StdMainStringArgsLoader;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.valid.StringValidator;

import static org.junit.jupiter.api.Assertions.*;

class ValueProblemTest {

	final static StrProp PROP = StrProp.builder().build();
	final static StdMainStringArgsLoader LOADER = new StdMainStringArgsLoader();
	final static String VALUE = "XYZ";
	final static Validator<String> VALIDATOR = new StringValidator.OneOf("aa", "bb");

	@Test
	void getProblemContextHappyPath() {

		ValueProblem.InvalidValueProblem ivp = new ValueProblem.InvalidValueProblem(
				LOADER, this.getClass(), PROP, VALUE, VALIDATOR
		);

		assertEquals("Property " + this.getClass().getCanonicalName() + ".PROP loaded from " +
				LOADER.getSpecificLoadDescription(), ivp.getProblemContext());
	}

	@Test
	void getProblemContextWithNullLoaderShouldNotFail() {

		ValueProblem.InvalidValueProblem ivp = new ValueProblem.InvalidValueProblem(
				null, this.getClass(), PROP, VALUE, VALIDATOR
		);

		assertEquals("Property " + this.getClass().getCanonicalName() + ".PROP loaded from " +
				Problem.UNKNOWN, ivp.getProblemContext());

	}

	@Test
	void getProblemContextWithLoaderWithNullDescriptionShouldNotFail() {

		Loader mockLoader = Mockito.spy(LOADER);
		Mockito.when(mockLoader.getSpecificLoadDescription()).thenReturn(null);

		ValueProblem.InvalidValueProblem ivp = new ValueProblem.InvalidValueProblem(
				mockLoader, this.getClass(), PROP, VALUE, VALIDATOR
		);

		assertEquals("Property " + this.getClass().getCanonicalName() + ".PROP loaded from " +
				Problem.UNKNOWN, ivp.getProblemContext());

	}
}