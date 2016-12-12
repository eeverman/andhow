package yarnandtail.andhow.point;

import java.util.List;
import yarnandtail.andhow.ConfigPointType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valid.StringValidator;
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
	
	public static StringPointBuilder builder() {
		return new StringPointBuilder();
	}
	
	
	public static class StringPointBuilder extends ConfigPointBuilder<StringPointBuilder, StringConfigPoint, String> {

		
		public StringPointBuilder() {
			instance = this;
			setValueType(StringType.instance());
		}

		@Override
		public StringConfigPoint build() {

			return new StringConfigPoint(defaultValue, required, shortDesc, validators,
				paramType, valueType,
				helpText, aliases.toArray(new String[aliases.size()]));
		}
		
		public StringPointBuilder mustMatchRegex(String regex) {
			this.addValidation(new StringValidator.Regex(regex));
			return this;
		}
		
		public StringPointBuilder mustStartWith(String prefix) {
			this.addValidation(new StringValidator.StartsWith(prefix, false));
			return this;
		}
		
		public StringPointBuilder mustStartWithIgnoreCase(String prefix) {
			this.addValidation(new StringValidator.StartsWith(prefix, true));
			return this;
		}
		
		public StringPointBuilder mustEndWith(String sufix) {
			this.addValidation(new StringValidator.EndsWith(sufix, false));
			return this;
		}
		
		public StringPointBuilder mustEndWithIgnoreCase(String sufix) {
			this.addValidation(new StringValidator.EndsWith(sufix, true));
			return this;
		}

	}
	
}
