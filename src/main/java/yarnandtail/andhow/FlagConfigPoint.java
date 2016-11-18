package yarnandtail.andhow;

import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class FlagConfigPoint extends ConfigPointBase<Boolean> {
	
	public FlagConfigPoint() {
		this(null, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, null, null);
	}
	
	public FlagConfigPoint(Boolean defaultValue, String shortDesc, String explicitName) {
		this(defaultValue, shortDesc, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, explicitName, null, null);
	}
	
	public FlagConfigPoint(
			Boolean defaultValue, String shortDesc,
			ConfigPointType paramType, ValueType<Boolean> valueType, boolean priv,
			String explicitName, String helpText, String[] aliases) {
		
		super(defaultValue, shortDesc, paramType, valueType, priv, explicitName, helpText, aliases);
	}

	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}

}
