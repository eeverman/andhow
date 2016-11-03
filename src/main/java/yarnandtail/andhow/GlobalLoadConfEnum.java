package yarnandtail.andhow;

import yarnandtail.andhow.valuetype.FlagType;

/**
 * @author eeverman
 */
@ConfigGroupDescription (
	groupName = "Global AndHow configuration parameters",
	groupDescription = "Configures the most basic aspects of how configuration is done, such as " +
			"printing help info (like this), or turning on detailed logging during configuration loading."
)
public enum GlobalLoadConfEnum implements GlobalLoadConfInt<GlobalLoadConfEnum> {	

	HELP("help", ConfigPointType.FLAG, null, "Print command help", null, null, null),
	VERBOSE_CONFIG("verboseConfig", ConfigPointType.FLAG, null, "Print detailed info during configuration", null, new String[] {"verbConf"}, null),
	VERY_VERBOSE_CONFIG("veryVerboseConfig", ConfigPointType.FLAG, null, "Print VERY detailed info during configuration", null, new String[] {"veryVerbConf"}, null);

	
	
	private final ConfigPointHelper core;

	
	private GlobalLoadConfEnum(String fullName, ConfigPointType paramType, Object defaultValue,
			String shortDesc, String helpText, String[] aliases,
			Enum[] allowedValues) {
		
		core = new ConfigPointHelper(GlobalLoadConfEnum.class, fullName, paramType, FlagType.get(), defaultValue,
			shortDesc, helpText, aliases, allowedValues, false);
	}

	@Override
	public ConfigPointCommon getCore() {
		return core;
	}
	
	@Override
	public boolean isHelp() {
		return this.equals(HELP);
	}

	@Override
	public boolean isVerboseConf() {
		return this.equals(VERBOSE_CONFIG);
	}

	@Override
	public boolean isVeryVerboseConf() {
		return this.equals(VERY_VERBOSE_CONFIG);
	}

}
