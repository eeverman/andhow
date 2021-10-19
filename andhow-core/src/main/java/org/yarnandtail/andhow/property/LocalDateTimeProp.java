package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.LocalDateTimeValidator;
import org.yarnandtail.andhow.valuetype.LocalDateTimeType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A {@link java.time.LocalDateTime} configuration Property
 * <p>
 * Parsing values from strings is done by the {@link LocalDateTimeType}, which parses values using
 * the {@link java.time.LocalDateTime#parse(CharSequence)} method, so the format
 * follows that specification, which looks like this:
 * <ul>
 *   <li>{@code 2007-12-03T00:00}</li>
 *   <li>{@code 2007-12-03T23:15:30}</li>
 *   <li>{@code 2007-12-03T23:15:30.123}</li>
 *   <li>{@code 2007-12-03T23:15:30.123456789}</li>
 * </ul>
 * <p>
 * Before parsing String values, the {@link TrimToNullTrimmer} is applied by default.
 */
public class LocalDateTimeProp extends PropertyBase<LocalDateTime> {

	public LocalDateTimeProp(
			LocalDateTime defaultValue, boolean required, String shortDesc, List<Validator<LocalDateTime>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<LocalDateTime> valueType, Trimmer trimmer,
			String helpText) {

		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code LocalDateTimeProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
	public static LocalDateTimeBuilder builder() {
		return new LocalDateTimeBuilder();
	}

	public static class LocalDateTimeBuilder extends PropertyBuilderBase<LocalDateTimeBuilder, LocalDateTimeProp, LocalDateTime> {

		public LocalDateTimeBuilder() {
			instance = this;
			valueType(LocalDateTimeType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public LocalDateTimeProp build() {

			return new LocalDateTimeProp(_defaultValue, _nonNull, _desc, _validators,
					_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}

		/**
		 * @deprecated Use {@code LocalDateTimeBuilder.before()}
		 */
		@Deprecated
		public LocalDateTimeBuilder mustBeBefore(LocalDateTime reference) {
			return this.before(reference);
		}

		/**
		 * @deprecated Use {@code LocalDateTimeBuilder.sameOrBefore()}
		 */
		@Deprecated
		public LocalDateTimeBuilder mustBeSameTimeOrBefore(LocalDateTime reference) {
			return this.sameOrBefore(reference);
		}

		/**
		 * @deprecated Use {@code LocalDateTimeBuilder.after()}
		 */
		@Deprecated
		public LocalDateTimeBuilder mustBeAfter(LocalDateTime reference) {
			return this.after(reference);
		}

		/**
		 * @deprecated Use {@code LocalDateTimeBuilder.sameOrAfter()}
		 */
		@Deprecated
		public LocalDateTimeBuilder mustBeSameTimeOrAfter(LocalDateTime reference) {
			return this.sameOrAfter(reference);
		}

		public LocalDateTimeBuilder before(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.Before(reference));
			return instance;
		}

		public LocalDateTimeBuilder sameOrBefore(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.SameTimeOrBefore(reference));
			return instance;
		}

		public LocalDateTimeBuilder after(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.After(reference));
			return instance;
		}

		public LocalDateTimeBuilder sameOrAfter(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.SameTimeOrAfter(reference));
			return instance;
		}

	}

}
