package yarnandtail.andhow;

/**
 * A problem bootstrapping the AndHow, prior to attempting to load any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class ConstructionProblem extends Problem {
	
	/** The Property that actually has the problem */
	protected PropertyCoord badPropertyCoord;
	
	/** For construction problems that duplicate or reference another Property... */
	protected PropertyCoord refPropertyCoord;
	
	/**
	 * For Properties that have some type of duplication w/ other properties, this is the
	 * Property that is duplicated (the earlier of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PropertyCoord getRefPropertyCoord() {
		return refPropertyCoord;
	}

	/**
	 * The Property that has the problem.
	 * 
	 * @return May return null if not applicable.
	 */
	public PropertyCoord getBadPropertyCoord() {
		return badPropertyCoord;
	}
	
	@Override
	public String getProblemContext() {
		if (badPropertyCoord != null) {
			return TextUtil.format("Property {}", badPropertyCoord.getPropName());
		} else {
			return UNKNOWN;
		}
	}
	
	
	
	public static class NonUniqueNames extends ConstructionProblem {
		String conflictName;

		public NonUniqueNames(
				Class<? extends PropertyGroup> refGroup, Property<?> refProperty, 
				Class<? extends PropertyGroup> badGroup, Property<?> badProperty, String conflictName) {
			
			this.refPropertyCoord = new PropertyCoord(refGroup, refProperty);
			this.badPropertyCoord = new PropertyCoord(badGroup, badProperty);
			this.conflictName = conflictName;
		}

		public String getConflictName() {
			return conflictName;
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format("Has the name {} which a name in use by {}. " +
					"All names must be unique.",
					conflictName, refPropertyCoord.getPropName());
		}
	}
	
	public static class DuplicateProperty extends ConstructionProblem {

		public DuplicateProperty(
				Class<? extends PropertyGroup> refGroup, Property<?> refProperty, 
				Class<? extends PropertyGroup> badGroup, Property<?> badProperty) {
			
			this.refPropertyCoord = new PropertyCoord(refGroup, refProperty);
			this.badPropertyCoord = new PropertyCoord(badGroup, badProperty);
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format("Is the same instance as {} - " +
					"The containing PropertyGroups are sharing a reference to the same " +
					"Property instance.  Properties must each be independant " +
					"instances because they each identify unique values.", 
				refPropertyCoord.getPropName());
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
		public String getProblemContext() {
			return TextUtil.format("Multiple loaders of type {}", loader.getClass().getCanonicalName());
		}
		
		@Override
		public String getProblemDescription() {
			return "The same Loader instance has been added multiple times. " +
					"Loaders of the same type are allowed, but they must be separate instances.";
		}
	}
	
	public static class SecurityException extends ConstructionProblem {
		Exception exception;

		public SecurityException(Exception exception, Class<? extends PropertyGroup> group) {
			this.exception = exception;
			badPropertyCoord = new PropertyCoord(group, null);
		}

		public Exception getException() {
			return exception;
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("PropertyGroup {}", badPropertyCoord.getGroup().getCanonicalName());
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format(
				"A security exception was thrown while trying to read class members.  " +
				"{} must read PropertyGroup class members via reflection to build Property names. " +
				"To fix, ensure that all PropertyGroup members are public or turn off " +
				"JVM security policies that might be preventing this.",
				AndHow.ANDHOW_INLINE_NAME);
		}
	}
	
	public static class InvalidDefaultValue extends ConstructionProblem {
		String invalidMessage;	

		public InvalidDefaultValue(Class<? extends PropertyGroup> group, Property<?> prop, String invalidMessage) {
			this.badPropertyCoord = new PropertyCoord(group, prop);
			this.invalidMessage = invalidMessage;
		}

		public String getInvalidMessage() {
			return invalidMessage;
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format(
					"Has a default value that does not pass validation: {}",
				invalidMessage);
		}
	}
	
	public static class InvalidValidationConfiguration extends ConstructionProblem {
		Validator<?> valid;

		public InvalidValidationConfiguration(
				Class<? extends PropertyGroup> group, Property<?> property, Validator<?> valid) {
			
			this.badPropertyCoord = new PropertyCoord(group, property);
			this.valid = valid;
		}

		public Validator<?> getValidator() {
			return valid;
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format(
					"Has a Validator of type {} that is not configured correctly: {}",
				valid.getClass().getSimpleName(), valid.getInvalidSpecificationMessage());
		}
	}
}
