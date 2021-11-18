package org.yarnandtail.andhow.test.bulktest;

import org.yarnandtail.andhow.api.Property;

import java.util.*;

public class PropExpectations {

	private Class<?> clazz;	// The containing class for the Properties
	private List<PropExpectation> expectations = new ArrayList();

	public PropExpectations(Class<?> clazz) {
		this.clazz = clazz;
	}

	public SimpleBuilder add(Property<?> property) {
		return new SimpleBuilder(this, property);
	}

	public StrBuilder addStr(Property<?> property) {
		return new StrBuilder(this, property);
	}

	public FlagBuilder addFlag(Property<?> property) {
		return new FlagBuilder(this, property);
	}

	public List<PropExpectation> getExpectations() {
		return Collections.unmodifiableList(expectations);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public static class SimpleBuilder<T extends SimpleBuilder<T>> {
		protected boolean built = false;	//true once build is called

		protected PropExpectations parent;
		protected Property<?> property;

		protected String rawString;
		protected Object trimResult;

		protected ResultType trimResultType = ResultType.UNSPECIFIED;

		public SimpleBuilder(PropExpectations parent, Property<?> property) {
			this.parent = parent;
			this.property = property;
		}

		public T raw(String raw) {
			rawString = raw;

			checkAutoBuild();

			return (T)this;
		}

		/**
		 * The expected result of getExplicitValue() when set w/ the raw value w/ a loader that
		 * trims string values.
		 * <p>
		 * Note that for skipped values with a default, the explicit value will be null (not the default)
		 *
		 * @param result Expected object value, which could be null.
		 * @return
		 */
		public T trimResult(Object result) {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResultType);
			}

			trimResultType = ResultType.EXPLICIT;
			trimResult = result;

			checkAutoBuild();
			return (T)this;
		}

		public T trimSameAsRaw() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimSameAsRaw - " +
						"trim value is already set to: " + trimResultType);
			}
			trimResultType = ResultType.SAME_AS_SOURCE;

			checkAutoBuild();
			return (T)this;
		}

		public T trimResultIsInvalidProb() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResultType);
			}

			trimResultType = ResultType.INVALID_PROB;

			checkAutoBuild();
			return (T)this;
		}

		public T trimResultIsMissingProb() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResultType);
			}

			trimResultType = ResultType.MISSING_REQUIRED_PROB;

			checkAutoBuild();
			return (T)this;
		}

		public T trimResultIsParseProb() {
			if (trimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResultType);
			}

			trimResultType = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return (T)this;
		}

		protected void checkAutoBuild() {
			if (isReadyToBuild()) {
				build();
			}
		}

		protected boolean isReadyToBuild() {
			if (built) {
				throw new IllegalStateException(
						"This builder was already auto-built, now attempting to modify state.");
			} else {
				return (rawString != null && ! trimResultType.isUnspecified());
			}
		}

		protected Object getFinalTrimResult() {
			Object result = trimResult;

			switch (trimResultType) {
				case EXPLICIT:
					break;	// Nothing to do - already assigned
				case SAME_AS_SOURCE:
					result = rawString;
					break;
				default:
					if (trimResultType.isProblem()) {
						result = trimResultType.getProblemType();
					} else {
						throw new IllegalArgumentException("Unrecognized ResultType: " + trimResultType);
					}
			}

			return result;
		}

		public void build() {

			if (! built) {

				Object myResult = getFinalTrimResult();

				PropExpectation exp =
						new PropExpectation(property, rawString, myResult, ResultType.UNSPECIFIED, ResultType.UNSPECIFIED, parent.expectations.size());

				parent.expectations.add(exp);

				built = true;

			} else {
				//Ignore unneeded call to build
			}
		}

	}

	public static class StrBuilder extends SimpleBuilder<StrBuilder> {

		protected Object noTrimResult;
		protected ResultType noTrimResultType = ResultType.UNSPECIFIED;


		public StrBuilder(final PropExpectations parent, final Property<?> property) {
			super(parent, property);
		}


		/**
		 * The expected result of getExplicitValue() when set w/ the raw value w/ a loader that does not
		 * trim string values.
		 * <p>
		 * Note that for skipped values with a default, the explicit value will be null (not the default)
		 *
		 * @param result Expected object value, which could be null.
		 * @return
		 */
		public StrBuilder noTrimResult(Object result) {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.EXPLICIT;
			noTrimResult = result;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimResultIsSameAsTrim() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimSameAsTrim - " +
						"no-trim value is already set to: " + noTrimResultType);
			}
			noTrimResultType = ResultType.SAME_AS_TRIM_RESULT;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimSameAsRaw() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimSameAsRaw - " +
						"no-trim value is already set to: " + noTrimResultType);
			}
			noTrimResultType = ResultType.SAME_AS_SOURCE;

			checkAutoBuild();
			return this;
		}


		public StrBuilder noTrimResultIsInvalidProb() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.INVALID_PROB;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimResultIsMissingProb() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.MISSING_REQUIRED_PROB;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimResultIsParseProb() {
			if (noTrimResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResultType);
			}

			noTrimResultType = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return this;
		}

		protected boolean isReadyToBuild() {
			return (super.isReadyToBuild() && ! noTrimResultType.isUnspecified());
		}

		protected Object getFinalNoTrimResult(Object trimResult) {
			Object result = noTrimResult;

			switch (noTrimResultType) {
				case EXPLICIT:
					break;	// Nothing to do - already assigned
				case SAME_AS_SOURCE:
					result = rawString;
					break;
				case SAME_AS_TRIM_RESULT:
					result = trimResult;
					break;
				default:
					if (noTrimResultType.isProblem()) {
						result = noTrimResultType.getProblemType();
					} else {
						throw new IllegalArgumentException("Unrecognized ResultType: " + noTrimResultType);
					}
			}

			return result;
		}


		public void build() {

			if (! built) {

				Object myTrimResult = getFinalTrimResult();
				Object myNoTrimResult = getFinalNoTrimResult(myTrimResult);

				PropExpectation exp =
						new PropExpectation(property, rawString, myTrimResult, myNoTrimResult, ResultType.UNSPECIFIED, parent.expectations.size());

				parent.expectations.add(exp);

				built = true;

			} else {
				//Ignore unneeded call to build
			}
		}

	}

	public static class FlagBuilder extends SimpleBuilder<FlagBuilder> {

		protected Object flagResult;
		protected ResultType flagResultType = ResultType.UNSPECIFIED;

		public FlagBuilder(final PropExpectations parent, final Property<?> property) {
			super(parent, property);
		}

		public FlagBuilder flagResult(Boolean result) {
			if (flagResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set flagResult - " +
						"flag value is already set to: " + flagResultType);
			}

			flagResultType = ResultType.EXPLICIT;
			flagResult = result;

			checkAutoBuild();
			return this;
		}

		public FlagBuilder flagResultIsSameAsTrim() {
			if (flagResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set flagSameAsTrim - " +
						"flag value is already set to: " + flagResultType);
			}
			flagResultType = ResultType.SAME_AS_TRIM_RESULT;

			checkAutoBuild();
			return this;
		}

		public FlagBuilder flagResultIsParseProb() {
			if (flagResultType != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set flagResult - " +
						"flag value is already set to: " + flagResultType);
			}

			flagResultType = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return this;
		}


		protected boolean isReadyToBuild() {
			return (super.isReadyToBuild() && ! flagResultType.isUnspecified());
		}

		protected Object getFinalFlagResult(Object trimResult) {
			Object result = flagResult;

			switch (flagResultType) {
				case EXPLICIT:
					break;	// Nothing to do - already assigned
				case SAME_AS_TRIM_RESULT:
					result = trimResult;
					break;
				default:
					if (flagResultType.isProblem()) {
						result = flagResultType.getProblemType();
					} else {
						throw new IllegalArgumentException("Unrecognized ResultType: " + flagResultType);
					}
			}

			return result;
		}


		public void build() {

			if (! built) {

				Object myTrimResult = getFinalTrimResult();
				Object myFlagResult = getFinalFlagResult(myTrimResult);

				PropExpectation exp =
						new PropExpectation(property, rawString, myTrimResult, ResultType.UNSPECIFIED, myFlagResult, parent.expectations.size());

				parent.expectations.add(exp);

				built = true;

			} else {
				//Ignore unneeded call to build
			}
		}
	}


}
