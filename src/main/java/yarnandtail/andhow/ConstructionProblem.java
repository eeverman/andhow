package yarnandtail.andhow;

/**
 * A problem building up the basic AppConfiguration, prior to attempting to load
 * any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class ConstructionProblem {
	
	protected PointDef refPoint;
	protected PointDef badPoint;
		
	/**
	 * For points that have some type of duplication w/ other points, this is the
	 * point that is duplicated (the earlier of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PointDef getRefPoint() {
		return refPoint;
	}
	
	/**
	 * For points that have some type of duplication w/ other points, this is the
	 * point that is the duplicate point (the later of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PointDef getBadPoint() {
		return badPoint;
	}
	
	/**
	 * A detailed description of the problem.
	 * 
	 * @return 
	 */
	public abstract String getMessage();
	
	public static class PointDef {
		ConfigPoint<?> point;
		Class<? extends ConfigPointGroup> group;
		String name;

		public PointDef(ConfigPoint<?> point, Class<? extends ConfigPointGroup> group, String name) {
			this.point = point;
			this.group = group;
			this.name = name;
		}

		public ConfigPoint<?> getPoint() {
			return point;
		}

		public Class<? extends ConfigPointGroup> getGroup() {
			return group;
		}

		public String getName() {
			return name;
		}
		
	}
	
	public static class NonUniqueNames extends ConstructionProblem {
		String conflictName;

		public NonUniqueNames(
				ConfigPoint<?> firstPoint, Class<? extends ConfigPointGroup> firstGroup, String firstCanonName, 
				ConfigPoint<?> secondPoint, Class<? extends ConfigPointGroup> secondGroup, String secondCanonName, 
				String conflictName) {
			
			refPoint = new PointDef(firstPoint, firstGroup, firstCanonName);
			badPoint = new PointDef(secondPoint, secondGroup, secondCanonName);
			this.conflictName = conflictName;
		}

		public String getConflictName() {
			return conflictName;
		}
		
		@Override
		public String getMessage() {
			return "The point " + badPoint.getName() + " has a name that duplicates " +
					refPoint.getName() + ".  The conflicting name/alias is '" + conflictName + "'";
		}
	}
	
	public static class DuplicatePoint extends ConstructionProblem {

		public DuplicatePoint(
				ConfigPoint<?> firstPoint, Class<? extends ConfigPointGroup> firstGroup, String firstCanonName, 
				ConfigPoint<?> secondPoint, Class<? extends ConfigPointGroup> secondGroup, String secondCanonName) {
			refPoint = new PointDef(firstPoint, firstGroup, firstCanonName);
			badPoint = new PointDef(secondPoint, secondGroup, secondCanonName);
		}
		
		@Override
		public String getMessage() {
			return "The AppConfig has been configured with a duplicate ConfigPoint instance, " +
				"meaning that two ConfigGroups are sharing the same ConfigPoint, which is not allowed. " +
				"The first point is " + refPoint.getName() + ", the second is " + badPoint.getName() + ". " +
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

		public SecurityException(Exception exception, Class<? extends ConfigPointGroup> group) {
			this.exception = exception;
			badPoint = new PointDef(null, group, null);
		}

		public Exception getException() {
			return exception;
		}
		
		@Override
		public String getMessage() {
			return "There was a security exception while trying to access members of " +
				"the ConfigPointGroup " + badPoint.getGroup().getCanonicalName() + ".  AppConfig needs " +
				"to be able to read class members via reflection, even if a package protected " +
				"class.  Perhaps there is security policy in place that is preventing it?";
		}
		
	}
	

	public static class InvalidDefaultValue extends ConstructionProblem {
		String invalidMessage;	
		//not all context is possible b/c we don't know the type of value to pass to the validator to re-validate

		public InvalidDefaultValue(ConfigPoint<?> point, Class<? extends ConfigPointGroup> group, String canonName, String invalidMessage) {
			this.badPoint = new PointDef(point, group, canonName);
			this.invalidMessage = invalidMessage;
		}

		public String getInvalidMessage() {
			return invalidMessage;
		}

		@Override
		public String getMessage() {
			return "The ConfigPoint " + badPoint.getName() + " is configured with a default " +
				"value that does not meet the validation requirements: " + invalidMessage;
		}
	}
	
	public static class InvalidValidationConfiguration extends ConstructionProblem {
		Validator<?> valid;

		public InvalidValidationConfiguration(
				ConfigPoint<?> point, Class<? extends ConfigPointGroup> group, String canonName, 
				Validator<?> valid) {
			
			this.badPoint = new PointDef(point, group, canonName);
			this.valid = valid;
		}

		public Validator<?> getValidator() {
			return valid;
		}
		
		public String getMessage() {
			return "The point " + badPoint.getName() + " has a validator of type " + 
					valid.getClass().getSimpleName() + " that is not configured correctly: " +
					valid.getInvalidSpecificationMessage();
		}
	}
}
