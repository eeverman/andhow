package org.yarnandtail.andhow.valid;

/**
 * Validator implementations for Long ValueTypes.
 */
public abstract class LngValidator extends BaseValidator<Long> {

	@Override
	public boolean isSpecificationValid() {
		return true;
	}

	@Override
	public String getInvalidSpecificationMessage() {
		return "THIS VALIDATION IS ALWAYS VALID";
	}

	/**
	 * Validate that a long is greater than a specified reference.
	 */
	public static class GreaterThan extends LngValidator {

		long ref;

		public GreaterThan(long ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Long value) {
			return value > ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than " + Long.toString(ref);
		}
	}


	/**
	 * Validate that a long is greater than or equal to a specified reference.
	 */
	public static class GreaterThanOrEqualTo extends LngValidator {

		private long ref;

		public GreaterThanOrEqualTo(long ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Long value) {
			return value >= ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than or equal to " + Long.toString(ref);
		}
	}


	/**
	 * Validate that a long is less than a specified reference.
	 */
	public static class LessThan extends LngValidator {

		private long ref;

		public LessThan(long ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Long value) {
			return value < ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than " + Long.toString(ref);
		}
	}


	/**
	 * Validate that a long is less than or equal to a specified reference.
	 */
	public static class LessThanOrEqualTo extends LngValidator {

		private long ref;

		public LessThanOrEqualTo(long ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Long value) {
			return value <= ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than or equal to " + Long.toString(ref);
		}
	}

}
