package org.yarnandtail.andhow.valid;

/**
 * Validator implementations for Integer ValueTypes.
 */
public abstract class IntValidator extends BaseValidator<Integer> {

	@Override
	public boolean isSpecificationValid() {
		return true;
	}

	@Override
	public String getInvalidSpecificationMessage() {
		return "THIS VALIDATION IS ALWAYS VALID";
	}

	/**
	 * Validate that an integer is greater than a reference value.
	 */
	public static class GreaterThan extends IntValidator {

		private final int ref;

		public GreaterThan(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Integer value) {
			return value > ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than " + Integer.toString(ref);
		}
	}


	/**
	 * Validate that an integer is greater than or equal to a reference value.
	 */
	public static class GreaterThanOrEqualTo extends IntValidator {

		private final int ref;

		public GreaterThanOrEqualTo(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Integer value) {
			return value >= ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than or equal to " + Integer.toString(ref);
		}
	}


	/**
	 * Validate that an integer is less than a reference value.
	 */
	public static class LessThan extends IntValidator {

		private final int ref;

		public LessThan(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(Integer value) {
			return value < ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than " + Integer.toString(ref);
		}
	}


	/**
	 * Validate that an integer is less than or equal to a reference value.
	 */
	public static class LessThanOrEqualTo extends IntValidator {

		private final int ref;

		public LessThanOrEqualTo(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Integer value) {
			return value <= ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than or equal to " + Integer.toString(ref);
		}
	}


}
