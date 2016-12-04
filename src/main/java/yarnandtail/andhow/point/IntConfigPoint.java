package yarnandtail.andhow.point;

import java.util.List;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valuetype.IntType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class IntConfigPoint extends ConfigPointBase<Integer> {
	
	public IntConfigPoint() {
		this(null, false, null, null, ConfigPointType.SINGLE_NAME_VALUE, IntType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public IntConfigPoint(Integer defaultValue, boolean required) {
		this(defaultValue, required, null, null, ConfigPointType.SINGLE_NAME_VALUE, IntType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public IntConfigPoint(
			Integer defaultValue, boolean required, String shortDesc, List<Validator<Integer>> validators,
			ConfigPointType paramType, ValueType<Integer> valueType,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, helpText, aliases);
	}

	@Override
	public Integer cast(Object o) throws RuntimeException {
		return (Integer)o;
	}

}
