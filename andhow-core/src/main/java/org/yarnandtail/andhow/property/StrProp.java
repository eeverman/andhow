package org.yarnandtail.andhow.property;

import java.math.BigDecimal;
import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.StringValidator;
import org.yarnandtail.andhow.valuetype.StrType;

/**
 * A {@link String} configuration Property
 * <p>
 * By default, StrProp uses the {@link QuotedSpacePreservingTrimmer}, which will keep
 * whitespace inside double quotes for loaders that are coming from text based sources, such
 * as properties files.
 */
public class StrProp extends PropertyBase<String> {

	public StrProp(String defaultValue, boolean nonNull, String shortDesc, List<Validator<String>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<String> valueType, Trimmer trimmer, String helpText) {

		super(defaultValue, nonNull, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code StrProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
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

			return new StrProp(_defaultValue, _nonNull, _desc, _validators, _aliases, PropertyType.SINGLE_NAME_VALUE,
					_valueType, _trimmer, _helpText);
		}

		/**
		 * @deprecated Use {@code StrBuilder.matches()}
		 */
		@Deprecated
		public StrBuilder mustMatchRegex(String regex) {
			return this.matches(regex);
		}

		public StrBuilder matches(String regex) {
			this.validation(new StringValidator.Regex(regex));
			return this;
		}

		/**
		 * @deprecated Use {@code StrBuilder.startsWith()}
		 */
		@Deprecated
		public StrBuilder mustStartWith(String prefix) {
			return this.startsWith(prefix);
		}

		public StrBuilder startsWith(String prefix) {
			this.validation(new StringValidator.StartsWith(prefix, false));
			return this;
		}

		/**
		 * @deprecated Use {@code StrBuilder.startsWithIgnoringCase()}
		 */
		@Deprecated
		public StrBuilder mustStartWithIgnoreCase(String prefix) {
			return this.startsWithIgnoringCase(prefix);
		}

		public StrBuilder startsWithIgnoringCase(String prefix) {
			this.validation(new StringValidator.StartsWith(prefix, true));
			return this;
		}

		/**
		 * @deprecated Use {@code StrBuilder.endsWith()}
		 */
		@Deprecated
		public StrBuilder mustEndWith(String sufix) {
			return this.endsWith(sufix);
		}

		public StrBuilder endsWith(String suffix) {
			this.validation(new StringValidator.EndsWith(suffix, false));
			return this;
		}

		/**
		 * @deprecated Use {@code StrBuilder.endsWithIgnoringCase()}
		 */
		@Deprecated
		public StrBuilder mustEndWithIgnoreCase(String sufix) {
			return this.endsWithIgnoringCase(sufix);
		}

		public StrBuilder endsWithIgnoringCase(String suffix) {
			this.validation(new StringValidator.EndsWith(suffix, true));
			return this;
		}

		/**
		 * @deprecated Use {@code StrBuilder.oneOf()}
		 */
		@Deprecated
		public StrBuilder mustEqual(String... values) {
			return this.oneOf(values);
		}

		public StrBuilder oneOf(String... values) {
			this.validation(new StringValidator.OneOf(values));
			return this;
		}

		public StrBuilder oneOfIgnoringCase(String... values) {
			this.validation(new StringValidator.OneOfIgnoringCase(values));
			return this;
		}
	}

}
