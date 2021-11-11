package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.internal.LoaderProblem.StringConversionLoaderProblem;
import org.yarnandtail.andhow.internal.RequirementProblem.NonNullPropertyProblem;
import org.yarnandtail.andhow.internal.ValueProblem.InvalidValueProblem;

import java.util.*;

public class PropExpectations {

	private Class<?> clazz;	// The containing class for the Properties
	private List<PropExpectation> expectations = new ArrayList();

	public PropExpectations(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Builder add(Property<?> property) {
		return new Builder(this, property);
	}

	public List<PropExpectation> getExpectations() {
		return Collections.unmodifiableList(expectations);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public static class Builder {
		private boolean built = false;	//true once build is called

		private PropExpectations parent;
		private Property<?> property;

		private String[] rawStringArray;
		private Object[] trimResultArray;
		private Object[] noTrimResultArray;

		private ResultType trimResultType = ResultType.UNSPECIFIED;
		private ResultType noTrimResultType = ResultType.UNSPECIFIED;

		public Builder(PropExpectations parent, Property<?> property) {
			this.parent = parent;
			this.property = property;
		}

		public Builder raw(String... strs) {

			if (strs != null) {
				rawStringArray = strs;
			} else {
				// special case:  A null indicates the intent to not set this value - skip it.
				// Same as RawValueType.SKIP
				rawStringArray = new String[] { null };
			}

			checkAutoBuild();
			return this;
		}

		/**
		 * The expected result of getExplicitValue() when set w/ the raw value w/ a loader that
		 * trims string values.
		 * <p>
		 * Note that for skipped values with a default, the explicit value will be null (not the default)
		 *
		 * @param results An array of expected object values, which can include nulls.  Each value
		 *                matches to the raw value of the same index.
		 * @return
		 */
		public Builder trimResult(Object... results) {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim values are already set to: " + trimResultType);
			}

			trimResultType = ResultType.EXPLICIT;

			if (results != null) {
				trimResultArray = results;
			} else {
				// special case:  A null indicates we really expect this value to be null - perhaps its skipped.
				trimResultArray = new String[] { null };
			}

			checkAutoBuild();
			return this;
		}

		public Builder trimResultIsSameAsOther() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim values are already set to: " + trimResultType);
			}

			trimResultType = ResultType.SAME_AS_OTHER_RESULT;

			checkAutoBuild();
			return this;
		}

		public Builder trimSameAsRaw() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimSameAsRaw - " +
						"no-trim values are already set to: " + trimResultType);
			}
			trimResultType = ResultType.SAME_AS_SOURCE;

			checkAutoBuild();
			return this;
		}

		public Builder trimResultIsInvalidProb() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim values are already set to: " + trimResultType);
			}

			trimResultType = ResultType.INVALID_PROB;

			checkAutoBuild();
			return this;
		}

		public Builder trimResultIsMissingProb() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim values are already set to: " + trimResultType);
			}

			trimResultType = ResultType.MISSING_REQUIRED_PROB;

			checkAutoBuild();
			return this;
		}

		public Builder trimResultIsParseProb() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim values are already set to: " + trimResultType);
			}

			trimResultType = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return this;
		}

		/**
		 * The expected result of getExplicitValue() when set w/ the raw value w/ a loader that does not
		 * trim string values.
		 * <p>
		 * Note that for skipped values with a default, the explicit value will be null (not the default)
		 *
		 * @param results An array of expected object values, which can include nulls.  Each value
		 *                matches to the raw value of the same index.
		 * @return
		 */
		public Builder noTrimResult(Object... results) {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim values are already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.EXPLICIT;

			if (results != null) {
				noTrimResultArray = results;
			} else {
				// special case:  A null indicates we really expect this value to be null - perhaps its skipped.
				noTrimResultArray = new String[] { null };
			}

			checkAutoBuild();
			return this;
		}

		public Builder noTrimResultIsSameAsOther() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimSameAsTrim - " +
						"no-trim values are already set to: " + noTrimResultType);
			}
			noTrimResultType = ResultType.SAME_AS_OTHER_RESULT;

			checkAutoBuild();
			return this;
		}

		public Builder noTrimSameAsRaw() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimSameAsRaw - " +
						"no-trim values are already set to: " + noTrimResultType);
			}
			noTrimResultType = ResultType.SAME_AS_SOURCE;

			checkAutoBuild();
			return this;
		}


		public Builder noTrimResultIsInvalidProb() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"trim values are already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.INVALID_PROB;

			checkAutoBuild();
			return this;
		}

		public Builder noTrimResultIsMissingProb() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"trim values are already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.MISSING_REQUIRED_PROB;

			checkAutoBuild();
			return this;
		}

		public Builder noTrimResultIsParseProb() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"trim values are already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return this;
		}

		private void checkAutoBuild() {
			if (built) {
				throw new IllegalStateException(
						"This builder was already auto-built, now attempting to modify state.");
			} else {
				if (rawStringArray != null && ! noTrimResultType.isUnspecified() &&
						! trimResultType.isUnspecified()) {

					build();
				}
			}
		}

		public void build() {

			if (! built) {

				if (trimResultType.isSameAsOtherResult() && noTrimResultType.isSameAsOtherResult()) {
					throw new IllegalStateException("Both results cannot be set to equal each other");
				}

				if (trimResultType.isUnspecified() || noTrimResultType.isUnspecified()) {
					throw new IllegalStateException("Both results cannot be set to equal each other");
				}

				switch (trimResultType) {
					case EXPLICIT:
						break;	// Nothing to do - already assigned
					case SAME_AS_SOURCE:
						trimResultArray = rawStringArray;
						break;
					case SAME_AS_OTHER_RESULT:
						trimResultArray = noTrimResultArray;
						break;
					default:
						if (trimResultType.isProblem()) {
							trimResultArray = new Object[rawStringArray.length];
							Arrays.fill(trimResultArray, trimResultType.getProblemType());
						} else {
							throw new IllegalArgumentException("Unrecognized ResultType: " + trimResultType);
						}
				}

				switch (noTrimResultType) {
					case EXPLICIT:
						break;	// Nothing to do - already assigned
					case SAME_AS_SOURCE:
						noTrimResultArray = rawStringArray;
						break;
					case SAME_AS_OTHER_RESULT:
						noTrimResultArray = trimResultArray;
						break;
					default:
						if (noTrimResultType.isProblem()) {
							noTrimResultArray = new Object[rawStringArray.length];
							Arrays.fill(noTrimResultArray, noTrimResultType.getProblemType());
						} else {
							throw new IllegalArgumentException("Unrecognized ResultType: " + trimResultType);
						}
				}

				// If any arrays are null, PropExpectation will throw Exception

				PropExpectation exp =
						new PropExpectation(property, rawStringArray, trimResultArray, noTrimResultArray, parent.expectations.size());

				parent.expectations.add(exp);

				built = true;

			} else {
				//Ignore unneeded call to build
			}
		}

	}


	public static class PropExpectation {
		private Property<?> property;
		private List<String> rawStrings;
		private List<Object> trimResult;
		private List<Object> noTrimResult;
		private int index;	//Just for debugging in logged messages

		public PropExpectation(Property<?> property, String[] rawStringArray, Object[] trimResultArray,
				Object[] noTrimResultArray, int index) {

			this.index = index;

			if (rawStringArray == null || trimResultArray == null || noTrimResultArray == null) {
				throw new IllegalArgumentException("PropExpectation arguments cannot be null");
			}

			if (rawStringArray.length == 0 || trimResultArray.length == 0
					|| noTrimResultArray.length == 0) {
				throw new IllegalArgumentException("PropExpectation arguments cannot be empty");
			}

			if (rawStringArray.length != trimResultArray.length &&
					rawStringArray.length != noTrimResultArray.length) {
				throw new IllegalArgumentException("PropExpectation arguments must be the same length");
			}

			this.property = property;


			rawStrings = new ArrayList<>(rawStringArray.length);
			rawStrings.addAll(Arrays.asList(rawStringArray));
			rawStrings = Collections.unmodifiableList(rawStrings);

			trimResult = new ArrayList<>(trimResultArray.length);
			trimResult.addAll(Arrays.asList(trimResultArray));
			trimResult = Collections.unmodifiableList(trimResult);

			noTrimResult = new ArrayList<>(noTrimResultArray.length);
			noTrimResult.addAll(Arrays.asList(noTrimResultArray));
			noTrimResult = Collections.unmodifiableList(noTrimResult);

		}

		public List<String> getRawStrings() {
			return rawStrings;
		}

		public List<Object> getTrimResults() {
			return trimResult;
		}

		public List<Object> getNoTrimResults() {
			return noTrimResult;
		}

		public Property<?> getProperty() {
			return property;
		}

		//Should be identifiable b/c there aren't identifiable names for these
		@Override
		public String toString() {
			return "Type: " + property.getClass().getSimpleName() +
					" Property Index: " + index + ", 1st raw val: " + rawStrings.get(0);
		}
	}

	private static enum ResultType {
		UNSPECIFIED(true, false, false, false, false, null),
		EXPLICIT(false, true, false, false, false, null),
		SAME_AS_SOURCE(false, false, true, false, false, null),
		SAME_AS_OTHER_RESULT(false, false, false, true, false, null),
		MISSING_REQUIRED_PROB(false, false, false, false, true, NonNullPropertyProblem.class),
		INVALID_PROB(false, false, false, false, true, InvalidValueProblem.class),
		STRING_PARSE_PROB(false, false, false, false, true, StringConversionLoaderProblem.class)
		;

		private boolean unspecified;
		private boolean explicitlySet;	// The value is explicitly set separately from this list of enums
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

}
