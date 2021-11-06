package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.BaseConfig;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.zTestGroups.PropExpectations.PropExpectation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PropAssertions {

	ValidatedValues values;
	PropertyConfiguration config;

	public PropAssertions(ValidatedValues values, PropertyConfiguration config) {
		this.values = values;
		this.config = config;
	}

	public void assertAll(PropExpectations expect, int expectIndex, boolean useTrimmedValues, boolean verbose) {
		expect.getExpectations().forEach(
				e -> assertOne(e, expectIndex, useTrimmedValues, verbose)
		);
	}

	public void assertErrors(BaseConfig config, PropExpectations expects, int expectIndex, boolean useTrimmedValues, boolean verbose) throws Exception {

		//Trick to allow access w/in the lambda
		final AppFatalException[] afeArray = new AppFatalException[1];

		String outText = tapSystemErr(() -> {
			afeArray[0] = assertThrows(AppFatalException.class,
					() -> buildCore(config));
		});

		AppFatalException afe = afeArray[0];

		List<PropExpectation> expectedProblems;

		if (useTrimmedValues) {
			expectedProblems = expects.getExpectations().stream()
					.filter(e -> e.getTrimResults().get(expectIndex) instanceof Problem).collect(Collectors.toList());
		} else {
			expectedProblems = expects.getExpectations().stream()
					.filter(e -> e.getNoTrimResults().get(expectIndex) instanceof Problem).collect(Collectors.toList());
		}

		assertEquals(expectedProblems.size(), afe.getProblems().size());

		for (PropExpectation exp : expectedProblems) {

			Problem expectedProblem = (Problem)
					((useTrimmedValues)?exp.getTrimResults().get(expectIndex):exp.getNoTrimResults().get(expectIndex));

			Property prop = exp.getProperty();
			List<? extends Problem> actualProblems = afe.getProblems().filter(expectedProblem.getClass());


		}



	}

	protected String findCanonicalName(Property prop, List<GroupProxy> proxies) {
		for (GroupProxy gp : proxies) {
			String simpleName = gp.getSimpleName(prop);

			if (simpleName != null) {

			}
		}
	}

	protected AndHowCore buildCore(BaseConfig aConfig) {
		AndHowCore core = new AndHowCore(
				aConfig.getNamingStrategy(),
				aConfig.buildLoaders(),
				aConfig.getRegisteredGroups());

		return core;
	}

	public void assertOne(PropExpectations.PropExpectation expect, int expectIndex, boolean useTrimmedValues, boolean verbose) {
		Property<?> p = expect.getProperty();
		Object val = values.getExplicitValue(p);

		Object expected;

		if (useTrimmedValues) {
			expected = expect.getTrimResults().get(expectIndex);
		} else {
			expected = expect.getNoTrimResults().get(expectIndex);
		}

		String msg = "Prop " + config.getCanonicalName(p) + " raw string [" + expect.getRawStrings().get(expectIndex) + "] "
				+ "should load to [" + expected + "] (" + (useTrimmedValues?"Trimmed":"Untrimmed") + ")";

		if (verbose) {
			System.out.println("Assert: " + msg);
		}

		assertEquals(expected, val, msg);
	}
}
