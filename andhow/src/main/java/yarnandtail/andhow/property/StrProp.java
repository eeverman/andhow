package yarnandtail.andhow.property;

import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valid.StringValidator;
import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.valuetype.QuotedStringType;
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
	
	public static StrBuilder builder() {
		return new StrBuilder();
	}
	
	
	public static class StrBuilder extends PropertyBuilderBase<StrBuilder, StrProp, String> {

		
		public StrBuilder() {
			instance = this;
			valueType(StringType.instance());
		}

		@Override
		public StrProp build() {

			return new StrProp(_defaultValue, _required, _shortDesc, _validators,
				_paramType, _valueType,
				_helpText, _aliases.toArray(new String[_aliases.size()]));
		}
		
		public StrBuilder mustMatchRegex(String regex) {
			this.validation(new StringValidator.Regex(regex));
			return this;
		}
		
		public StrBuilder mustStartWith(String prefix) {
			this.validation(new StringValidator.StartsWith(prefix, false));
			return this;
		}
		
		public StrBuilder mustStartWithIgnoreCase(String prefix) {
			this.validation(new StringValidator.StartsWith(prefix, true));
			return this;
		}
		
		public StrBuilder mustEndWith(String sufix) {
			this.validation(new StringValidator.EndsWith(sufix, false));
			return this;
		}
		
		public StrBuilder mustEndWithIgnoreCase(String sufix) {
			this.validation(new StringValidator.EndsWith(sufix, true));
			return this;
		}
		
		/**
		 * By default all String values are 'trimmed to null', so that if a value
		 * is all whitespace, it is considered null.
		 * Calling keepWhitespaceInsideQuotes changes this behavior:  Values
		 * are still trimmed, however, if after trimming the first and last characters
		 * are double quotes, the quotes are removed and the entire string inside
		 * the quotes (including whitespace) is preserved.
		 * <p>
		 * When adding a value directly, either as a default value or a forced
		 * value, no trimmming is done:  It is assumed you are directly assigning
		 * the desired value.
		 * <p>
		 * With this option enabled when loading values (using ... to represent spaces):
		 * <ul>
		 * <li>"...some value..." - - > ...some value...
		 * <li>..."...some value..."... - - > ...some value...
		 * <li>...some value... - - > some value
		 * <li>...some "words" you said... - - > some "words" you said
		 * <li>..".some "words" you said.".. - - > .some "words" you said.</ul>
		 * @return 
		 */
		public StrBuilder keepWhitespaceInsideQuotes() {
			_valueType = QuotedStringType.instance();
			return this;
		}

	}
	
}
