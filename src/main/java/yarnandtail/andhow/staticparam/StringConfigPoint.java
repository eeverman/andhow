package yarnandtail.andhow.staticparam;

import yarnandtail.andhow.ConfigPointType;

/**
 *
 * @author eeverman
 */
public class StringConfigPoint extends ConfigPointBase<String> {
	
	public StringConfigPoint(String name) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), null, "", "", null, false);
	}
	
	public StringConfigPoint(String name, String defaultValue) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), defaultValue, "", "", null, false);
	}
	
	public StringConfigPoint(String explicitName,
			ConfigPointType paramType, ValueType<String> valueType,
			String defaultValue, String shortDesc, String helpText, String[] aliases,
			boolean priv) {
		
		super(explicitName, paramType, valueType, defaultValue, shortDesc, helpText, aliases, priv);

	}
	
	public String getValue() {
		String s = getExplicitValue();
		if (s != null) {
			return s;
		} else {
			return getDefaultValue();
		}
	}
	
	@Override
	public String getExplicitValue() {
		return AppConfig.instance().getUserString(this);
	}
	
	@Override
	public String getDefaultValue() {
		return getBaseDefault();
	}

}
