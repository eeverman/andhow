package org.yarnandtail.andhow.property;

import java.util.Collections;
import java.util.List;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.*;

/**
 * Base property implementation that handles most of state information and common methods.
 * 
 * @author eeverman
 */
public abstract class PropertyBase<T> implements Property<T> {
	
	private final PropertyType paramType;
	private final ValueType<T> valueType;
	private final Trimmer trimmer;
	private final T defValue;
	private final boolean nonNull;
	private final String shortDesc;
	private final List<Validator<T>> validators;
	private final List<Name> aliases;
	private final String helpText;
	
	public PropertyBase(
			T defaultValue, boolean nonNull, String shortDesc, List<Validator<T>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<T> valueType, Trimmer trimmer,
			String helpText) {
				
		//Clean all values to be non-null
		this.paramType = paramType;
		this.valueType = valueType;
		this.trimmer = trimmer;
		this.defValue = defaultValue;
		this.nonNull = nonNull;
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
	public String getDescription() {
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
	public boolean isNonNullRequired() {
		return nonNull;
	}
	
	@Override
	public T getValue() {
		
		T v = getExplicitValue();
		if (v != null) {
			return v;
		} else {
			return getDefaultValue();
		}
	
	}
	
	@Override
	public final T getExplicitValue() {
		Object v = AndHow.instance().getExplicitValue(this);
		return valueType.cast(v);
	}
	
	@Override
	public T getDefaultValue() {
		return defValue;
	}
	
}
