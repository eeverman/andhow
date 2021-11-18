package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.Property;

import java.util.*;

public class PropExpectation {
	private Property<?> property;
	private String rawString;
	private Object trimResult;
	private Object noTrimResult;
	private Object flagResult;
	private int index;  //Just for debugging in logged messages

	public PropExpectation(Property<?> property, String rawString, Object trimResult,
			Object noTrimResult, Object flagResult, int index) {

		this.index = index;

		if (rawString == null || trimResult == null || noTrimResult == null || flagResult == null) {
			throw new IllegalArgumentException("PropExpectation arguments cannot be null");
		}

		this.property = property;
		this.rawString = rawString;
		this.trimResult = trimResult;
		this.noTrimResult = noTrimResult;
		this.flagResult = flagResult;
	}

	public String getRawString() {
		return rawString;
	}

	public Object getTrimResult() {
		return trimResult;
	}

	public Object getNoTrimResult() {
		return noTrimResult;
	}

	public Object getFlagResult() {
		return flagResult;
	}

	public Property<?> getProperty() {
		return property;
	}

	//Should be identifiable b/c there aren't identifiable names for these
	@Override
	public String toString() {
		return "Type: " + property.getClass().getSimpleName() +
				" Property Index: " + index + ", 1st raw val: " + rawString;
	}
}
