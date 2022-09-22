package org.yarnandtail.andhow.valid;

/**
 * Validator implementations for Double ValueTypes.
 */
public abstract class DblValidator extends BaseValidator<Double> {

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
	public static class GreaterThan extends DblValidator {

		private final double ref;

		public GreaterThan(double ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Double value) {
			return value > ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than " + Double.toString(ref);
		}
	}


	/**
	 * Validate that an long is greater than or equal to a specified reference.
	 */
	public static class GreaterThanOrEqualTo extends DblValidator {

		private final double ref;

		public GreaterThanOrEqualTo(double ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Double value) {
			return value >= ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than or equal to " + Double.toString(ref);
		}
	}


	/**
	 * Validate that an long is less than a specified reference.
	 */
	public static class LessThan extends DblValidator {

		private final double ref;

		public LessThan(double ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Double value) {
			return value < ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than " + Double.toString(ref);
		}
	}


	/**
	 * Validate that a long is less than or equal to a specified reference.
	 */
	public static class LessThanOrEqualTo extends DblValidator {

		private final double ref;

		public LessThanOrEqualTo(double ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final Double value) {
			return value <= ref;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than or equal to " + Double.toString(ref);
		}
	}

}
