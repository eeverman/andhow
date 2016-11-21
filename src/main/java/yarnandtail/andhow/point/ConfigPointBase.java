package yarnandtail.andhow.point;

import yarnandtail.andhow.valuetype.ValueType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.AppConfig;
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
	private final boolean priv;
	private final List<String> alias;
	
	public ConfigPointBase(
			T defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<T> valueType, boolean priv,
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
		this.priv = priv;
		
	}
	
	public ConfigPointBase(
			T defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<T> valueType, boolean priv,
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
		this.priv = priv;
		
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
	public boolean isPrivate() {
		return priv;
	}
	
	@Override
	public T getBaseDefault() {
		return defaultValue;
	}

	@Override
	public boolean isRequired() {
		return required;
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
	public T getExplicitValue() {
		Object v = AppConfig.instance().getValue(this);
		return cast(v);
	}
	
	@Override
	public T convertString(String str) throws ParsingException {
		return getValueType().convert(str);
	}
	
	@Override
	public T getDefaultValue() {
		return getBaseDefault();
	}
	
}
