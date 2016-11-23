package yarnandtail.andhow.point;

import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.valuetype.ValueType;
import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StringConfigPoint extends ConfigPointBase<String> {
	
	public StringConfigPoint() {
		this(null, false, "", ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), false, null, EMPTY_STRING_ARRAY);
	}
	
	public StringConfigPoint(String defaultValue, boolean required) {
		this(defaultValue, required, "", ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), false, null, EMPTY_STRING_ARRAY);
	}
	
	public StringConfigPoint(
			String defaultValue, boolean required, String shortDesc, 
			ConfigPointType paramType, ValueType<String> valueType, boolean priv,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, paramType, valueType, priv, helpText, aliases);
	}
	
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
