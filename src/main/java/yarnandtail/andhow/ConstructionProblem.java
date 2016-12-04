package yarnandtail.andhow;

/**
 * A problem building up the basic AppConfiguration, prior to attempting to load
 * any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class ConstructionProblem {
	
	public abstract String getMessage();
	
	public static class NonUniqueNames extends ConstructionProblem {
		ConfigPoint<?> firstPoint;
		ConfigPointGroup firstGroup;
		String firstCanonName;
		ConfigPoint<?> secondPoint;
		ConfigPointGroup secondGroup;
		String secondCanonName;
		String conflictName;

		public NonUniqueNames(ConfigPoint<?> firstPoint, ConfigPointGroup firstGroup, String firstCanonName, ConfigPoint<?> secondPoint, ConfigPointGroup secondGroup, String secondCanonName, String conflictName) {
			this.firstPoint = firstPoint;
			this.firstGroup = firstGroup;
			this.firstCanonName = firstCanonName;
			this.secondPoint = secondPoint;
			this.secondGroup = secondGroup;
			this.secondCanonName = secondCanonName;
			this.conflictName = conflictName;
		}
		
		public ConfigPoint<?> getFirstPoint() {
			return firstPoint;
		}

		public ConfigPointGroup getFirstGroup() {
			return firstGroup;
		}

		public String getFirstCanonName() {
			return firstCanonName;
		}

		public ConfigPoint<?> getSecondPoint() {
			return secondPoint;
		}

		public ConfigPointGroup getSecondGroup() {
			return secondGroup;
		}

		public String getSecondCanonName() {
			return secondCanonName;
		}

		public String getConflictName() {
			return conflictName;
		}
		
		public String getMessage() {
			return "The point " + secondCanonName + " has a name that duplicates " +
					firstCanonName + ".  The conflicting name is '" + conflictName + "'";
		}
	}
	
	public static class InvalidValidationConfiguration extends ConstructionProblem {
		ConfigPoint<?> point;
		String canonName;
		Validator<?> valid;

		public InvalidValidationConfiguration(ConfigPoint<?> point, String canonName, Validator<?> valid) {
			this.point = point;
			this.canonName = canonName;
			this.valid = valid;
		}

		public ConfigPoint<?> getPoint() {
			return point;
		}

		public String getCanonName() {
			return canonName;
		}

		public Validator<?> getValidator() {
			return valid;
		}
		
		public String getMessage() {
			return "The point " + canonName + " has a validator of type " + 
					valid.getClass().getSimpleName() + " that is not configured correctly: " +
					valid.
		}
		
	}
}
