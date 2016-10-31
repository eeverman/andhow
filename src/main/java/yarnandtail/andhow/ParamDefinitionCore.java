package yarnandtail.andhow;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static yarnandtail.andhow.ParamDefinition.EMPTY_ENUM_LIST;
import static yarnandtail.andhow.ParamDefinition.EMPTY_STRING_LIST;

/**
 *
 * @author eeverman
 */
public class ParamDefinitionCore implements ParamDefinitionInterface {

	private final Class<? extends ParamDefinition> enumClass;
	private final String explicitName;
	private final ParamType paramType;
	private final Object defaultValue;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	private final List<Enum> allowedValueEnum;
	
	public ParamDefinitionCore(Class<? extends ParamDefinition> enumClass, String explicitName, ParamType paramType, Object defaultValue,
			String shortDesc, String helpText, String[] aliases,
			Enum[] allowedValues) {
		
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
		this.defaultValue = defaultValue;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		this.allowedValueEnum = allowedValueEnumList;

		
		if (this.enumClass == null) {
			throw new RuntimeException("All parameters must be constructed by passing the enclosing Enum class in.");
		}
	}
	
	@Override
	public Class<? extends ParamDefinition> getEnumClass() {
		return enumClass;
	}
	
	@Override
	public String getEntireSetName() {
		if (enumClass.getAnnotation(ParamDefinitionAnnotation.class) != null) {
			return StringUtils.trimToEmpty(enumClass.getAnnotation(ParamDefinitionAnnotation.class).groupName());
		} else {
			return "";
		}
	}

	@Override
	public String getEntireSetDescription() {
		if (enumClass.getAnnotation(ParamDefinitionAnnotation.class) != null) {
			return StringUtils.trimToEmpty(enumClass.getAnnotation(ParamDefinitionAnnotation.class).groupDescription());
		} else {
			return "";
		}
	}
	
	@Override
	public ParamType getParamType() {
		return paramType;
	}

	@Override
	public String getExplicitName() {
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
	public List<String> getAlias() {
		return alias;
	}

	@Override
	public List<Enum> getPossibleValueEnums() {
		return allowedValueEnum;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
	
}
