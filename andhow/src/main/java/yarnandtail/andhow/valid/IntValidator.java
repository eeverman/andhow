package yarnandtail.andhow.valid;

import yarnandtail.andhow.Validator;

/**
 * A collection of String validation types
 * 
 * @author ericeverman
 */
public class IntValidator {
	
	/**
	 * Validate that an integer is greater than a specified int.
	 */
	public static class GreaterThan implements Validator<Integer> {

		int ref;

		public GreaterThan(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isSpecificationValid() {
			return true;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "";
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
	 * Validate that an integer is greater than or equal to a specified int.
	 */
	public static class GreaterThanOrEqualTo implements Validator<Integer> {

		int ref;

		public GreaterThanOrEqualTo(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isSpecificationValid() {
			return true;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The GreaterThanOrEqualTo reference value cannot be null";
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
	 * Validate that an integer is less than a specified int.
	 */
	public static class LessThan implements Validator<Integer> {

		int ref;

		public LessThan(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isSpecificationValid() {
			return true;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The LessThan reference value cannot be null";
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
	 * Validate that an integer is less than or equal to a specified int.
	 */
	public static class LessThanOrEqualTo implements Validator<Integer> {

		int ref;

		public LessThanOrEqualTo(int ref) {
			this.ref = ref;
		}

		@Override
		public boolean isSpecificationValid() {
			return true;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The LessThanOrEqualTo reference value cannot be null";
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
