package yarnandtail.andhow.point;

import java.util.List;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valuetype.ValueType;
import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StringConfigPoint extends ConfigPointBase<String> {
	
	public StringConfigPoint() {
		this(null, false, "", null, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public StringConfigPoint(String defaultValue, boolean required) {
		this(defaultValue, required, "", null, ConfigPointType.SINGLE_NAME_VALUE, StringType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public StringConfigPoint(
			String defaultValue, boolean required, String shortDesc, List<Validator<String>> validators,
			ConfigPointType paramType, ValueType<String> valueType,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, helpText, aliases);
	}
	
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
