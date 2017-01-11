package yarnandtail.andhow.property;

import java.time.LocalDateTime;
import java.util.List;
import yarnandtail.andhow.PropertyType;
import yarnandtail.andhow.Trimmer;
import yarnandtail.andhow.Validator;
import yarnandtail.andhow.ValueType;
import yarnandtail.andhow.valid.LngValidator;
import yarnandtail.andhow.valid.LocalDateTimeValidator;
import yarnandtail.andhow.valuetype.LngType;
import yarnandtail.andhow.valuetype.LocalDateTimeType;

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
			PropertyType paramType, ValueType<LocalDateTime> valueType, Trimmer trimmer,
			String helpText) {
		
		super(defaultValue, required, shortDesc, validators, paramType, valueType, trimmer, helpText);
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

			return new LocalDateTimeProp(_defaultValue, _required, _shortDesc, _validators,
				PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}
		
		public LocalDateTimeBuilder mustBeBefore(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.Before(reference));
			return instance;
		}
		
		public LocalDateTimeBuilder mustBeSameTimeOrBefore(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.SameTimeOrBefore(reference));
			return instance;
		}
		
		public LocalDateTimeBuilder mustBeAfter(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.After(reference));
			return instance;
		}
		
		public LocalDateTimeBuilder mustBeSameTimeOrAfter(LocalDateTime reference) {
			validation(new LocalDateTimeValidator.SameTimeOrAfter(reference));
			return instance;
		}

	}

}
