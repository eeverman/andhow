package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface Validator<T> {
	
	boolean isValid(T value);
	
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
