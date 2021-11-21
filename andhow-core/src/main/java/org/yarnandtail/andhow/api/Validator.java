package org.yarnandtail.andhow.api;

public interface Validator<T> {
	
	boolean isValid(T value);
	
	/**
	 * If the value is not valid, this will create a user message explaining
	 * why the value is not valid.
	 * 
	 * This message should only refer to the value and the criteria and assume
	 * that it is included in a larger message that identifies the property
	 * and other context.
	 * 
	 * This method should be prepared to handle nulls.
	 * 
	 * Ending punctuation should not be included.
	 * 
	 * Example:  The value '999999999' is larger than the max value of 9999
	 * 
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
	 * 
	 * This method may return a message regardless of if the validation is invalid
	 * or not - use isSpecificationValid() to determine if the Validator is
	 * configured correctly or not.
	 * @return A message describing an assumed problem with the validation specification.
	 */
	String getInvalidSpecificationMessage();
	
	/**
	 * Returns true if the validation criteria itself is valid.
	 * 
	 * For things like regex expressions, this would check to see if the regex
	 * expression is a valid expression.
	 * 
	 * @return 
	 */
	boolean isSpecificationValid();
	
	/**
	 * Get a description of the validation rule.
	 * 
	 * Assume that the text returned is prefixed with 'The value must...'
	 * 
	 * As an example for a regex rule with the pattern 'ABC.*', the return could be:  
	 * "match the regex pattern 'ABC.*'"
	 * @return 
	 */
	String getTheValueMustDescription();
}
