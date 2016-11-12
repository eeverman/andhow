package yarnandtail.andhow.staticparam;

import yarnandtail.andhow.ConfigParamUtil;
import yarnandtail.andhow.ConfigPointType;

/**
 *
 * @author eeverman
 */
public class FlagConfigPoint extends ConfigPointBase<Boolean> {
	
	public FlagConfigPoint(String name) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), null, "", "", null, false);
	}
	
	public FlagConfigPoint(String name, Boolean defaultValue) {
		this(name, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), defaultValue, "", "", null, false);
	}
	
	public FlagConfigPoint(String explicitName,
			ConfigPointType paramType, ValueType<Boolean> valueType,
			Boolean defaultValue, String shortDesc, String helpText, String[] aliases,
			boolean priv) {
		
		super(explicitName, paramType, valueType, defaultValue, shortDesc, helpText, aliases, priv);

	}
	
	@Override
	public Boolean getValue() {
		Boolean b = getExplicitValue();
		if (b == null) b = getDefaultValue();
		return b;
	}
	
	@Override
	public Boolean getExplicitValue() {
		String v = AppConfig.instance().getUserString(this);
		if (v != null) {
			return ConfigParamUtil.toBoolean(v);
		} else {
			return null;
		}
	}
	
	@Override
	public Boolean getDefaultValue() {
		return getBaseDefault();
	}

}
