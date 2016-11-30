package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class RequiredPointException extends ValidationException {

	public RequiredPointException(ConfigPoint<?> point, String canonName) {
		super(point, canonName, "The ConfigPoint '" + canonName + "' is required, but is not configured and has no default");
	}	
	
}
