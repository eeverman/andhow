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
	
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#getClassConfig()
	 */
	@Override
	public Class<?> getClassConfig() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#getInstanceConfig()
	 */
	@Override
	public List<Property> getInstanceConfig() {
		return Collections.emptyList();
	}
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#getConfigSamplePrinter()
	 */
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
		
		key = TextUtil.trimToNull(key);
		
		if (key != null) {
			
			String effKey = appConfigDef.getNamingStrategy().toEffectiveName(key);
			
			Property prop = appConfigDef.getProperty(effKey);

			if (prop != null) {
				
				ValidatedValue pv = null;
				
				try {
					pv = createValue(appConfigDef, prop, strValue);
				} catch (ParsingException e) {
					loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, e.getProblemText()));
				}
								
				if (pv != null) {
					ValidatedValue dup = findDuplicateProperty(pv, values);

					if (dup == null) {
						values.add(pv);
					} else {
						loaderProblems.add(new DuplicatePropertyLoaderProblem(
							this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop));
					}
				}
				
			} else if (this instanceof ReadLoader) {
				ReadLoader rl = (ReadLoader)this;
				if (rl.isUnknownPropertyAProblem()) {
					loaderProblems.add(new UnknownPropertyLoaderProblem(this, key));
				}
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
	 * @param value The Object to be loaded to this property
	 */
	protected void attemptToAdd(StaticPropertyConfigurationInternal appConfigDef, List<ValidatedValue> values, 
			ProblemList<Problem> loaderProblems, Property prop, Object value) {
		
		if (prop != null) {
			
			ValidatedValue pv = null;
			
			if (value.getClass().equals(prop.getValueType().getDestinationType())) {

				pv = new ValidatedValue(prop, value);

			} else if (value instanceof String) {

				try {
					pv = createValue(appConfigDef, prop, value.toString());
				} catch (ParsingException e) {
					loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, e.getProblemText()));
				}

			} else {
				loaderProblems.add(
						new ObjectConversionValueProblem(this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop, value));
			}
			
			if (pv != null) {
				
				ValidatedValue dup = findDuplicateProperty(pv, values);
				
				if (dup == null) {
					values.add(pv);
				} else {
					loaderProblems.add(new DuplicatePropertyLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop).getProxiedGroup(), prop));
				}
			}

		}
	}
	
	/**
	 * Find duplicate property.
	 *
	 * @param current the current
	 * @param values the values
	 * @return the validated value
	 */
	protected ValidatedValue findDuplicateProperty(ValidatedValue current, List<ValidatedValue> values) {
		for (ValidatedValue ref : values) {
			if (current.getProperty().equals(ref.getProperty())) {
				return ref;
			}
		}
		return null;
	}
	
	/**
	 * Creates the value.
	 *
	 * @param <T> the generic type
	 * @param appConfigDef the app config def
	 * @param prop the prop
	 * @param untrimmedString the untrimmed string
	 * @return the validated value
	 * @throws ParsingException the parsing exception
	 */
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
	
	/* (non-Javadoc)
	 * @see org.yarnandtail.andhow.api.Loader#releaseResources()
	 */
	@Override
	public void releaseResources() {
		//Nothing to do by default
	}
	
}
