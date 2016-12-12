package yarnandtail.andhow.property;

import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valid.StringValidator;
import yarnandtail.andhow.valuetype.ValueType;
import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StringProp extends PropertyBase<String> {
	
	public StringProp() {
		this(null, false, "", null, PropertyType.SINGLE_NAME_VALUE, StringType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public StringProp(String defaultValue, boolean required) {
		this(defaultValue, required, "", null, PropertyType.SINGLE_NAME_VALUE, StringType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public StringProp(
			String defaultValue, boolean required, String shortDesc, List<Validator<String>> validators,
			PropertyType paramType, ValueType<String> valueType,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, helpText, aliases);
	}
	
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
	public static StringBuilder builder() {
		return new StringBuilder();
	}
	
	
	public static class StringBuilder extends PropertyBuilderBase<StringBuilder, StringProp, String> {

		
		public StringBuilder() {
			instance = this;
			setValueType(StringType.instance());
		}

		@Override
		public StringProp build() {

			return new StringProp(defaultValue, required, shortDesc, validators,
				paramType, valueType,
				helpText, aliases.toArray(new String[aliases.size()]));
		}
		
		public StringBuilder mustMatchRegex(String regex) {
			this.addValidation(new StringValidator.Regex(regex));
			return this;
		}
		
		public StringBuilder mustStartWith(String prefix) {
			this.addValidation(new StringValidator.StartsWith(prefix, false));
			return this;
		}
		
		public StringBuilder mustStartWithIgnoreCase(String prefix) {
			this.addValidation(new StringValidator.StartsWith(prefix, true));
			return this;
		}
		
		public StringBuilder mustEndWith(String sufix) {
			this.addValidation(new StringValidator.EndsWith(sufix, false));
			return this;
		}
		
		public StringBuilder mustEndWithIgnoreCase(String sufix) {
			this.addValidation(new StringValidator.EndsWith(sufix, true));
			return this;
		}

	}
	
}
