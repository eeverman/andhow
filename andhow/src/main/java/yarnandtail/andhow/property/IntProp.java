package yarnandtail.andhow.property;

import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valid.IntValidator;
import yarnandtail.andhow.valuetype.IntType;
import yarnandtail.andhow.valuetype.ValueType;

/**
 *
 * @author eeverman
 */
public class IntProp extends PropertyBase<Integer> {
	
	public IntProp() {
		this(null, false, null, null, PropertyType.SINGLE_NAME_VALUE, IntType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public IntProp(Integer defaultValue, boolean required) {
		this(defaultValue, required, null, null, PropertyType.SINGLE_NAME_VALUE, IntType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public IntProp(
			Integer defaultValue, boolean required, String shortDesc, List<Validator<Integer>> validators,
			PropertyType paramType, ValueType<Integer> valueType,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, helpText, aliases);
	}

	@Override
	public Integer cast(Object o) throws RuntimeException {
		return (Integer)o;
	}
	
	public static IntBuilder builder() {
		return new IntBuilder();
	}
	
	public static class IntBuilder extends PropertyBuilderBase<IntBuilder, IntProp, Integer> {

		public IntBuilder() {
			instance = this;
			setValueType(IntType.instance());
		}

		@Override
		public IntProp build() {

			return new IntProp(defaultValue, required, shortDesc, validators,
				paramType, valueType,
				helpText, aliases.toArray(new String[aliases.size()]));

		}
		
		public IntBuilder mustBeGreaterThan(int reference) {
			addValidation(new IntValidator.GreaterThan(reference));
			return instance;
		}
		
		public IntBuilder mustBeGreaterThanOrEqualTo(int reference) {
			addValidation(new IntValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}
		
		public IntBuilder mustBeLessThan(int reference) {
			addValidation(new IntValidator.LessThan(reference));
			return instance;
		}
		
		public IntBuilder mustBeLessThanOrEqualTo(int reference) {
			addValidation(new IntValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
