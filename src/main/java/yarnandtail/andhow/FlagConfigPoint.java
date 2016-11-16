package yarnandtail.andhow;

import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.valuetype.ValueType;

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
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}

}
