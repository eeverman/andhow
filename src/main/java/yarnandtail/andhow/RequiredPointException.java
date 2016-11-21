package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class RequiredPointException extends ValidationException {
	ConfigPoint<?> point;
	String canonName;

	public RequiredPointException(ConfigPoint<?> point, String canonName) {
		super("The ConfigPoint '" + canonName + "' is required, but is not configured and has no default");
		this.point = point;
		this.canonName = canonName;
	}

	public ConfigPoint<?> getPoint() {
		return point;
	}

	public String getCanonName() {
		return canonName;
	}
	
	
}
