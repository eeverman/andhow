package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.StringValidator;
import org.yarnandtail.andhow.valuetype.StrType;

/**
 * A Property that refers to a String value.
 * 
 * All the basic Java types use a three letter abv. to keep declaration lines
 * short, in the form of:  [Type]Prop
 * 
 * By default, this uses the QuotedSpacePreservingTrimmer, which will keep
 * whitespace inside double quotes.
 * 
 * @author eeverman
 */
public class StrProp extends PropertyBase<String> {
	
	public StrProp(
			String defaultValue, boolean required, String shortDesc, List<Validator<String>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<String> valueType, Trimmer trimmer,
			String helpText) {
		
		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}
	
	public static StrBuilder builder() {
		return new StrBuilder();
	}
	
	
	public static class StrBuilder extends PropertyBuilderBase<StrBuilder, StrProp, String> {

		
		public StrBuilder() {
			instance = this;
			valueType(StrType.instance());
			trimmer(QuotedSpacePreservingTrimmer.instance());
		}

		@Override
		public StrProp build() {

			return new StrProp(_defaultValue, _required, _shortDesc, _validators,
				_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);
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
