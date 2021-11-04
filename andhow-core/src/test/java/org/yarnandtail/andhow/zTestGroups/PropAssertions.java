package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropAssertions {

	ValidatedValues values;
	PropertyConfiguration config;

	public PropAssertions(ValidatedValues values, PropertyConfiguration config) {
		this.values = values;
		this.config = config;
	}

	public void assertAll(PropExpectations expect, int expectIndex, boolean useTrimmedValues) {
		expect.getExpectations().forEach(
				e -> assertOne(e, expectIndex, useTrimmedValues)
		);
	}

	public void assertOne(PropExpectations.PropExpectation expect, int expectIndex, boolean useTrimmedValues) {
		Property<?> p = expect.getProperty();
		Object val = values.getExplicitValue(p);

		Object expected;

		if (useTrimmedValues) {
			expected = expect.getTrimResults().get(expectIndex);
		} else {
			expected = expect.getNoTrimResults().get(expectIndex);
		}

		String msg = "Prop " + config.getCanonicalName(p) + " str [" + expect.getRawStrings().get(expectIndex) + "] "
				+ "should load to [" + expected + "] (" + (useTrimmedValues?"Trimmed":"Untrimmed") + ")";

		assertEquals(expected, val, msg);
	}
}
