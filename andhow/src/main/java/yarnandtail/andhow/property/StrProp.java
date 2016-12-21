package yarnandtail.andhow.property;

import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Trimmer;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.valid.StringValidator;
import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.valuetype.StringType;

/**
 *
 * @author eeverman
 */
public class StrProp extends PropertyBase<String> {
	
	public StrProp(
			String defaultValue, boolean required, String shortDesc, List<Validator<String>> validators,
			PropertyType paramType, ValueType<String> valueType, Trimmer trimmer,
			String helpText, String[] aliases) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, trimmer, helpText, aliases);
	}
	
	public static StrBuilder builder() {
		return new StrBuilder();
	}
	
	
	public static class StrBuilder extends PropertyBuilderBase<StrBuilder, StrProp, String> {

		
		public StrBuilder() {
			instance = this;
			valueType(StringType.instance());
			trimmer(QuotedSpacePreservingTrimmer.instance());
		}

		@Override
		public StrProp build() {

			return new StrProp(_defaultValue, _required, _shortDesc, _validators,
				_paramType, _valueType, _trimmer,
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

	}
	
}
