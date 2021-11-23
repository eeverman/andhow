package org.yarnandtail.andhow.valid;

import java.util.Arrays;

/**
 * Validator implementations for String ValueTypes.
 */
public class StringValidator {

	private StringValidator() { /* No instances */ }

	/**
	 * Validate that a string is one from the specified set.
	 */
	public static class OneOf extends BaseValidator<String> {

		String[] values;

		public OneOf(String... values) {
			this.values = values;
		}

		@Override
		public boolean isSpecificationValid() {
			return values != null &&
					values.length != 0 &&
					!Arrays.asList(values).contains(null);
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The list must contain at least one value and none of the values can be null";
		}

		@Override
		public boolean isValidWithoutNull(final String value) {
			return Arrays.stream(values).anyMatch(value::equals);
		}

		@Override
		public String getTheValueMustDescription() {
			return "be equal to one of '" + Arrays.deepToString(values) + "'";
		}
	}

	/**
	 * Validate that a string is one from the specified set ignoring case.
	 */
	public static class OneOfIgnoringCase extends BaseValidator<String> {

		String[] values;

		public OneOfIgnoringCase(String... values) {
			this.values = values;
		}

		@Override
		public boolean isSpecificationValid() {
			return values != null &&
					values.length != 0 &&
					!Arrays.asList(values).contains(null);
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The list must contain at least one value and none of the values can be null";
		}

		@Override
		public boolean isValidWithoutNull(final String value) {
			return Arrays.stream(values).anyMatch(value::equalsIgnoreCase);
		}

		@Override
		public String getTheValueMustDescription() {
			return "be equal to one of '" + Arrays.deepToString(values) + "' ignoring case";
		}
	}

	/**
	 * Validate that a string starts with a specific string.
	 */
	public static class StartsWith extends BaseValidator<String> {

		private String prefix;
		private boolean ignoreCase;

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
			return "The StartsWith expression cannot be null";
		}

		@Override
		public boolean isValidWithoutNull(final String value) {
			if (ignoreCase) {
				return value.toUpperCase().startsWith(prefix.toUpperCase());
			} else {
				return value.startsWith(prefix);
			}
		}

		@Override
		public String getTheValueMustDescription() {
			return "start with '" + prefix + "'";
		}
	}

	/**
	 * Validate that a string ends with a specific string.
	 */
	public static class EndsWith extends BaseValidator<String> {

		private String suffix;
		private boolean ignoreCase;

		public EndsWith(String sufix, boolean ignoreCase) {
			this.suffix = sufix;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public boolean isSpecificationValid() {
			return suffix != null;
		}

		@Override
		public String getInvalidSpecificationMessage() {
			return "The EndWith expression cannot be null";
		}

		@Override
		public boolean isValidWithoutNull(final String value) {
			if (ignoreCase) {
				return value.toUpperCase().endsWith(suffix.toUpperCase());
			} else {
				return value.endsWith(suffix);
			}
		}

		@Override
		public String getTheValueMustDescription() {
			return "end with '" + suffix + "'";
		}
	}

	/**
	 * Validate based on a regex string.
	 */
	public static class Regex extends BaseValidator<String> {

		private String regex;

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
		public boolean isValidWithoutNull(final String value) {
			return value.matches(regex);
		}

		@Override
		public String getTheValueMustDescription() {
			return "match the regex expression '" + regex + "'";
		}

	}
}
