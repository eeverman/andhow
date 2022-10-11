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

		protected Object rawString = ResultType.UNSPECIFIED;
		protected Object trimResult = ResultType.UNSPECIFIED;

		public SimpleBuilder(PropExpectations parent, Property<?> property) {
			this.parent = parent;
			this.property = property;
		}

		public T raw(Object raw) {
			if (raw == null) {
				throw new IllegalArgumentException("Raw cannot be set to null");
			}

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
		 * @param result Expected object value.
		 * @return
		 */
		public T trimResult(Object result) {
			if (trimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResult);
			} else if (result == null) {
				throw new IllegalArgumentException("trimResult cannot be null");
			}

			trimResult = result;

			checkAutoBuild();
			return (T)this;
		}

		public T trimSameAsRaw() {
			if (trimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimSameAsRaw - " +
						"trim value is already set to: " + trimResult);
			}

			trimResult = ResultType.SAME_AS_SOURCE;

			checkAutoBuild();
			return (T)this;
		}

		public T trimIsNull() {
			if (trimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimSameAsRaw - " +
						"trim value is already set to: " + trimResult);
			}

			trimResult = ResultType.NULL;

			checkAutoBuild();
			return (T)this;
		}

		public T trimResultIsInvalidProb() {
			if (trimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResult);
			}

			trimResult = ResultType.INVALID_PROB;

			checkAutoBuild();
			return (T)this;
		}

		public T trimResultIsMissingProb() {
			if (trimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResult);
			}

			trimResult = ResultType.MISSING_REQUIRED_PROB;

			checkAutoBuild();
			return (T)this;
		}

		public T trimResultIsParseProb() {
			if (trimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set trimResult - " +
						"trim value is already set to: " + trimResult);
			}

			trimResult = ResultType.STRING_PARSE_PROB;

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
				return (rawString != ResultType.UNSPECIFIED && trimResult != ResultType.UNSPECIFIED);
			}
		}

		protected Object getFinalTrimResult() {
			Object result = trimResult;

			if (trimResult instanceof ResultType) {
				ResultType trimResultType = (ResultType)trimResult;
				switch (trimResultType) {
					case SAME_AS_SOURCE:
						result = rawString;
						break;
					case NULL:
						break;	// Just pass this value along
					default:
						if (trimResultType.isProblem()) {
							result = trimResultType.getProblemType();
						} else {
							throw new IllegalArgumentException("Unrecognized ResultType: " + trimResultType);
						}
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

		protected Object noTrimResult = ResultType.UNSPECIFIED;

		public StrBuilder(final PropExpectations parent, final Property<?> property) {
			super(parent, property);
		}

		/**
		 * The expected result of getExplicitValue() when set w/ the raw value w/ a loader that does not
		 * trim string values.
		 * <p>
		 * Note that for skipped values with a default, the explicit value will be null (not the default)
		 *
		 * @param result Expected object value.
		 * @return
		 */
		public StrBuilder noTrimResult(Object result) {
			if (noTrimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResult);
			} else if (noTrimResult == null) {
				throw new IllegalArgumentException("Cannot set noTrimResult to null");
			}

			noTrimResult = result;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimResultIsSameAsTrim() {
			if (noTrimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimSameAsTrim - " +
						"no-trim value is already set to: " + noTrimResult);
			}
			noTrimResult = ResultType.SAME_AS_TRIM_RESULT;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimSameAsRaw() {
			if (noTrimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimSameAsRaw - " +
						"no-trim value is already set to: " + noTrimResult);
			}

			noTrimResult = ResultType.SAME_AS_SOURCE;

			checkAutoBuild();
			return this;
		}


		public StrBuilder noTrimResultIsInvalidProb() {
			if (noTrimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResult);
			}

			noTrimResult = ResultType.INVALID_PROB;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimResultIsMissingProb() {
			if (noTrimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResult);
			}

			noTrimResult = ResultType.MISSING_REQUIRED_PROB;

			checkAutoBuild();
			return this;
		}

		public StrBuilder noTrimResultIsParseProb() {
			if (noTrimResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"no-trim value is already set to: " + noTrimResult);
			}

			noTrimResult = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return this;
		}

		protected boolean isReadyToBuild() {
			return (super.isReadyToBuild() && noTrimResult != ResultType.UNSPECIFIED);
		}

		protected Object getFinalNoTrimResult(Object trimResult) {
			Object result = noTrimResult;

			if (trimResult instanceof ResultType) {
				ResultType noTrimResultType = (ResultType) noTrimResult;

				switch (noTrimResultType) {
					case SAME_AS_SOURCE:
						result = rawString;
						break;
					case NULL:
						break;	// Just pass this value along
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

		protected Object flagResult = ResultType.UNSPECIFIED;

		public FlagBuilder(final PropExpectations parent, final Property<?> property) {
			super(parent, property);
		}

		public FlagBuilder flagResult(Boolean result) {
			if (flagResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set flagResult - " +
						"flag value is already set to: " + flagResult);
			} else if (result == null) {
				throw new IllegalArgumentException("Cannot set flagResult to null");
			}

			flagResult = result;

			checkAutoBuild();
			return this;
		}

		public FlagBuilder flagResultIsSameAsTrim() {
			if (flagResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set flagSameAsTrim - " +
						"flag value is already set to: " + flagResult);
			}
			flagResult = ResultType.SAME_AS_TRIM_RESULT;

			checkAutoBuild();
			return this;
		}

		public FlagBuilder flagResultIsParseProb() {
			if (flagResult != ResultType.UNSPECIFIED) {
				throw new IllegalStateException("Cannot set flagResult - " +
						"flag value is already set to: " + flagResult);
			}

			flagResult = ResultType.STRING_PARSE_PROB;

			checkAutoBuild();
			return this;
		}


		protected boolean isReadyToBuild() {
			return (super.isReadyToBuild() && flagResult != ResultType.UNSPECIFIED);
		}

		protected Object getFinalFlagResult(Object trimResult) {
			Object result = flagResult;

			if (flagResult instanceof ResultType) {
				ResultType flagResultType = (ResultType)flagResult;

				switch (flagResultType) {
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
