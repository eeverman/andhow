package yarnandtail.andhow;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static yarnandtail.andhow.ConfigPoint.EMPTY_ENUM_LIST;
import static yarnandtail.andhow.ConfigPoint.EMPTY_STRING_LIST;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class ConfigPointHelper implements ConfigPointCommon {

	private final Class<? extends ConfigPoint> enumClass;
	private final String explicitName;
	private final ConfigPointType paramType;
	private final ValueType valueType;
	private final Object defaultValue;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	private final List<Enum> allowedValueEnum;
	private final boolean priv;
	
	public ConfigPointHelper(Class<? extends ConfigPoint> enumClass, String explicitName,
			ConfigPointType paramType, ValueType valueType,
			Object defaultValue, String shortDesc, String helpText, String[] aliases,
			Enum[] allowedValues, boolean priv) {
		
		List<String> aliasList;
		if (aliases != null && aliases.length > 0) {
			aliasList = Collections.unmodifiableList(Arrays.asList(aliases));
		} else {
			aliasList = EMPTY_STRING_LIST;
		}
		
		
		List<Enum> allowedValueEnumList;
		if (allowedValues != null && allowedValues.length > 0) {
			allowedValueEnumList = Collections.unmodifiableList(Arrays.asList(allowedValues));
		} else {
			allowedValueEnumList = EMPTY_ENUM_LIST;
		}
	
				
		//Clean all values to be non-null
		this.enumClass = enumClass;
		this.explicitName = StringUtils.trimToNull(explicitName);
		this.paramType = paramType;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		this.allowedValueEnum = allowedValueEnumList;
		this.priv = priv;

		
		if (this.enumClass == null) {
			throw new RuntimeException("All parameters must be constructed by passing the enclosing Enum class in.");
		}
	}
	
	@Override
	public Class<? extends ConfigPoint> getEnumClass() {
		return enumClass;
	}
	
	@Override
	public String getGroupDescription() {
		if (enumClass.getAnnotation(ConfigGroupDescription.class) != null) {
			return StringUtils.trimToEmpty(enumClass.getAnnotation(ConfigGroupDescription.class).groupName());
		} else {
			return "";
		}
	}

	@Override
	public String getEntireSetDescription() {
		if (enumClass.getAnnotation(ConfigGroupDescription.class) != null) {
			return StringUtils.trimToEmpty(enumClass.getAnnotation(ConfigGroupDescription.class).groupDescription());
		} else {
			return "";
		}
	}
	
	@Override
	public ConfigPointType getPointType() {
		return paramType;
	}
	
	@Override
	public ValueType getValueType() {
		return valueType;
	}

	@Override
	public String getExplicitBaseName() {
		return explicitName;
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
	public List<Enum> getPossibleValueEnums() {
		return allowedValueEnum;
	}

	@Override
	public Object getBaseDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isPrivate() {
		return priv;
	}
	
}
