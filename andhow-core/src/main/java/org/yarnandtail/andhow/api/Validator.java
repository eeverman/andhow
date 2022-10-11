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
	 * A user message explaining why the value is not valid if it fails this validation rule.
	 * <p>
	 * The message should only refer to the value and rule criteria, and assume it is presented in
	 * a context that has already indicated an error and the Property.  No leading or trailing
	 * punctuation should not be included.
	 * <p>
	 * Example:  The value '999999999' is larger than the max value of 9999
	 * <p>
	 *
	 * @param value The value which failed validation.  Not null.
	 * @return A message explaining how the value violates the validation rule.
	 */
	String getInvalidMessage(T value);

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
