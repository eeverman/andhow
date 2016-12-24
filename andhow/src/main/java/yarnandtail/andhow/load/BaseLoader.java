package yarnandtail.andhow.load;

import yarnandtail.andhow.ParsingException;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.LoaderProblem.DuplicatePropertyLoaderProblem;
import yarnandtail.andhow.LoaderProblem.UnknownPropertyLoaderProblem;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.ValueProblem;
import yarnandtail.andhow.internal.RuntimeDefinition;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends PropertyGroup> getLoaderConfig() {
		return null;
	}
	
	/**
	 * 
	 * @param appConfigDef Used to look up the property name for find the actual property
	 * @param values List of PropertyValues to add to.
	 * @param loaderProblems A list of LoaderProblems to add to if there is a loader related problem
	 * @param key The property name
	 * @param strValue The property value
	 * @throws ParsingException 
	 */
	protected void attemptToAdd(RuntimeDefinition appConfigDef, List<PropertyValue> values, 
			List<LoaderProblem> loaderProblems, String key, String strValue) {
		
		key = TextUtil.trimToNull(key);
		
		if (key != null) {
			
			Property prop = appConfigDef.getProperty(key);

			if (prop != null) {
				
				PropertyValue pv = createValue(appConfigDef, prop, strValue);
				
				if (pv != null) {
					PropertyValue dup = findDuplicateProperty(pv, values);

					if (dup == null) {
						values.add(pv);
					} else {
						loaderProblems.add(new DuplicatePropertyLoaderProblem(
							this, appConfigDef.getGroupForProperty(prop), prop));
					}
				}
				
			} else {
				loaderProblems.add(new UnknownPropertyLoaderProblem(this, key));
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
	
	protected <T> PropertyValue createValue(RuntimeDefinition appConfigDef, Property<T> prop, String untrimmedString) {
		
		ArrayList<ValueProblem> issues = new ArrayList(0);
		T value = null;
		
		try {
			
			String trimmed = prop.getTrimmer().trim(untrimmedString);
			
			if (trimmed != null || prop.getPropertyType().isFlag()) {
			
				value = prop.getValueType().convert(trimmed);

				for (Validator<T> v : prop.getValidators()) {
					if (! v.isValid(value)) {
						issues.add(new ValueProblem.InvalidValueProblem(
								this, appConfigDef.getGroupForProperty(prop), prop, value, v));
					}
				}
			} else {
				return null;	//No value to create
			}
		} catch (ParsingException ex) {
			issues.add(new ValueProblem.StringConversionValueProblem(
					this, appConfigDef.getGroupForProperty(prop), prop, untrimmedString));
		}
		

		
		return new PropertyValue(prop, value, issues);
	}
	
}
