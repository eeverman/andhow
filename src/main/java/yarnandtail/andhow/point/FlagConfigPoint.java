package yarnandtail.andhow.point;

import yarnandtail.andhow.ConfigPointBase;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class FlagConfigPoint extends ConfigPointBase<Boolean> {
	
	public FlagConfigPoint() {
		this(null, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, (String)null);
	}
	
	public FlagConfigPoint(Boolean defaultValue, String shortDesc, String explicitName) {
		this(defaultValue, shortDesc, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, explicitName);
	}
	
	public FlagConfigPoint(
			Boolean defaultValue, String shortDesc,
			ConfigPointType paramType, ValueType<Boolean> valueType, boolean priv,
			String helpText, String[] aliases) {
		
		super(defaultValue, shortDesc, paramType, valueType, priv, helpText, aliases);
	}
	
	public FlagConfigPoint(
			Boolean defaultValue, String shortDesc,
			ConfigPointType paramType, ValueType<Boolean> valueType, boolean priv,
			String helpText, String explicitName) {
		
		super(defaultValue, shortDesc, paramType, valueType, priv, helpText, explicitName);
	}

	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}

}
