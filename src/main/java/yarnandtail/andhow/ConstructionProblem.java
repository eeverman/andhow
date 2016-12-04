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
		Class<? extends ConfigPointGroup> firstGroup;
		String firstCanonName;
		ConfigPoint<?> secondPoint;
		Class<? extends ConfigPointGroup> secondGroup;
		String secondCanonName;
		String conflictName;

		public NonUniqueNames(
				ConfigPoint<?> firstPoint, Class<? extends ConfigPointGroup> firstGroup, String firstCanonName, 
				ConfigPoint<?> secondPoint, Class<? extends ConfigPointGroup> secondGroup, String secondCanonName, 
				String conflictName) {
			
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

		public Class<? extends ConfigPointGroup> getFirstGroup() {
			return firstGroup;
		}

		public String getFirstCanonName() {
			return firstCanonName;
		}

		public ConfigPoint<?> getSecondPoint() {
			return secondPoint;
		}

		public Class<? extends ConfigPointGroup> getSecondGroup() {
			return secondGroup;
		}

		public String getSecondCanonName() {
			return secondCanonName;
		}

		public String getConflictName() {
			return conflictName;
		}
		
		@Override
		public String getMessage() {
			return "The point " + secondCanonName + " has a name that duplicates " +
					firstCanonName + ".  The conflicting name is '" + conflictName + "'";
		}
	}
	
	public static class DuplicatePoint extends ConstructionProblem {
		ConfigPoint<?> firstPoint;
		ConfigPointGroup firstGroup;
		String firstCanonName;
		ConfigPoint<?> secondPoint;
		ConfigPointGroup secondGroup;
		String secondCanonName;

		public DuplicatePoint(
				ConfigPoint<?> firstPoint, ConfigPointGroup firstGroup, String firstCanonName, 
				ConfigPoint<?> secondPoint, ConfigPointGroup secondGroup, String secondCanonName) {
			this.firstPoint = firstPoint;
			this.firstGroup = firstGroup;
			this.firstCanonName = firstCanonName;
			this.secondPoint = secondPoint;
			this.secondGroup = secondGroup;
			this.secondCanonName = secondCanonName;
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
		
		@Override
		public String getMessage() {
			return "The AppConfig has been configured with a duplicate ConfigPoint instance, " +
				"meaning that two ConfigGroups are sharing the same ConfigPoint, which is not allowed. " +
				"The first point is " + firstCanonName + ", the second is " + secondCanonName + ". " +
				"The first part of each name of the ConfigGroup containing the points.";
		}
		

	}
	
	public static class DuplicateLoader extends ConstructionProblem {
		Loader loader;

		public DuplicateLoader(Loader loader) {
			this.loader = loader;
		}

		public Loader getLoader() {
			return loader;
		}

		@Override
		public String getMessage() {
			return "The AppConfig has been configured with a duplicate Loader instance " +
					"of type " + loader.getClass().getCanonicalName() + ".  " +
					"Multiple loaders of the same type are OK, but they must be separate instances.";
		}
	}
	
	public static class SecurityException extends ConstructionProblem {
		Exception exception;
		Class<? extends ConfigPointGroup> group;

		public SecurityException(Exception exception, Class<? extends ConfigPointGroup> group) {
			this.exception = exception;
			this.group = group;
		}

		public Exception getException() {
			return exception;
		}

		public Class<? extends ConfigPointGroup> getGroup() {
			return group;
		}
		
		@Override
		public String getMessage() {
			return "There was a security exception while trying to access members of " +
				"the ConfigPointGroup " + group.getCanonicalName() + ".  AppConfig needs " +
				"to be able to read class members via reflection, even if a package protected " +
				"class.  Perhaps there is security policy in place that is preventing it?";
		}
		
	}
	

	public static class InvalidDefaultValue extends ConstructionProblem {
		ConfigPoint<?> point;
		String canonName;
		String invalidMessage;	
		//not all context is possible b/c we don't know the type of value to pass to the validator to re-validate

		public InvalidDefaultValue(ConfigPoint<?> point, String canonName, String invalidMessage) {
			this.point = point;
			this.canonName = canonName;
			this.invalidMessage = invalidMessage;
		}

		public ConfigPoint<?> getPoint() {
			return point;
		}

		public String getCanonName() {
			return canonName;
		}

		public String getInvalidMessage() {
			return invalidMessage;
		}

		@Override
		public String getMessage() {
			return "The ConfigPoint " + canonName + " is configured with a default " +
				"value that does not meet the validation requirements: " + invalidMessage;
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
					valid.getInvalidSpecificationMessage();
		}
	}
}
