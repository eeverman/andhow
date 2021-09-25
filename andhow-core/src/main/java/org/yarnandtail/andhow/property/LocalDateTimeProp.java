package org.yarnandtail.andhow.property;

import java.time.LocalDateTime;
import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.LocalDateTimeValidator;
import org.yarnandtail.andhow.valuetype.LocalDateTimeType;

/**
 * A Property that refers to a Long value.
 * 
 * All the basic Java types use a three letter abv. to keep declaration lines
 * short, in the form of:  [Type]Prop
 * 
 * By default this uses the TrimToNullTrimmer, which removes all whitespace from
 * the value and ultimately null if the value is all whitespace.
 * 
 * @author eeverman
 */
public class LocalDateTimeProp extends PropertyBase<LocalDateTime> {
	
	public LocalDateTimeProp(
			LocalDateTime defaultValue, boolean required, String shortDesc, List<Validator<LocalDateTime>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<LocalDateTime> valueType, Trimmer trimmer,
			String helpText) {
		
		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}
	
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
