package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.util.Collections;
import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.DuplicatePropertyLoaderProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.ObjectConversionValueProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.UnknownPropertyLoaderProblem;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<?> getClassConfig() {
		return null;
	}
	
	@Override
	public List<Property> getInstanceConfig() {
		return Collections.emptyList();
	}
	
	@Override
	public SamplePrinter getConfigSamplePrinter() {
		return null;	//Each implementation needs to provide its own.
	}

	/**
	 * Util method to load a String to a property by name.
	 * 
	 * Used for text based loaders.
	 * 
	 * @param appConfigDef Used to look up the property name for find the actual property
	 * @param values List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of Problems to add to if there is a loader related problem
	 * @param key The property name
	 * @param strValue The property value 
	 */
	protected void attemptToAdd(StaticPropertyConfigurationInternal appConfigDef, List<ValidatedValue> values, 
			ProblemList<Problem> loaderProblems, String key, String strValue) {

		Property prop = mapNametoProperty(appConfigDef, key);

		if (prop != null) {

			ValidatedValue validatedValue = null;

			try {
				validatedValue = createValue(appConfigDef, prop, strValue);
			} catch (ParsingException e) {
				loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
					this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, e.getProblemText()));
			}

			attemptToAddIfNotDuplicate(appConfigDef, values, loaderProblems, validatedValue);

		} else if (this instanceof ReadLoader) {
			ReadLoader rl = (ReadLoader)this;
			if (rl.isUnknownPropertyAProblem()) {
				loaderProblems.add(new UnknownPropertyLoaderProblem(this, key));
			}
		}

	}

	/**
	 * Util method to load an Object value to a named Property.
	 *
	 * Intended for code-based loaders where Property names are specified with Object based keys.
	 * This would happen with hardcoded / in-code loaders, likely during testing, where property
	 * values can be specified as real objects, but the Properties themselves may not all be
	 * visible, so names are used instead of Property references.
	 *
	 * @param appConfigDef Used to look up the property name for find the actual property
	 * @param values List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of Problems to add to if there is a loader related problem
	 * @param key The property name
	 * @param value The property value as an Object, already of the expected type for the Property.
	 */
	protected void attemptToAdd(StaticPropertyConfigurationInternal appConfigDef, List<ValidatedValue> values,
															ProblemList<Problem> loaderProblems, String key, Object value) {

		Property prop = mapNametoProperty(appConfigDef, key);

		if (prop != null) {

			attemptToAdd(appConfigDef, values, loaderProblems, prop, value);

		} else if (this instanceof ReadLoader) {
			ReadLoader rl = (ReadLoader)this;
			if (rl.isUnknownPropertyAProblem()) {
				loaderProblems.add(new UnknownPropertyLoaderProblem(this, key));
			}
		}

	}

	/**
	 * Util method to attempt to load an object of an unknown type to a property.
	 * 
	 * Used for object based loaders where value are not in text form.
	 * This loader assumes the passed property is a valid property to to load to,
	 * but it will check to make sure it is not null, which is not treated as an error.
	 * 
	 * @param appConfigDef Used to look up the property name for find the actual property
	 * @param values List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of LoaderProblems to add to if there is a loader related problem
	 * @param prop The Property to load to
	 * @param value The Object to be loaded to this property.  If a String and that does
	 *              not match the Property type, parsing is attempted to convert it.
	 */
	protected void attemptToAdd(StaticPropertyConfigurationInternal appConfigDef, List<ValidatedValue> values, 
			ProblemList<Problem> loaderProblems, Property prop, Object value) {
		
		if (prop != null) {
			
			ValidatedValue validatedValue = null;
			
			if (value.getClass().equals(prop.getValueType().getDestinationType())) {

				validatedValue = new ValidatedValue(prop, value);

			} else if (value instanceof String) {

				try {
					validatedValue = createValue(appConfigDef, prop, value.toString());
				} catch (ParsingException e) {
					loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, e.getProblemText()));
				}

			} else {
				loaderProblems.add(
						new ObjectConversionValueProblem(this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, value));
			}

			attemptToAddIfNotDuplicate(appConfigDef, values, loaderProblems, validatedValue);

		}
	}

	/**
	 * Adds the ValidatedValue to the VV list if it is not a duplicate.
	 * This is the actual 'load' moment of the Loader:  Adding it to this list means
	 * that the value has been loaded.
	 *
	 * Loader subclasses should use the other attemptToAdd methods - this one is invoked by those.
	 *
	 * @param appConfigDef Used to look up the property name for find the actual property
	 * @param values List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of Problems to add to if there is a loader related problem
	 * @param validatedValue The validated value to load, which may be null which is a no-op.
	 */
	protected void attemptToAddIfNotDuplicate(StaticPropertyConfigurationInternal appConfigDef, List<ValidatedValue> values,
																						ProblemList<Problem> loaderProblems, ValidatedValue validatedValue) {

		if (validatedValue != null) {
			ValidatedValue dup = findDuplicateProperty(validatedValue, values);

			if (dup == null) {
				values.add(validatedValue);
			} else {
				loaderProblems.add(new DuplicatePropertyLoaderProblem(
						this, appConfigDef.getGroupForProperty(validatedValue.getProperty()).getProxiedGroup(), validatedValue.getProperty()));
			}
		}
	}
	
	protected ValidatedValue findDuplicateProperty(ValidatedValue current, List<ValidatedValue> values) {
		for (ValidatedValue ref : values) {
			if (current.getProperty().equals(ref.getProperty())) {
				return ref;
			}
		}
		return null;
	}
	
	protected <T> ValidatedValue createValue(StaticPropertyConfigurationInternal appConfigDef, 
			Property<T> prop, String untrimmedString) throws ParsingException {
		
		T value = null;
		
		String trimmed = untrimmedString;

		if (prop.getValueType().getDestinationType().equals(String.class) && 
				this.isTrimmingRequiredForStringValues()) {

			trimmed = prop.getTrimmer().trim(untrimmedString);
		}


		if (trimmed != null || prop.getPropertyType().isFlag()) {

			value = prop.getValueType().parse(trimmed);

		} else {
			return null;	//No value to create
		}
		
		return new ValidatedValue(prop, value);
	}

	/**
	 * Maps the passed Property name or alias to a Property or null if it cannot be found.
	 * @param appConfigDef The AppConfig
	 * @param name The name, which will be trimmed and converted to an effective name.
	 * @return The Property or null if it cannot be found.
	 */
	protected Property mapNametoProperty(StaticPropertyConfigurationInternal appConfigDef, String name) {

		name = TextUtil.trimToNull(name);

		if (name != null) {

			Property prop = appConfigDef.getProperty(name);
			return prop;

		} else {
			return null;
		}
	}
	
	@Override
	public void releaseResources() {
		//Nothing to do by default
	}
	
}
