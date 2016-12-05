package yarnandtail.andhow.valid;

import java.util.regex.PatternSyntaxException;
import yarnandtail.andhow.Validator;

/**
 * A collection of String validation types
 * 
 * @author ericeverman
 */
public class StringValidator {
	
	/**
	 * Validate that a string starts with a specific string.
	 */
	public static class StartsWith implements Validator<String> {

		String prefix;
		boolean ignoreCase;

		public StartsWith(String prefix, boolean ignoreCase) {
			this.prefix = prefix;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public boolean isSpecificationValid() {
			return prefix != null;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The StartWith expression cannot be null";
		}

		@Override
		public boolean isValid(String value) {
			if (value != null) {
				if (ignoreCase) {
					return value.toUpperCase().startsWith(prefix.toUpperCase());
				} else {
					return value.startsWith(prefix);
				}
			}
			return false;
		}

		@Override
		public String getInvalidMessage(String value) {
			return "The value '" + value + "' does not start with '" + prefix + "'";
		}
		
		@Override
		public String getTheValueMustDescription() {
			return "The value must start with '" + prefix + "'";
		}
	}
	

	/**
	 * Validate that a string ends with a specific string.
	 */
	public static class EndsWith implements Validator<String> {

		String sufix;
		boolean ignoreCase;

		public EndsWith(String sufix, boolean ignoreCase) {
			this.sufix = sufix;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public boolean isSpecificationValid() {
			return sufix != null;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The EndWith expression cannot be null";
		}

		@Override
		public boolean isValid(String value) {
			if (value != null) {
				if (ignoreCase) {
					return value.toUpperCase().endsWith(sufix.toUpperCase());
				} else {
					return value.endsWith(sufix);
				}
			}
			return false;
		}

		@Override
		public String getInvalidMessage(String value) {
			return "The value '" + value + "' does not end with '" + sufix + "'";
		}
		
		@Override
		public String getTheValueMustDescription() {
			return "The value must end with '" + sufix + "'";
		}
	}
	
	/**
	 * Validate based on a regex string.
	 */
	public static class Regex implements Validator<String> {

		String regex;

		public Regex(String regex) {
			this.regex = regex;
		}

		@Override
		public boolean isSpecificationValid() {

			try {
				"".matches(regex);
				return true;
			} catch (PatternSyntaxException e) {
				return false;
			}

		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The expression '" + regex + "' is not a valid regex expression";
		}

		@Override
		public boolean isValid(String value) {
			if (value != null && value.matches(regex)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String getInvalidMessage(String value) {
			return "The value '" + value + "' does not match the regex expression '" + regex + "'";
		}
		
		@Override
		public String getTheValueMustDescription() {
			return "The value must match the regex '" + regex + "'";
		}

	}	
}
