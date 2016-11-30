package yarnandtail.andhow;

/**
 * An exception is really just a way to bundle all the aspects of a validation
 * issue into one place - it is not likely ever thrown.
 * 
 * The actual message assembled here is not used.  Since validation may be done
 * on actual assigned values as well as on overridden values, the generated
 * message will depend on that context during reporting.
 * 
 * @author eeverman
 */
public class InvalidValueException extends ValidationException {
	String value;
	Validator validator;
	
	
	public InvalidValueException(ConfigPoint<?> point, String canonName, String value, Validator validator) {
		super(point, canonName, "The ConfigPoint '" + canonName + "' is required, but is not configured and has no default");
		this.value = value;
		this.validator = validator;
	}	
	
}
