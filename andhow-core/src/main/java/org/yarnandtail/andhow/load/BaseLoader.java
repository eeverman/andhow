package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.*;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;
import org.yarnandtail.andhow.util.TextUtil;

import java.util.Collections;
import java.util.List;

/**
 * Base implementation of a Loader that provides some basic services and behavior that all loader
 * can share and use.
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
		return null;  //Each implementation needs to provide its own.
	}

	/**
	 * Util method to load a String to a property by name.
	 * <p>
	 * Used for text based loaders.
	 *
	 * @param appConfigDef   Used to look up the property name for find the actual property
	 * @param values         List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of Problems to add to if there is a loader related problem
	 * @param key            The property name
	 * @param strValue       The property value
	 */
	protected void attemptToAdd(PropertyConfigurationInternal appConfigDef, List<ValidatedValue> values,
															ProblemList<Problem> loaderProblems, String key, String strValue) {

		Property prop = mapNametoProperty(appConfigDef, key);

		if (prop != null) {

			ValidatedValue validatedValue = null;

			try {
				validatedValue = createValue(prop, strValue);
			} catch (ParsingException e) {
				loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, e.getProblemText()));
			}

			attemptToAddIfNotDuplicate(appConfigDef, values, loaderProblems, validatedValue);

		} else if (this instanceof ReadLoader) {
			ReadLoader rl = (ReadLoader) this;
			if (rl.isUnknownPropertyAProblem()) {
				loaderProblems.add(new UnknownPropertyLoaderProblem(this, key));
			}
		}

	}

	/**
	 * Util method to load an Object value to a named Property.
	 * <p>
	 * Intended for code-based loaders where Property names are specified with Object based keys.
	 * This would happen with hardcoded / in-code loaders, likely during testing, where property
	 * values can be specified as real objects, but the Properties themselves may not all be
	 * visible, so names are used instead of Property references.
	 *
	 * @param appConfigDef   Used to look up the property name for find the actual property
	 * @param values         List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of Problems to add to if there is a loader related problem
	 * @param key            The property name
	 * @param value          The property value as an Object, already of the expected type for the Property.
	 */
	protected void attemptToAdd(PropertyConfigurationInternal appConfigDef, List<ValidatedValue> values,
															ProblemList<Problem> loaderProblems, String key, Object value) {

		Property prop = mapNametoProperty(appConfigDef, key);

		if (prop != null) {

			attemptToAdd(appConfigDef, values, loaderProblems, prop, value);

		} else if (this instanceof ReadLoader) {
			ReadLoader rl = (ReadLoader) this;
			if (rl.isUnknownPropertyAProblem()) {
				loaderProblems.add(new UnknownPropertyLoaderProblem(this, key));
			}
		}

	}

	/**
	 * Util method to attempt to load an object of an unknown type to a property.
	 * <p>
	 * Used for object based loaders where value are not in text form.
	 * This loader assumes the passed property is a valid property to load to,
	 * but it will check to make sure it is not null, which is not treated as an error.
	 *
	 * @param appConfigDef   Used to look up the property name for find the actual property
	 * @param values         List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of LoaderProblems to add to if there is a loader related problem
	 * @param prop           The Property to load to
	 * @param value          The Object to be loaded to this property.  If a String and that does
	 *                       not match the Property type, parsing is attempted to convert it.
	 */
	protected void attemptToAdd(PropertyConfigurationInternal appConfigDef, List<ValidatedValue> values,
															ProblemList<Problem> loaderProblems, Property prop, Object value) {

		if (prop != null) {

			ValidatedValue validatedValue = null;

			if (value.getClass().equals(prop.getValueType().getDestinationType())) {

				validatedValue = new ValidatedValue(prop, value);

			} else if (value instanceof String) {

				try {
					validatedValue = createValue(prop, value.toString());
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
	 * <p>
	 * Loader subclasses should use the other attemptToAdd methods - this one is invoked by those.
	 *
	 * @param appConfigDef   Used to look up the property name for find the actual property
	 * @param values         List of PropertyValues to add to, which should be only the value of this loader.
	 * @param loaderProblems A list of Problems to add to if there is a loader related problem
	 * @param validatedValue The validated value to load, which may be null which is a no-op.
	 */
	protected void attemptToAddIfNotDuplicate(
			PropertyConfigurationInternal appConfigDef, List<ValidatedValue> values,
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

	/**
	 * Central method for parsing Strings into a Property value.
	 * <p>
	 * This method throws a ParsingException if the value cannot be parsed into the destination
	 * type.  If that happens, the caller should consider it a {@link LoaderProblem}, not a
	 * {@link org.yarnandtail.andhow.internal.ValueProblem}.
	 * <p>
	 * If a loader bypasses this method, it must replicate the trimming and (non) validation logic
	 * in this method to ensure String are trimmed appropriately.  Specifically:
	 * <ul>
	 * <li>For Properties of destination type String, this method applies the Property's
	 * trimmer only if {@link #isTrimmingRequiredForStringValues} returns true.</li>
	 * <li>For non-String type Properties, the Property's trimmer is always applied.</li>
	 * <li>After trimming (or not), the Property's ValueType.parse() method is called.</li>
	 * </ul>
	 * @param prop The Property the untrimmedString String holds the value for
	 * @param untrimmedString A String to be parsed into the destination type of the Property.
	 * @param <T> The destination type of the Property (i.e., an IntProp is Integer).
	 * @return A {@link ValidatedValue} containing the Property and the resulting value.  Validation
	 *   has not yet been applied to the ValidatedValue, so it won't have any Problems listed.
	 * @throws ParsingException If the String value cannot be parsed into the destination type.
	 */
	protected <T> ValidatedValue createValue(
			Property<T> prop, String untrimmedString) throws ParsingException {

		T value = null;

		String effectiveString = untrimmedString;

		// Trim if its not a Str type, or it is a str type and trimming is req'ed for Str's.
		boolean requiresTrim =
				(! prop.getValueType().getDestinationType().equals(String.class)) ||
				(prop.getValueType().getDestinationType().equals(String.class) &&
								 this.isTrimmingRequiredForStringValues());

		if (requiresTrim) {
			effectiveString = prop.getTrimmer().trim(untrimmedString);
		}

		if (effectiveString != null || prop.getPropertyType().isFlag()) {
			value = prop.getValueType().parse(effectiveString);
		} else {
			return null;  //No value to create
		}

		return new ValidatedValue(prop, value);
	}

	/**
	 * Maps the passed Property name or alias to a Property or null if it cannot be found.
	 * <p>
	 * @param appConfigDef The AppConfig
	 * @param name         The name, which will be trimmed and converted to an effective name.
	 * @return The Property or null if it cannot be found.
	 */
	protected Property mapNametoProperty(PropertyConfigurationInternal appConfigDef, String name) {

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
