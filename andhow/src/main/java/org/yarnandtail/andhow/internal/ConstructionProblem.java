package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.Loader;
import org.yarnandtail.andhow.Problem;
import org.yarnandtail.andhow.Property;
import org.yarnandtail.andhow.PropertyGroup;
import org.yarnandtail.andhow.Validator;
import org.yarnandtail.andhow.util.TextUtil;

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
			return TextUtil.format("Has the name '{}' which is a name or alias in use by {}. " +
					"All names must be unique and, for the BasicNamingStrategy, " +
					"must be unique in a case insensitive way.",
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
	
	public static class LoaderPropertyNotRegistered extends ConstructionProblem {
		Loader loader;
		Property property;
		
		public LoaderPropertyNotRegistered(Loader loader, Property property) {
			this.loader = loader;
			this.property = property;
		}

		public Loader getLoader() {
			return loader;
		}
		
		public Property getProperty() {
			return property;
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("The {} loader's Property of type {}", 
					loader.getClass().getCanonicalName(), 
					property.getValueType().getDestinationType().getSimpleName());
		}
		
		@Override
		public String getProblemDescription() {
			return "This loader has a Property declared in its constructor, " +
					"but that property is not added to the application configuration. " +
					"Add the Property to a PropertyGroup and add the group to " +
					"the configuration via builder.group(myGroup.class).";
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
	

	public static class PropertyNotPartOfGroup extends ConstructionProblem {	

		public PropertyNotPartOfGroup(Class<? extends PropertyGroup> group, Property<?> prop) {
			this.badPropertyCoord = new PropertyCoord(group, prop);
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format(
					"This property is not part of the group its is being added to.");
		}
	}
	
	public static class ExportException extends ConstructionProblem {
		Exception exception;
		String message;

		public ExportException(Exception exception, Class<? extends PropertyGroup> group, String message) {
			this.exception = exception;
			this.message = message;
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
				"An error occured while initiating the value export.  The message was: {}",
				message);
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
