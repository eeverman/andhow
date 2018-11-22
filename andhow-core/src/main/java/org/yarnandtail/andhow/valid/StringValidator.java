package org.yarnandtail.andhow.valid;

import java.util.Arrays;

import org.yarnandtail.andhow.api.Validator;

/**
 * A collection of String validation types
 * 
 * @author ericeverman
 */
public class StringValidator {

	/**
	 * Validate that a string is one from the specified set.
	 */
	public static class Equals implements Validator<String> {

		String[] values;

		public MatchesFrom(String... values) {
			this.values = values;
		}

		@Override
		public boolean isSpecificationValid() {
			return values != null && values.length != 0;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The MatchesFrom expression cannot be null and should have atleast 1 value";
		}

		@Override
		public boolean isValid(String value) {
			if (value != null) {
				return Arrays.stream(values).anyMatch(value::equals);
			}
			return false;
		}

		@Override
		public String getTheValueMustDescription() {
			return "be equal to one of '" + Arrays.deepToString(values) + "'";
		}
	}

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
		public String getTheValueMustDescription() {
			return "start with '" + prefix + "'";
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
		public String getTheValueMustDescription() {
			return "end with '" + sufix + "'";
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
			} catch (Exception e) {
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
		public String getTheValueMustDescription() {
			return "match the regex expression '" + regex + "'";
		}

	}
}
