package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.api.Validator;

/**
 * A collection of Double validation types
 * 
 * @author ericeverman
 */
public abstract class DblValidator implements Validator<Double> {
	
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

		double ref;

		public GreaterThan(double ref) {
			this.ref = ref;
		}

		@Override
		public Boolean isValid(Double value) {
			if (value != null) {
				return (value > ref);
			}
			return false;
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

		double ref;

		public GreaterThanOrEqualTo(double ref) {
			this.ref = ref;
		}

		@Override
		public Boolean isValid(Double value) {
			if (value != null) {
				return (value >= ref);
			}
			return false;
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

		double ref;

		public LessThan(double ref) {
			this.ref = ref;
		}

		@Override
		public Boolean isValid(Double value) {
			if (value != null) {
				return (value < ref);
			}
			return false;
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

		double ref;

		public LessThanOrEqualTo(double ref) {
			this.ref = ref;
		}
		
		@Override
		public Boolean isValid(Double value) {
			if (value != null) {
				return (value <= ref);
			}
			return false;
		}
		
		@Override
		public String getTheValueMustDescription() {
			return "be less than or equal to " + Double.toString(ref);
		}
	}
	
}
