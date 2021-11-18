package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.internal.*;

enum ResultType {
	UNSPECIFIED(true, false, false, false, false, null),
	EXPLICIT(false, true, false, false, false, null),
	SAME_AS_SOURCE(false, false, true, false, false, null),
	SAME_AS_TRIM_RESULT(false, false, false, true, false, null),
	MISSING_REQUIRED_PROB(false, false, false, false, true, RequirementProblem.NonNullPropertyProblem.class),
	INVALID_PROB(false, false, false, false, true, ValueProblem.InvalidValueProblem.class),
	STRING_PARSE_PROB(false, false, false, false, true, LoaderProblem.StringConversionLoaderProblem.class);

	private boolean unspecified;
	private boolean explicitlySet;  // The value is explicitly set separately from this list of enums
	private boolean sameAsSource;
	private boolean sameAsOtherResult;
	private boolean problem;
	private Class<? extends Problem> problemType;

	ResultType(boolean unspecified, boolean explicitlySet, boolean sameAsSource, boolean sameAsOtherResult,
			boolean problem, Class<? extends Problem> problemType) {
		this.unspecified = unspecified;
		this.explicitlySet = explicitlySet;
		this.sameAsSource = sameAsSource;
		this.sameAsOtherResult = sameAsOtherResult;
		this.problem = problem;
		this.problemType = problemType;
	}

	public boolean isSameAsSource() {
		return sameAsSource;
	}

	public boolean isSameAsOtherResult() {
		return sameAsOtherResult;
	}

	public boolean isProblem() {
		return problem;
	}

	public Class<? extends Problem> getProblemType() {
		return problemType;
	}

	public boolean isUnspecified() {
		return unspecified;
	}

	public boolean isExplicitlySet() {
		return explicitlySet;
	}
}
