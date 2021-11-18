package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropValueAssertions {

	ValidatedValues values;
	PropertyConfiguration config;
	int expectIndex;
	boolean useTrimmedValues;
	List<PropExpectations> expects;

	public PropValueAssertions(ValidatedValues values, PropertyConfiguration config,
			int expectIndex, boolean useTrimmedValues, List<PropExpectations> expects) {

		this.values = values;
		this.config = config;
		this.expectIndex = expectIndex;
		this.useTrimmedValues = useTrimmedValues;
		this.expects = expects;
	}

	public void assertAll(boolean verbose) {
		expects.stream().flatMap(es -> es.getExpectations().stream()).forEach(
				e -> assertOne(e, expectIndex, useTrimmedValues, verbose)
		);
	}

	public void assertOne(PropExpectation expect, int expectIndex, boolean useTrimmedValues, boolean verbose) {
		Property<?> p = expect.getProperty();
		Object val = values.getExplicitValue(p);

		Object expected;

		if (useTrimmedValues) {
			expected = expect.getTrimResults().get(expectIndex);
		} else {
			expected = expect.getNoTrimResults().get(expectIndex);
		}

		String msg = "Prop " + config.getCanonicalName(p) + " raw string [" + expect.getRawStrings().get(expectIndex) + "] "
				+ "should load to [" + expected + "] (" + (useTrimmedValues ? "Trimmed" : "Untrimmed") + ")";

		if (verbose) {
			System.out.println("Assert: " + msg);
		}

		assertEquals(expected, val, msg);
	}
}
