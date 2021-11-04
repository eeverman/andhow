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

		public Builder(PropExpectations parent, Property<?> property) {
			this.parent = parent;
			this.property = property;
		}

		private void checkAutoBuild() {
			if (built) {
				throw new IllegalStateException(
						"This builder was already auto-built, now attempting to modify state.");
			} else {
				if (rawStringArray != null && trimResultArray != null && noTrimResultArray != null) {
					build();
				}
			}
		}

		public Builder rawStrings(String... strs) {
			rawStringArray = strs;
			return this;
		}

		public Builder trimResult(Object... results) {
			trimResultArray = results;
			return this;
		}

		public Builder noTrimResult(Object... results) {
			noTrimResultArray = results;
			return this;
		}

		public void build() {

			if (! built) {
				if (noTrimResultArray == null) {
					noTrimResultArray = trimResultArray;
				}
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
