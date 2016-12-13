package yarnandtail.andhow;

/**
 * A problem bootstrapping the AndHow, prior to attempting to load any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class ConstructionProblem {
	
	protected PropertyDef refPropertyDef;
	protected PropertyDef badPropertyDef;
		
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
	
	/**
	 * A detailed description of the problem.
	 * 
	 * @return 
	 */
	public abstract String getMessage();
	
	public static class PropertyDef {
		Property<?> property;
		Class<? extends PropertyGroup> group;
		String name;

		public PropertyDef(Property<?> prop, Class<? extends PropertyGroup> group, String name) {
			this.property = prop;
			this.group = group;
			this.name = name;
		}

		public Property<?> getProperty() {
			return property;
		}

		public Class<? extends PropertyGroup> getGroup() {
			return group;
		}

		public String getName() {
			return name;
		}
		
	}
	
	public static class NonUniqueNames extends ConstructionProblem {
		String conflictName;

		public NonUniqueNames(
				Property<?> refProperty, Class<? extends PropertyGroup> refGroup, String refCanonName, 
				Property<?> badProperty, Class<? extends PropertyGroup> badGroup, String badCanonName, 
				String conflictName) {
			
			this.refPropertyDef = new PropertyDef(refProperty, refGroup, refCanonName);
			this.badPropertyDef = new PropertyDef(badProperty, badGroup, badCanonName);
			this.conflictName = conflictName;
		}

		public String getConflictName() {
			return conflictName;
		}
		
		@Override
		public String getMessage() {
			return "The property " + badPropertyDef.getName() + " has a name that duplicates " +
					refPropertyDef.getName() + ".  The conflicting name/alias is '" + conflictName + "'";
		}
	}
	
	public static class DuplicateProperty extends ConstructionProblem {

		public DuplicateProperty(
				Property<?> refProperty, Class<? extends PropertyGroup> refGroup, String refCanonName, 
				Property<?> badProperty, Class<? extends PropertyGroup> badGroup, String badCanonName) {
			this.refPropertyDef = new PropertyDef(refProperty, refGroup, refCanonName);
			this.badPropertyDef = new PropertyDef(badProperty, badGroup, badCanonName);
		}
		
		@Override
		public String getMessage() {
			return AndHow.ANDHOW_INLINE_NAME +  " has been configured with a duplicate Property instance, " +
				"meaning that one or more PropertyGroups are sharing the same Property instance, which is not allowed. " +
				"The first Property is " + refPropertyDef.getName() + ", the second is " + badPropertyDef.getName() + ". " +
				"The first part of each name is the PropertyGroup containing the properties.";
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
			return AndHow.ANDHOW_INLINE_NAME + " has been configured with a duplicate Loader instance " +
					"of type " + loader.getClass().getCanonicalName() + ".  " +
					"Multiple loaders of the same type are OK, but they must be separate instances.";
		}
	}
	
	public static class SecurityException extends ConstructionProblem {
		Exception exception;

		public SecurityException(Exception exception, Class<? extends PropertyGroup> group) {
			this.exception = exception;
			badPropertyDef = new PropertyDef(null, group, null);
		}

		public Exception getException() {
			return exception;
		}
		
		@Override
		public String getMessage() {
			return "There was a security exception while trying to access members of " +
				"the PropertyGroup " + badPropertyDef.getGroup().getCanonicalName() + ".  " +
				AndHow.ANDHOW_INLINE_NAME + " must be able to read class members via reflection, even from a package protected " +
				"interface.  Perhaps there is security policy in place that is preventing it?";
		}
		
	}
	

	public static class InvalidDefaultValue extends ConstructionProblem {
		String invalidMessage;	
		//not all context is possible b/c we don't know the type of value to pass to the validator to re-validate

		public InvalidDefaultValue(Property<?> prop, Class<? extends PropertyGroup> group, String canonName, String invalidMessage) {
			this.badPropertyDef = new PropertyDef(prop, group, canonName);
			this.invalidMessage = invalidMessage;
		}

		public String getInvalidMessage() {
			return invalidMessage;
		}

		@Override
		public String getMessage() {
			return "The Property " + badPropertyDef.getName() + " is configured with a default " +
				"value that does not meet the validation requirements: " + invalidMessage;
		}
	}
	
	public static class InvalidValidationConfiguration extends ConstructionProblem {
		Validator<?> valid;

		public InvalidValidationConfiguration(
				Property<?> property, Class<? extends PropertyGroup> group, String canonName, 
				Validator<?> valid) {
			
			this.badPropertyDef = new PropertyDef(property, group, canonName);
			this.valid = valid;
		}

		public Validator<?> getValidator() {
			return valid;
		}
		
		public String getMessage() {
			return "The property " + badPropertyDef.getName() + " has a validator of type " + 
					valid.getClass().getSimpleName() + " that is not configured correctly: " +
					valid.getInvalidSpecificationMessage();
		}
	}
}
