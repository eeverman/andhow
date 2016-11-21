package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public class NamingException extends Exception {
	private final ConfigPoint<?> newPoint;
	private final String newPointCanonName;
	private final String newPointConflictName;
	private final ConfigPoint<?> existingPoint;
	private final String existingPointCanonName;

	public NamingException(ConfigPoint<?> newPoint, String newPointCanonName, String newPointConflictName, 
			ConfigPoint<?> existingPoint, String existingPointCanonName) {
		super(
			"The point " + newPointCanonName + " is trying to use the name '" + newPointConflictName + "' " +
			"which is already in use by " + existingPointCanonName + ". " +
			"Try using a more specific naming strategy or a different alias for one of the points."
		);
		this.newPoint = newPoint;
		this.newPointCanonName = newPointCanonName;
		this.newPointConflictName = newPointConflictName;
		this.existingPoint = existingPoint;
		this.existingPointCanonName = existingPointCanonName;
	}

	public ConfigPoint<?> getNewPoint() {
		return newPoint;
	}

	public String getNewPointCanonName() {
		return newPointCanonName;
	}

	public String getNewPointConflictName() {
		return newPointConflictName;
	}

	public ConfigPoint<?> getExistingPoint() {
		return existingPoint;
	}

	public String getExistingPointCanonName() {
		return existingPointCanonName;
	}	
	
}
