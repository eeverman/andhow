package yarnandtail.andhow;

import yarnandtail.andhow.valuetype.ValueType;
import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StringConfigPoint extends ConfigPointBase<String> {
	
	public StringConfigPoint() {
		this(null, "", ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), false, null, null,  null);
	}
	
	public StringConfigPoint(String defaultValue, String shortDesc, String explicitName) {
		this(defaultValue, shortDesc, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), false, explicitName, null,  null);
	}
	
	public StringConfigPoint(String defaultValue, String shortDesc, 
			ConfigPointType paramType, ValueType<String> valueType, boolean priv,
			String explicitName) {
		this(defaultValue, shortDesc, paramType, valueType, priv, explicitName, null, null);
	}
	
	public StringConfigPoint(
			String defaultValue, String shortDesc, 
			ConfigPointType paramType, ValueType<String> valueType, boolean priv,
			String explicitName, String helpText, String[] aliases) {
		
		super(defaultValue, shortDesc, paramType, valueType, priv, explicitName, helpText, aliases);
	}
	
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
