package org.yarnandtail.andhow.internal;

import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * A problem bootstrapping the AndHow, prior to attempting to load any values.
 * 
 * 
 * @author ericeverman
 */
public abstract class ConstructionProblem implements Problem {

	private static final String SEE_USER_GUIDE =
			"  See user guide for configuration docs & examples: https://www.andhowconfig.org/user-guide";


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
	
	@Override
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}
	
	
	
	public static class NonUniqueNames extends ConstructionProblem {
		String conflictName;

		public NonUniqueNames(
				GroupProxy refGroup, Property<?> refProperty, 
				GroupProxy badGroup, Property<?> badProperty, String conflictName) {
			
			this.refPropertyCoord = new PropertyCoord(refGroup.getProxiedGroup(), refProperty);
			this.badPropertyCoord = new PropertyCoord(badGroup.getProxiedGroup(), badProperty);
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
				GroupProxy refGroup, Property<?> refProperty, 
				GroupProxy badGroup, Property<?> badProperty) {
			
			this.refPropertyCoord = new PropertyCoord(refGroup.getProxiedGroup(), refProperty);
			this.badPropertyCoord = new PropertyCoord(badGroup.getProxiedGroup(), badProperty);
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

	
	public static class LoaderPropertyIsNull extends ConstructionProblem {
		Loader loader;
		
		public LoaderPropertyIsNull(Loader loader) {
			this.loader = loader;
		}

		public Loader getLoader() {
			return loader;
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("{} loader", 
					loader.getClass().getSimpleName());
		}
		
		@Override
		public String getProblemDescription() {
			return "This loader expects a Property passed in its constructor, " +
					"however, a null value was passed.  " +
					"This is not a property pointing to null configuration value - " +
					"The actual reference to the property is null. ";
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
			return TextUtil.format("{} loader's {} property of type {}", 
					loader.getClass().getSimpleName(),
					property.getClass().getSimpleName(),
					property.getValueType().getDestinationType().getSimpleName());
		}
		
		@Override
		public String getProblemDescription() {
			return TextUtil.format("This loader has a {} property passed in its constructor, " +
					"but the property is not part of any of the PropertyGroups added during AndHow's initiation. " +
					"Ensure the property is declared in a PropertyGroup and that the group " +
					"is added to AndHow in its initiation.",
					property.getClass().getSimpleName());
		}
	}
	
	public static class SecurityException extends ConstructionProblem {
		Exception exception;
		
		public SecurityException(Exception exception, Class<?> group) {
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
				"To fix, Properties must either be public or the JVM security policies " +
				"that are preventing reflection visibility must be disabled.",
				AndHow.ANDHOW_INLINE_NAME);
		}
	}
	

	public static class PropertyNotPartOfGroup extends ConstructionProblem {	

		public PropertyNotPartOfGroup(GroupProxy group, Property<?> prop) {
			this.badPropertyCoord = new PropertyCoord(group.getProxiedGroup(), prop);
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

		public ExportException(Exception exception, GroupProxy group, String message) {
			this.exception = exception;
			this.message = message;
			badPropertyCoord = new PropertyCoord(group.getProxiedGroup(), null);
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
				"An error occurred while initiating the value export.  The message was: {}",
				message);
		}
	}
	
	public static class InvalidDefaultValue extends ConstructionProblem {
		String invalidMessage;	

		public InvalidDefaultValue(GroupProxy group, Property<?> prop, String invalidMessage) {
			this.badPropertyCoord = new PropertyCoord(group.getProxiedGroup(), prop);
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
				GroupProxy group, Property<?> property, Validator<?> valid) {
			
			this.badPropertyCoord = new PropertyCoord(group.getProxiedGroup(), property);
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
	
	public static class TooManyAndHowInitInstances extends ConstructionProblem {
		List<String> names = new ArrayList();

		public TooManyAndHowInitInstances(List<? extends AndHowInit> instances) {
			for (AndHowInit init : instances) {
				names.add(init.getClass().getCanonicalName());
			}
		}

		public List<String> getInstanceNames() {
			return names;
		}
		
		@Override
		public String getProblemDescription() {
			String joined = String.join(", ", names);
			
			return TextUtil.format(
					"There can be only be one instance each of {} and AndHowTestInit on "
						+ "the classpath, but multiple were found: {}",
					AndHowInit.class.getCanonicalName(), joined);
		}
	}
	
	public static class InitiationLoopException extends ConstructionProblem {
		AndHow.Initialization originalInit;
		AndHow.Initialization secondInit;

		public InitiationLoopException(AndHow.Initialization originalInit, AndHow.Initialization secondInit) {
			this.originalInit = originalInit;
			this.secondInit = secondInit;
		}

		public AndHow.Initialization getOriginalInit() {
			return originalInit;
		}

		public AndHow.Initialization getSecondInit() {
			return secondInit;
		}

		
		@Override
		public String getProblemDescription() {
			
			return "AndHow detected a loop during initiation.  "
					+ "Likely causes are calls [Property].value() or AndHow.instance() in an unexpected place, such as: " + System.lineSeparator()
					+ "- Static initiation blocks or static variable initiation values, e.g., 'static int MyVar = [Some AndHow Prop].getValue()'" + System.lineSeparator()
					+ "- An AndHow Property that refers to the value of another AndHow property in its construction" + System.lineSeparator()
					+ "- An AndHowInit implementation that calls one of these methods in its getConfiguration method" + System.lineSeparator()
					+ "- A custom AndHowConfiguration instance using one of these methods (likely an exotic test setup)"
					+ "::The first line in the stack trace following this error referring to your application code is likely causing the initiation loop::";
		}
		
		@Override
		public String getFullMessage() {
			return getProblemDescription();
		}
	}

	public static class SetConfigCalledDuringInitializationException extends ConstructionProblem {

		@Override
		public String getProblemDescription() {

			return "AndHow is initializing, so AndHow.setConfig() cannot be called. "
					+ "This is most likely due to a custom AndHowConfiguration instance that "
					+ "calls AndHow.setConfig() in one of its methods. " + SEE_USER_GUIDE;
		}

		@Override
		public String getFullMessage() {
			return getProblemDescription();
		}
	}

	public static class SetConfigCalledDuringFindConfigException extends ConstructionProblem {

		@Override
		public String getProblemDescription() {

			return "AndHow.setConfig() was called during the invocation of AndHow.findConfig(), " +
					"which is not allowed.  Likely caused by setConfig() called inside of AndHowInit.getConfiguration(). " +
					SEE_USER_GUIDE;
		}

		@Override
		public String getFullMessage() {
			return getProblemDescription();
		}
	}
}
