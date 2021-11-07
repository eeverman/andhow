package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.BaseConfig;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.AndHowCore;
import org.yarnandtail.andhow.internal.PropertyProblem;
import org.yarnandtail.andhow.zTestGroups.PropExpectations.PropExpectation;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;

public class PropValueAssertions {

	ValidatedValues values;
	PropertyConfiguration config;
	int expectIndex;
	boolean useTrimmedValues;
	List<PropExpectations> expects;

	public PropValueAssertions(ValidatedValues values, PropertyConfiguration config,
			int expectIndex, boolean useTrimmedValues, PropExpectations... expects) {

		this.values = values;
		this.config = config;
		this.expectIndex = expectIndex;
		this.useTrimmedValues = useTrimmedValues;
		this.expects = Arrays.asList(expects);
	}

	public void assertAll(boolean verbose) {
		expects.stream().flatMap(es -> es.getExpectations().stream()).forEach(
				e -> assertOne(e, expectIndex, useTrimmedValues, verbose)
		);
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
