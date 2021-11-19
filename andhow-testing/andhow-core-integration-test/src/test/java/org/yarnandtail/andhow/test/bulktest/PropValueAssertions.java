package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PropValueAssertions {

	ValidatedValues values;
	PropertyConfiguration config;
	boolean useTrimmedValues;
	boolean useFlagValuesForFlags;
	List<PropExpectations> expects;

	public PropValueAssertions(ValidatedValues values, PropertyConfiguration config,
			boolean useTrimmedValues, boolean useFlagValuesIfFlags, List<PropExpectations> expects) {

		this.values = values;
		this.config = config;
		this.useTrimmedValues = useTrimmedValues;
		this.useFlagValuesForFlags = useFlagValuesIfFlags;
		this.expects = expects;
	}

	public void assertAll(boolean verbose) {
		expects.stream().flatMap(es -> es.getExpectations().stream()).forEach(
				e -> assertOne(e, verbose)
		);
	}

	public void assertOne(PropExpectation expect, boolean verbose) {
		Property<?> p = expect.getProperty();
		Object actualValue = values.getExplicitValue(p);

		Object expectedValue;
		String valueType;	// just for debug

		if (p.getValueType() instanceof FlaggableType && this.useFlagValuesForFlags) {
			expectedValue = expect.getFlagResult();
			valueType = "Flag";
		} else if (useTrimmedValues) {
			expectedValue = expect.getTrimResult();
			valueType = "Trimmed";
		} else {
			expectedValue = expect.getNoTrimResult();
			valueType = "Untrimmed";
		}

		String msg = "Prop " + config.getCanonicalName(p) + " raw string [" + expect.getRawString() + "] "
				+ "should load to [" + expectedValue + "] (" + valueType + ")";

		if (verbose) {
			System.out.println("Assert: " + msg);
		}

		if (expectedValue instanceof ResultType) {
			ResultType type = (ResultType)expectedValue;
			switch (type) {
				case NULL:
					assertNull(actualValue, msg);
					break;
				default:
					throw new IllegalStateException("Unexpected ResultType: " + type);
			}
		} else {
			assertEquals(expectedValue, actualValue, msg);
		}

	}
}
