package yarnandtail.andhow.propfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigParamDefinition;
import yarnandtail.andhow.ConfigParamType;
import yarnandtail.andhow.ParamDefinition;
import yarnandtail.andhow.ParamType;
import static yarnandtail.andhow.ParamDefinition.EMPTY_ENUM_LIST;
import static yarnandtail.andhow.ParamDefinition.EMPTY_STRING_LIST;

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
public enum PropFileLoadConfig implements ConfigParamDefinition<PropFileLoadConfig> {
	
	//All full names and alias should be lower case.
	//Make sure aliases are unique
	PLACEHOLDER("Core configuration loading params", "A set of configuration parameters for basic configuration of loading application configuration"),
	PROPERTIES_FILE_NAME("propFileName", ParamType.CONF_SINGLE_NAME_VALUE_OPTIONAL, 
		ConfigParamType.PROPERTIES_FILE_NAME, "config.properties",
		"Name of a properties file to load", 
		"Sets the name of the properties file to attempt to load to find further properties.  "
		+ "Any attempt to find a properties file can be turned off via SKIP_PROPERTIES_FROM_FILE_SYSTEM.", new String[] {"prop_file_name", "prop_filename"}),
	/** Name of a properties file to load if the onfig_file_name cannot be
	 * found.  This will allow default properties (possibly bundled w/ the deployment)
	 * to be used only if all other search paths have been exhausted.
	 */
	PROPERTIES_DEFAULT_FILE_NAME("defaultPropFileName", ParamType.CONF_SINGLE_NAME_VALUE_OPTIONAL, 
		ConfigParamType.PROPERTIES_DEFAULT_FILE_NAME, "default_config.properties",
		"Name of a properties file to load if the prop_file_name cannot be found.", 
		"Sets the name of a default properties file to attempt to load to find further properties.  " +
			"This allows default properties (possibly bundled w/ the deployment) to be used only if all other search paths have been exhausted." +
			"Properties file loading can be turned off comletely via SKIP_PROPERTIES_FROM_FILE_SYSTEM.", new String[] {"default_prop_filename", "def_prop_filename"}),
	PROPERTIES_FILE_SYSTEM_PATH("propFilePath", ParamType.CONF_MULTI_NAME_VALUE_OPTIONAL,
		ConfigParamType.PROPERTIES_FILE_SYSTEM_PATH, ",~/",
		"File system directories to search for the properties file in.", 
		"Sets a list of file system paths to search for properties file in.  Paths are expanded as follows:  " +
			"Paths begining with '~/' have '~/' replaced w/ the user's home directory.  " +
			"Empty paths are replaced w/ the directoy containing the current executable, which would be the directory of the jar file when running an executable jar.  " +
			"Properties file loading can be turned off comletely via SKIP_PROPERTIES_FROM_FILE_SYSTEM.", new String[] {"prop_filepath"}),
	SKIP_PROPERTIES_FROM_FILE_SYSTEM("skipFileSystemPropFile", ParamType.CONF_FLAG_OPTIONAL,
		ConfigParamType.SKIP_PROPERTIES_FROM_FILE_SYSTEM, null,
		"Turns off loading of a properties file from the file system.", 
		null, new String[] {"skip_filesystem_props"}),

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

	
	private PropFileLoadConfig(String fullName, ParamType paramType, ConfigParamType configParamType, Object defaultValue,
			String shortDesc, String helpText) {
		
		this(fullName, paramType, configParamType, defaultValue, shortDesc, helpText, null, null);
	}
	
	private PropFileLoadConfig(String fullName, ParamType paramType, ConfigParamType configParamType, Object defaultValue,
			String shortDesc, String helpText, String[] aliases) {
		
		this(fullName, paramType, configParamType, defaultValue, shortDesc, helpText, aliases, null);
	}
	
	private PropFileLoadConfig(String fullName, ParamType paramType, ConfigParamType configParamType, Object defaultValue,
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
	private PropFileLoadConfig(String entireSetName, String entireSetDescription) {
		
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
	public List<PropFileLoadConfig> getAllConfigParamsIncludingNonRealOnes() {
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
