package yarnandtail.andhow.point;

import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.valuetype.FlagType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class FlagConfigPoint extends ConfigPointBase<Boolean> {
	
	public FlagConfigPoint() {
		this(null, false, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, EMPTY_STRING_ARRAY);
	}
	
	public FlagConfigPoint(Boolean defaultValue, boolean required) {
		this(defaultValue, required, null, ConfigPointType.SINGLE_NAME_VALUE, FlagType.instance(), false, null, EMPTY_STRING_ARRAY);
	}
	
	public FlagConfigPoint(
			Boolean defaultValue, boolean required, String shortDesc,
			ConfigPointType paramType, ValueType<Boolean> valueType, boolean priv,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, paramType, valueType, priv, helpText, aliases);
	}

	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}

}
