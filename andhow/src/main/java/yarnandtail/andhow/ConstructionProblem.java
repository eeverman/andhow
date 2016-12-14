package yarnandtail.andhow;

/**
 * A problem bootstrapping the AndHow, prior to attempting to load any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class ConstructionProblem extends Problem {
	
	/** The Property that actually has the problem */
	protected PropertyDef badPropertyDef;
	
	/** For construction problems that duplicate or reference another Property... */
	protected PropertyDef refPropertyDef;
	
	/**
	 * For Properties that have some type of duplication w/ other points, this is the
	 * Property that is duplicated (the earlier of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PropertyDef getRefProperty() {
		return refPropertyDef;
	}

	/**
	 * For Properties that have some type of duplication w/ other points, this is the
	 * property that is the duplicate one (the later of the two duplicates).
	 * @return May return null if not applicable.
	 */
	public PropertyDef getBadProperty() {
		return badPropertyDef;
	}
	
	public static class NonUniqueNames extends ConstructionProblem {
		String conflictName;

		public NonUniqueNames(
				Class<? extends PropertyGroup> refGroup, Property<?> refProperty, 
				Class<? extends PropertyGroup> badGroup, Property<?> badProperty, String conflictName) {
			
			this.refPropertyDef = new PropertyDef(refGroup, refProperty);
			this.badPropertyDef = new PropertyDef(badGroup, badProperty);
			this.conflictName = conflictName;
		}

		public String getConflictName() {
			return conflictName;
		}
		
		@Override
		public String getMessage() {
			return TextUtil.format("The property {} has a name that duplicates {}. " +
					"The conflicting name/alias is '{}'.  All names must be unique.",
					badPropertyDef.getName(), refPropertyDef.getName(), conflictName);
		}
	}
	
	public static class DuplicateProperty extends ConstructionProblem {

		public DuplicateProperty(
				Class<? extends PropertyGroup> refGroup, Property<?> refProperty, 
				Class<? extends PropertyGroup> badGroup, Property<?> badProperty) {
			
			this.refPropertyDef = new PropertyDef(refGroup, refProperty);
			this.badPropertyDef = new PropertyDef(badGroup, badProperty);
		}
		
		@Override
		public String getMessage() {
			return TextUtil.format(
					"The Property {} is actually the same instance as {} - " +
					"The containing PropertyGroups are sharing a reference to the same " +
					"Property instance.  Properties must each be independant " +
					"instances because they each identify unique values.", 
				badPropertyDef.getName(), refPropertyDef.getName());
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
			return TextUtil.format("Two loaders of type {}  " +
					"are actually the same Loader instance. " +
					"Multiple loaders of the same type are allowed, but they must be separate instances.",
				loader.getClass().getCanonicalName());
		}
	}
	
	public static class SecurityException extends ConstructionProblem {
		Exception exception;

		public SecurityException(Exception exception, Class<? extends PropertyGroup> group) {
			this.exception = exception;
			badPropertyDef = new PropertyDef(group, null);
		}

		public Exception getException() {
			return exception;
		}
		
		@Override
		public String getMessage() {
			return TextUtil.format(
				"A security exception blocked to access members of the PropertyGroup {}.  " +
				"{} must be able to read class members via reflection, even package protected " +
				"interfaces.  Is there a security policy in place that is preventing this?",
				badPropertyDef.getGroup().getCanonicalName(), AndHow.ANDHOW_INLINE_NAME);
		}
		
	}
	
	public static class InvalidDefaultValue extends ConstructionProblem {
		String invalidMessage;	

		public InvalidDefaultValue(Class<? extends PropertyGroup> group, Property<?> prop, String invalidMessage) {
			this.badPropertyDef = new PropertyDef(group, prop);
			this.invalidMessage = invalidMessage;
		}

		public String getInvalidMessage() {
			return invalidMessage;
		}

		@Override
		public String getMessage() {
			return TextUtil.format(
					"The Property {} has a default value that does not pass validation: {}",
				badPropertyDef.getName(), invalidMessage);
		}
	}
	
	public static class InvalidValidationConfiguration extends ConstructionProblem {
		Validator<?> valid;

		public InvalidValidationConfiguration(
				Class<? extends PropertyGroup> group, Property<?> property, Validator<?> valid) {
			
			this.badPropertyDef = new PropertyDef(group, property);
			this.valid = valid;
		}

		public Validator<?> getValidator() {
			return valid;
		}
		
		public String getMessage() {
			return TextUtil.format(
					"The Property {} has a Validator of type {} that is not configured correctly: {}",
				badPropertyDef.getName(), valid.getClass().getSimpleName(), valid.getInvalidSpecificationMessage());
		}
	}
}
