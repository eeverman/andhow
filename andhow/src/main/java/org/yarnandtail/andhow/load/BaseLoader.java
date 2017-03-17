package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.api.PropertyValue;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.api.PropertyGroup;
import org.yarnandtail.andhow.api.Validator;
import org.yarnandtail.andhow.api.SamplePrinter;
import org.yarnandtail.andhow.api.Property;
import org.yarnandtail.andhow.api.Loader;
import org.yarnandtail.andhow.api.ConstructionDefinition;
import java.util.Collections;
import java.util.List;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.DuplicatePropertyLoaderProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.ObjectConversionValueProblem;
import org.yarnandtail.andhow.internal.LoaderProblem.UnknownPropertyLoaderProblem;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends PropertyGroup> getClassConfig() {
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
	protected void attemptToAdd(ConstructionDefinition appConfigDef, List<PropertyValue> values, 
			ProblemList<Problem> loaderProblems, String key, String strValue) {
		
		key = TextUtil.trimToNull(key);
		
		if (key != null) {
			
			String effKey = appConfigDef.getNamingStrategy().toEffectiveName(key);
			
			Property prop = appConfigDef.getProperty(effKey);

			if (prop != null) {
				
				PropertyValue pv = null;
				
				try {
					pv = createValue(appConfigDef, prop, strValue);
				} catch (ParsingException e) {
					loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop), prop, e.getProblemText()));
				}
								
				if (pv != null) {
					PropertyValue dup = findDuplicateProperty(pv, values);

					if (dup == null) {
						values.add(pv);
					} else {
						loaderProblems.add(new DuplicatePropertyLoaderProblem(
							this, appConfigDef.getGroupForProperty(prop), prop));
					}
				}
				
			} else if (this.isUnrecognizedPropertyNamesConsideredAProblem()) {
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
	 * @param value The Object to be loaded to this property
	 */
	protected void attemptToAdd(ConstructionDefinition appConfigDef, List<PropertyValue> values, 
			ProblemList<Problem> loaderProblems, Property prop, Object value) {
		
		if (prop != null) {
			
			PropertyValue pv = null;
			
			if (value.getClass().equals(prop.getValueType().getDestinationType())) {

				pv = new PropertyValue(prop, value);

			} else if (value instanceof String) {

				try {
					pv = createValue(appConfigDef, prop, value.toString());
				} catch (ParsingException e) {
					loaderProblems.add(new LoaderProblem.StringConversionLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop), prop, e.getProblemText()));
				}

			} else {
				loaderProblems.add(
						new ObjectConversionValueProblem(this, appConfigDef.getGroupForProperty(prop), prop, value));
			}
			
			if (pv != null) {
				
				PropertyValue dup = findDuplicateProperty(pv, values);
				
				if (dup == null) {
					values.add(pv);
				} else {
					loaderProblems.add(new DuplicatePropertyLoaderProblem(
						this, appConfigDef.getGroupForProperty(prop), prop));
				}
			}

		}
	}
	
	protected PropertyValue findDuplicateProperty(PropertyValue current, List<PropertyValue> values) {
		for (PropertyValue ref : values) {
			if (current.getProperty().equals(ref.getProperty())) {
				return ref;
			}
		}
		return null;
	}
	
	protected <T> PropertyValue createValue(ConstructionDefinition appConfigDef, 
			Property<T> prop, String untrimmedString) throws ParsingException {
		
		ProblemList<Problem> problems = new ProblemList();
		T value = null;
		
		String trimmed = untrimmedString;

		if (prop.getValueType().getDestinationType().equals(String.class) && 
				this.isTrimmingRequiredForStringValues()) {

			trimmed = prop.getTrimmer().trim(untrimmedString);
		}


		if (trimmed != null || prop.getPropertyType().isFlag()) {

			value = prop.getValueType().parse(trimmed);

			for (Validator<T> v : prop.getValidators()) {
				if (! v.isValid(value)) {
					problems.add(new ValueProblem.InvalidValueProblem(
							this, appConfigDef.getGroupForProperty(prop), prop, value, v));
				}
			}
		} else {
			return null;	//No value to create
		}
		
		return new PropertyValue(prop, value, problems);
	}
	
}
