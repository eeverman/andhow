package org.yarnandtail.andhow.api;

/**
 * A single validation rule and rule metadata for a Property value.
 * <p>
 *
 * @param <T> The data type the validator can validate.
 */
public interface Validator<T> {

	/**
	 * Determines if the non-null value is valid according to the rule this validator enforces.
	 * <p>
	 * Implementations should check for null and throw an IllegalArgumentException, since some
	 * types of validation operations may fail to detect a null and simply return false.
	 * {@link Loader}s (which call Validators) is a probable user extension point, so consistent
	 * handling and rejection of null values is important for custom Loaders to detect logic
	 * issues.
	 * <p>
	 *
	 * @param value A non-null value of type 'T'.
	 * @return boolean to indicate if the value is valid for this rule.
	 * @throws IllegalArgumentException If the value is null.
	 */
	boolean isValid(T value) throws IllegalArgumentException;

	/**
	 * If the value is not valid, this will create a user message explaining
	 * why the value is not valid.
	 * <p>
	 * This message should only refer to the value and the criteria and assume
	 * that it is included in a larger message that identifies the property
	 * and other context.
	 * <p>
	 * This method should be prepared to handle nulls.
	 * <p>
	 * Ending punctuation should not be included.
	 * <p>
	 * Example:  The value '999999999' is larger than the max value of 9999
	 * <p>
	 * The default implementation in the Validator Interface provides a reasonable
	 * default if the conventions are followed correction in the
	 * getTheValueMustDescription method.
	 *
	 * @param value
	 * @return
	 */
	default String getInvalidMessage(T value) {
		if (value != null) {
			return "The value '" + value.toString() + "' must " + getTheValueMustDescription();
		} else {
			return "The value [[null]] must " + getTheValueMustDescription();
		}
	}

	/**
	 * If the specification itself is invalid (such as a bad regex string), this
	 * will build a message describing the problem to the user.
	 * <p>
	 * This method may return a message regardless of if the validation is invalid
	 * or not - use isSpecificationValid() to determine if the Validator is
	 * configured correctly or not.
	 *
	 * @return A message describing an assumed problem with the validation specification.
	 */
	String getInvalidSpecificationMessage();

	/**
	 * Returns true if the validation criteria itself is valid.
	 * <p>
	 * For things like regex expressions, this would check to see if the regex
	 * expression is a valid expression.
	 *
	 * @return
	 */
	boolean isSpecificationValid();

	/**
	 * Get a description of the validation rule.
	 * <p>
	 * Assume that the text returned is prefixed with 'The value must...'
	 * <p>
	 * As an example for a regex rule with the pattern 'ABC.*', the return could be:
	 * "match the regex pattern 'ABC.*'"
	 *
	 * @return
	 */
	String getTheValueMustDescription();
}
