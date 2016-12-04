package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface Validator<T> {
	
	boolean isValid(T value);
	
	/**
	 * If the value is not valid, this will create a user message explaining
	 * why the value is not valid.
	 * 
	 * This message should only refer to the value and the criteria and assume
	 * that it is included in a larger message that identifies the ConfigPoint
	 * and other context.
	 * 
	 * This method should be prepared to handle nulls.
	 * 
	 * Ending punctuation should not be included.
	 * 
	 * Example:  The value '999999999' is larger than the max value of 9999
	 * 
	 * @param value
	 * @return 
	 */
	String getInvalidMessage(T value);
	
	/**
	 * Returns true if the validation criteria itself is valid.
	 * 
	 * For things like regex expressions, this would check to see if the regex
	 * expression is a valid expression.
	 * 
	 * @return 
	 */
	boolean isSpecificationValid();
}
