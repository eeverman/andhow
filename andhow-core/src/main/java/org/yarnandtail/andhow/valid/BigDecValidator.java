package org.yarnandtail.andhow.valid;

import java.math.BigDecimal;

/**
 * Validator implementations for BigDecimal ValueTypes.
 */
public abstract class BigDecValidator extends BaseValidator<BigDecimal> {

	final BigDecimal ref;

	/**
	 * Base constructor of BigDecValidator constraints
	 *
	 * @param ref to be compared to property value
	 */
	BigDecValidator(BigDecimal ref) {
		this.ref = ref;
	}

	@Override
	public boolean isSpecificationValid() {
		return ref != null;
	}

	@Override
	public String getInvalidSpecificationMessage() {
		return "The constraint may not be null";
	}

	/**
	 * Validate that a BigDecimal is greater than a specified reference.
	 */
	public static class GreaterThan extends BigDecValidator {
		/**
		 * Construct a GreaterThan property constraint
		 *
		 * @param ref to be compared to property value
		 */
		public GreaterThan(BigDecimal ref) {
			super(ref);
		}

		@Override
		public boolean isValidWithoutNull(final BigDecimal value) {
			return value.compareTo(ref) > 0;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than " + ref;
		}
	}

	/**
	 * Validate that a BigDecimal is greater than or equal to a specified reference.
	 */
	public static class GreaterThanOrEqualTo extends BigDecValidator {
		/**
		 * Construct a GreaterThanOrEqualTo property constraint
		 *
		 * @param ref to be compared to property value
		 */
		public GreaterThanOrEqualTo(BigDecimal ref) {
			super(ref);
		}

		@Override
		public boolean isValidWithoutNull(final BigDecimal value) {
			return value.compareTo(ref) >= 0;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be greater than or equal to " + ref;
		}
	}

	/**
	 * Validate that a BigDecimal is less than a specified reference.
	 */
	public static class LessThan extends BigDecValidator {
		/**
		 * Construct a LessThan property constraint
		 *
		 * @param ref to be compared to property value
		 */
		public LessThan(BigDecimal ref) {
			super(ref);
		}

		@Override
		public boolean isValidWithoutNull(final BigDecimal value) {
			return value.compareTo(ref) < 0;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than " + ref;
		}
	}

	/**
	 * Validate that a BigDecimal is less than or equal to a specified reference.
	 */
	public static class LessThanOrEqualTo extends BigDecValidator {
		/**
		 * Construct a LessThanOrEqualTo property constraint
		 *
		 * @param ref to be compared to property value
		 */
		public LessThanOrEqualTo(BigDecimal ref) {
			super(ref);
		}

		@Override
		public boolean isValidWithoutNull(final BigDecimal value) {
			return value.compareTo(ref) <= 0;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be less than or equal to " + ref;
		}
	}
}
