package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.Name;
import org.yarnandtail.andhow.api.ValueType;
import org.yarnandtail.andhow.api.ValueMap;
import org.yarnandtail.andhow.api.Validator;
import org.yarnandtail.andhow.api.Trimmer;
import org.yarnandtail.andhow.api.PropertyType;
import org.yarnandtail.andhow.api.Property;
import java.util.Collections;
import java.util.List;
import org.yarnandtail.andhow.*;

/**
 *
 * @author eeverman
 */
public abstract class PropertyBase<T> implements Property<T> {
	
	private final PropertyType paramType;
	private final ValueType<T> valueType;
	private final Trimmer trimmer;
	private final T defaultValue;
	private final boolean required;
	private final String shortDesc;
	private final List<Validator<T>> validators;
	private final List<Name> aliases;
	private final String helpText;
	
	public PropertyBase(
			T defaultValue, boolean required, String shortDesc, List<Validator<T>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<T> valueType, Trimmer trimmer,
			String helpText) {
				
		//Clean all values to be non-null
		this.paramType = paramType;
		this.valueType = valueType;
		this.trimmer = trimmer;
		this.defaultValue = defaultValue;
		this.required = required;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.validators = (validators != null)?Collections.unmodifiableList(validators) : Collections.emptyList();
		this.aliases = (aliases != null)?Collections.unmodifiableList(aliases) : Collections.emptyList();
		this.helpText = (helpText != null)?helpText:"";
		
	}
	
	@Override
	public PropertyType getPropertyType() {
		return paramType;
	}
	
	@Override
	public ValueType<T> getValueType() {
		return valueType;
	}

	@Override
	public Trimmer getTrimmer() {
		return trimmer;
	}
	
	@Override
	public String getShortDescription() {
		return shortDesc;
	}
	
	@Override
	public List<Validator<T>> getValidators() {
		return validators;
	}
	
	@Override
	public List<Name> getRequestedAliases() {
		return aliases;
	}

	@Override
	public String getHelpText() {
		return helpText;
	}
	
	@Override
	public boolean isRequired() {
		return required;
	}
	
	@Override
	public T getValue(ValueMap values) {
		T v = getExplicitValue(values);
		if (v != null) {
			return v;
		} else {
			return getDefaultValue();
		}
	}
	
	@Override
	public final T getValue() {
		return getValue(AndHow.instance());
	}
	
	@Override
	public T getExplicitValue(ValueMap values) {
		Object v = values.getExplicitValue(this);
		return valueType.cast(v);
	}
	
	@Override
	public final T getExplicitValue() {
		return getExplicitValue(AndHow.instance());
	}
	
	@Override
	public T getDefaultValue() {
		return defaultValue;
	}
	
}
