package yarnandtail.andhow;

/**
 * Records an invalid value and the validation rule that it violated.
 * 
 * Since validation may be done on actual assigned values as well as on overridden
 * and default values, the final message must be limited to just the validation
 * issue so that it can be set in a larger context.
 * 
 * @author eeverman
 */
public class InvalidValueIssue extends ValueIssue {

	protected Validator validator;
	
	
	public InvalidValueIssue(ConfigPoint<?> point, Object value, Validator validator) {
		super(point, value);
		this.validator = validator;
	}	

	@Override
	public String getMessageInPointContext() {
		return validator.getInvalidMessage(value);
	}

	public Validator getValidator() {
		return validator;
	}
	
}
