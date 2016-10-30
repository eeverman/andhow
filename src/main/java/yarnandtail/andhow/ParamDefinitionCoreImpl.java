package yarnandtail.andhow;

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
public class ParamDefinitionCoreImpl implements ParamDefinitionCore {

	private final String fullName;
	private final ParamType paramType;
	private final Object defaultValue;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	private final List<Enum> allowedValueEnum;
	
	private ParamDefinitionCoreImpl(String fullName, ParamType paramType, Object defaultValue,
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
		this.fullName = StringUtils.trimToNull(fullName);
		this.paramType = paramType;
		this.defaultValue = defaultValue;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		this.allowedValueEnum = allowedValueEnumList;

		
		if (this.fullName == null) {
			throw new RuntimeException("All parameters must have a non-empty fullName");
		}
	}
	
	@Override
	public ParamType getParamType() {
		return paramType;
	}

	@Override
	public String getFullName() {
		return fullName;
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
	public List<String> getPossibleValues() {
		List<Enum> allowedValueEnumList = getPossibleValueEnums();
		List<String> domainStrings = new ArrayList(allowedValueEnumList.size());
		for (Enum e : allowedValueEnumList) {
			domainStrings.add(e.toString());
		}
		
		return domainStrings;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public boolean isReal() {
		return paramType.isReal();
	}
	
	@Override
	public boolean isNotReal() {
		return paramType.isNotReal();
	}


	
}
