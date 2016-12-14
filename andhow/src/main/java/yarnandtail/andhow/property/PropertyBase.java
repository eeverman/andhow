package yarnandtail.andhow.property;

import yarnandtail.andhow.ValueType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import yarnandtail.andhow.AndHow;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.ValueMap;
import yarnandtail.andhow.Property;

/**
 *
 * @author eeverman
 */
public abstract class PropertyBase<T> implements Property<T> {
	
	private final PropertyType paramType;
	private final ValueType<T> valueType;
	private final T defaultValue;
	private final boolean required;
	private final String shortDesc;
	private final List<Validator<T>> validators;
	private final String helpText;
	private final List<String> alias;
	
	public PropertyBase(
			T defaultValue, boolean required, String shortDesc, List<Validator<T>> validators,
			PropertyType paramType, ValueType<T> valueType,
			String helpText, String[] aliases) {
		
		List<String> aliasList;
		if (aliases != null && aliases.length > 0) {
			aliasList = Arrays.stream(aliases).map(s -> TextUtil.trimToNull(s)).
					filter(s -> s != null).collect(Collectors.toList());
			aliasList = Collections.unmodifiableList(aliasList);
		} else {
			aliasList = EMPTY_STRING_LIST;
		}
				
		//Clean all values to be non-null
		this.paramType = paramType;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.required = required;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.validators = (validators != null)?Collections.unmodifiableList(validators) : Collections.emptyList();
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		
	}
	
	public PropertyBase(
			T defaultValue, boolean required, String shortDesc, List<Validator<T>> validators,
			PropertyType paramType, ValueType<T> valueType,
			String helpText, String explicitName) {
		
		List<String> aliasList;
		explicitName = TextUtil.trimToNull(explicitName);
		if (explicitName != null) {
			aliasList = Collections.unmodifiableList(Arrays.asList(new String[] {explicitName}));
		} else {
			aliasList = EMPTY_STRING_LIST;
		}
	
				
		//Clean all values to be non-null
		this.paramType = paramType;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.required = required;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.validators = (validators != null)?Collections.unmodifiableList(validators) : Collections.emptyList();
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		
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
	public String getShortDescription() {
		return shortDesc;
	}
	
	@Override
	public List<Validator<T>> getValidators() {
		return validators;
	}

	@Override
	public String getHelpText() {
		return helpText;
	}

	@Override
	public List<String> getBaseAliases() {
		return alias;
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
