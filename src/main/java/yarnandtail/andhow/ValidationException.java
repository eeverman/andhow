package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class ValidationException extends Exception {
	ConfigPoint<?> point;
	String canonName;
	
	public ValidationException(ConfigPoint<?> point, String canonName, String message) {
		super(message);
	}
	
	public ConfigPoint<?> getPoint() {
		return point;
	}

	public String getCanonName() {
		return canonName;
	}
	
}
