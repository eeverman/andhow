package yarnandtail.andhow.property;

import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valid.StringValidator;
import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StrProp extends PropertyBase<String> {
	
	public StrProp() {
		this(null, false, "", null, PropertyType.SINGLE_NAME_VALUE, StringType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public StrProp(String defaultValue, boolean required) {
		this(defaultValue, required, "", null, PropertyType.SINGLE_NAME_VALUE, StringType.instance(), null, EMPTY_STRING_ARRAY);
	}
	
	public StrProp(
			String defaultValue, boolean required, String shortDesc, List<Validator<String>> validators,
			PropertyType paramType, ValueType<String> valueType,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, helpText, aliases);
	}
	
	public static StringBuilder builder() {
		return new StringBuilder();
	}
	
	
	public static class StringBuilder extends PropertyBuilderBase<StringBuilder, StrProp, String> {

		
		public StringBuilder() {
			instance = this;
			valueType(StringType.instance());
		}

		@Override
		public StrProp build() {

			return new StrProp(_defaultValue, _required, _shortDesc, _validators,
				_paramType, _valueType,
				_helpText, _aliases.toArray(new String[_aliases.size()]));
		}
		
		public StringBuilder mustMatchRegex(String regex) {
			this.validation(new StringValidator.Regex(regex));
			return this;
		}
		
		public StringBuilder mustStartWith(String prefix) {
			this.validation(new StringValidator.StartsWith(prefix, false));
			return this;
		}
		
		public StringBuilder mustStartWithIgnoreCase(String prefix) {
			this.validation(new StringValidator.StartsWith(prefix, true));
			return this;
		}
		
		public StringBuilder mustEndWith(String sufix) {
			this.validation(new StringValidator.EndsWith(sufix, false));
			return this;
		}
		
		public StringBuilder mustEndWithIgnoreCase(String sufix) {
			this.validation(new StringValidator.EndsWith(sufix, true));
			return this;
		}

	}
	
}
