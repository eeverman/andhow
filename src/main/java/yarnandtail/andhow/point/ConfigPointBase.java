package yarnandtail.andhow.point;

import yarnandtail.andhow.valuetype.ValueType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.AppConfig;
import yarnandtail.andhow.AppConfigValues;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.load.ParsingException;

/**
 *
 * @author eeverman
 */
public abstract class ConfigPointBase<T> implements ConfigPoint<T> {
	
	private final ConfigPointType paramType;
	private final ValueType valueType;
	private final T defaultValue;
	private final boolean required;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	
	public ConfigPointBase(
			T defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<T> valueType,
			String helpText, String[] aliases) {
		
		List<String> aliasList;
		if (aliases != null && aliases.length > 0) {
			aliasList = Arrays.stream(aliases).map(s -> StringUtils.trimToNull(s)).
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
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		
	}
	
	public ConfigPointBase(
			T defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<T> valueType,
			String helpText, String explicitName) {
		
		List<String> aliasList;
		explicitName = StringUtils.trimToNull(explicitName);
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
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		
	}
	
	@Override
	public ConfigPointType getPointType() {
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
	public T getValue(AppConfigValues values) {
		T v = getExplicitValue(values);
		if (v != null) {
			return v;
		} else {
			return getDefaultValue();
		}
	}
	
	@Override
	public final T getValue() {
		return getValue(AppConfig.instance());
	}
	
	@Override
	public T getExplicitValue(AppConfigValues values) {
		Object v = values.getValue(this);
		return cast(v);
	}
	
	@Override
	public final T getExplicitValue() {
		return getExplicitValue(AppConfig.instance());
	}
	
	@Override
	public T convertString(String str) throws ParsingException {
		return getValueType().convert(str);
	}
	
	@Override
	public T getDefaultValue() {
		return defaultValue;
	}
	
}
