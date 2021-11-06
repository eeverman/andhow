package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.Property;

import java.util.*;

public class PropExpectations {

	private List<PropExpectation> expectations = new ArrayList();

	public PropExpectations() {
	}

	public Builder add(Property<?> property) {
		return new Builder(this, property);
	}

	public List<PropExpectation> getExpectations() {
		return Collections.unmodifiableList(expectations);
	}

	public static class Builder {
		private boolean built = false;	//true once build is called

		private PropExpectations parent;
		private Property<?> property;

		private String[] rawStringArray;
		private Object[] trimResultArray;
		private Object[] noTrimResultArray;

		private boolean _noTrimSameAsTrim = false;
		private boolean _noTrimSameAsRaw = false;

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

			if (results != null) {
				trimResultArray = results;
			} else {
				// special case:  A null indicates we really expect this value to be null - perhaps its skipped.
				trimResultArray = new String[] { null };
			}

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
			if (_noTrimSameAsTrim || _noTrimSameAsRaw) {
				throw new IllegalStateException("Cannot set noTrimResult - " +
						"noTrimSameAsTrim or noTrimSameAsRawalready set");
			}

			if (results != null) {
				noTrimResultArray = results;
			} else {
				// special case:  A null indicates we really expect this value to be null - perhaps its skipped.
				noTrimResultArray = new String[] { null };
			}



			noTrimResultArray = results;
			checkAutoBuild();
			return this;
		}

		public Builder noTrimSameAsTrim() {
			if (_noTrimSameAsRaw) {
				throw new IllegalStateException("Cannot set noTrimSameAsTrim - " +
						"noTrimSameAsRaw already set");
			} else if (noTrimResultArray != null) {
				throw new IllegalStateException("Cannot set noTrimSameAsTrim - " +
						"noTrimResultArray already set");
			}
			_noTrimSameAsTrim = true;
			checkAutoBuild();
			return this;
		}

		public Builder noTrimSameAsRaw() {
			if (_noTrimSameAsTrim) {
				throw new IllegalStateException("Cannot set noTrimSameAsRaw - " +
						"noTrimSameAsTrim already set");
			} else if (noTrimResultArray != null) {
				throw new IllegalStateException("Cannot set noTrimSameAsRaw - " +
						"noTrimResultArray already set");
			}

			_noTrimSameAsRaw = true;
			checkAutoBuild();
			return this;
		}

		private void checkAutoBuild() {
			if (built) {
				throw new IllegalStateException(
						"This builder was already auto-built, now attempting to modify state.");
			} else {
				if (rawStringArray != null && trimResultArray != null &&
						(noTrimResultArray != null || _noTrimSameAsTrim || _noTrimSameAsRaw)) {
					build();
				}
			}
		}

		public void build() {

			if (! built) {
				if (noTrimResultArray == null) {
					if (_noTrimSameAsRaw) {
						noTrimResultArray = rawStringArray;
					} else if (_noTrimSameAsTrim) {
						noTrimResultArray = trimResultArray;
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
					" index: " + index + " 1st raw: " + rawStrings.get(0);
		}
	}

}
