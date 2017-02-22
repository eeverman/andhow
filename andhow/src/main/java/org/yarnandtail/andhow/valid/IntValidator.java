package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.Validator;

/**
 * A collection of String validation types
 * 
 * @author ericeverman
 */
public abstract class IntValidator implements Validator<Integer> {
	
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

		int ref;

		public GreaterThan(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValid(Integer value) {
			if (value != null) {
				return (value > ref);
			}
			return false;
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

		int ref;

		public GreaterThanOrEqualTo(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValid(Integer value) {
			if (value != null) {
				return (value >= ref);
			}
			return false;
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

		int ref;

		public LessThan(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValid(Integer value) {
			if (value != null) {
				return (value < ref);
			}
			return false;
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

		int ref;

		public LessThanOrEqualTo(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValid(Integer value) {
			if (value != null) {
				return (value <= ref);
			}
			return false;
		}
		
		@Override
		public String getTheValueMustDescription() {
			return "be less than or equal to " + Integer.toString(ref);
		}
	}
	
	
}
