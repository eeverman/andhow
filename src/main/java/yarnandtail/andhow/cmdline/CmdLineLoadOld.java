package yarnandtail.andhow.cmdline;

import yarnandtail.andhow.propfile.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigParamDefinition;
import yarnandtail.andhow.ConfigParamType;
import yarnandtail.andhow.ParamType;
import static yarnandtail.andhow.ConfigPoint.EMPTY_ENUM_LIST;
import static yarnandtail.andhow.ConfigPoint.EMPTY_STRING_LIST;
import yarnandtail.andhow.ConfigPoint;

/**
 * MAY NEED TO ADD A 2ND INTERFACE SPECIFICALLLY FOR CONFIG PARAMS SO THAT
 * NEW INSTANCES CAN BE MADE W/ NEW DEFAULTS.  IF I WANT DIFFERENT DEFAULT
 * BEHAVIOUR, HOW COULD I DO IT IF I RELY ON THE IDENTITY OF THESE INDIVIDUAL
 * ENUMS?
 * 
 * INSTEAD, THE CONFIG INTERFACE WOULD ADD A METHOD LIKE:
 * configTypeenum getConfigType() which would indicate its type.
 * @author eeverman
 */
public enum CmdLineLoadOld implements ConfigParamDefinition<CmdLineLoadOld> {
	
	//All full names and alias should be lower case.
	//Make sure aliases are unique
	PLACEHOLDER("Core configuration loading params", "A set of configuration parameters for basic configuration of loading application configuration"),

	/**
	 * Classpath (the package) of the properties file.
	 * File system paths (PROPERTIES_FILE_SYSTEM_PATH) are searched before
	 * the PROPERTIES_FILE_CLASSLOADER_PATH to allow local configuration to
	 * override configuration internal to the deployed artifact.
	 * If not specified, the default package is assumed.
	 */
	//PROPERTIES_FILE_CLASSLOADER_PATH(false, false, true),
	HELP_FLAG("help", ParamType.CONF_FLAG_OPTIONAL, ConfigParamType.HELP, null, "Print command help", null),
	VERBOSE_CONFIG_FLAG("verboseConfig", ParamType.CONF_FLAG_OPTIONAL, ConfigParamType.VERBOSE_CONFIG, null, "Print command help", null, new String[] {"verbConf"});

	
	
	private final String fullName;
	private final ParamType paramType;
	private final ConfigParamType configParamType;
	private final Object defaultValue;
	private final String shortDesc;
	private final String helpText;
	private final List<String> alias;
	private final List<Enum> allowedValueEnum;

	
	private CmdLineLoadOld(String fullName, ParamType paramType, ConfigParamType configParamType, Object defaultValue,
			String shortDesc, String helpText) {
		
		this(fullName, paramType, configParamType, defaultValue, shortDesc, helpText, null, null);
	}
	
	private CmdLineLoadOld(String fullName, ParamType paramType, ConfigParamType configParamType, Object defaultValue,
			String shortDesc, String helpText, String[] aliases) {
		
		this(fullName, paramType, configParamType, defaultValue, shortDesc, helpText, aliases, null);
	}
	
	private CmdLineLoadOld(String fullName, ParamType paramType, ConfigParamType configParamType, Object defaultValue,
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
		this.configParamType = configParamType;
		this.defaultValue = defaultValue;
		this.shortDesc = (shortDesc != null)?shortDesc:"";
		this.helpText = (helpText != null)?helpText:"";
		this.alias = aliasList;
		this.allowedValueEnum = allowedValueEnumList;

		
		if (this.fullName == null) {
			throw new RuntimeException("All parameters must have a non-empty fullName");
		}
	}
	
	/**
	 * Constructor only for a 'fake' placeholder value.
	 */
	private CmdLineLoadOld(String entireSetName, String entireSetDescription) {
		
		this.paramType = ParamType.NON_PARAM;
		this.configParamType = ConfigParamType.NONE;
		this.defaultValue = null;
		this.fullName = entireSetName;
		this.alias = EMPTY_STRING_LIST;
		this.allowedValueEnum = EMPTY_ENUM_LIST;
		this.shortDesc = entireSetDescription;
		this.helpText = "";
	}
	
	@Override
	public ParamType getParamType() {
		return paramType;
	}
	
	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public String getEntireSetName() {
		return StringUtils.trimToEmpty(PLACEHOLDER.fullName);
	}

	@Override
	public String getEntireSetDescription() {
		return StringUtils.trimToEmpty(PLACEHOLDER.shortDesc);
	}
	
	@Override
	public List<CmdLineLoadOld> getAllConfigParamsIncludingNonRealOnes() {
		return Arrays.asList(values());
	}
	
	@Override
	public List<Enum> getPossibleValueEnums() {
		return allowedValueEnum;
	}

	@Override
	public String getExplicitName() {
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
	public ConfigParamType getConfigParamType() {
		return configParamType;
	}

}
