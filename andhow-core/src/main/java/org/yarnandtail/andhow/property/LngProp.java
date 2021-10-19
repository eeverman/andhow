package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.LngValidator;
import org.yarnandtail.andhow.valuetype.LngType;

import java.util.List;

/**
 * A {@link Long} configuration Property
 * <p>
 * Parsing values from strings is done by the {@link LngType}, which parses values using the
 * {@link Long#parseLong(String)}.  Note that the {@code parseLong} method
 * <em>does not accept a trailing 'L' or 'l'</em> as the Java literal does.
 * E.g., this is the correct way to spec a Long value in a properties file:
 * <code>
 * name.of.my.long.property.MY_PROPERTY = 90
 * </code>
 * Before parsing String values, the {@link TrimToNullTrimmer} is applied by default.
 */
public class LngProp extends PropertyBase<Long> {

	public LngProp(
			Long defaultValue, boolean required, String shortDesc, List<Validator<Long>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<Long> valueType, Trimmer trimmer,
			String helpText) {

		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code LngProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
	public static LngBuilder builder() {
		return new LngBuilder();
	}

	public static class LngBuilder extends PropertyBuilderBase<LngBuilder, LngProp, Long> {

		public LngBuilder() {
			instance = this;
			valueType(LngType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public LngProp build() {

			return new LngProp(_defaultValue, _nonNull, _desc, _validators,
					_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}

		/**
		 * @deprecated Use {@code LngBuilder.greaterThan()}
		 */
		@Deprecated
		public LngBuilder mustBeGreaterThan(long reference) {
			return this.greaterThan(reference);
		}

		public LngBuilder greaterThan(long reference) {
			validation(new LngValidator.GreaterThan(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code LngBuilder.greaterThanOrEqualTo()}
		 */
		@Deprecated
		public LngBuilder mustBeGreaterThanOrEqualTo(long reference) {
			return greaterThanOrEqualTo(reference);
		}

		public LngBuilder greaterThanOrEqualTo(long reference) {
			validation(new LngValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code LngBuilder.lessThan()}
		 */
		@Deprecated
		public LngBuilder mustBeLessThan(long reference) {
			return this.lessThan(reference);
		}

		public LngBuilder lessThan(long reference) {
			validation(new LngValidator.LessThan(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code LngBuilder.lessThanOrEqualTo()}
		 */
		@Deprecated
		public LngBuilder mustBeLessThanOrEqualTo(long reference) {
			return this.lessThanOrEqualTo(reference);
		}

		public LngBuilder lessThanOrEqualTo(long reference) {
			validation(new LngValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
