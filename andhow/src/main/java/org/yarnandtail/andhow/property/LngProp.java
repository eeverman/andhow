package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.valid.LngValidator;
import org.yarnandtail.andhow.valuetype.LngType;

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
public class LngProp extends PropertyBase<Long> {
	
	public LngProp(
			Long defaultValue, boolean required, String shortDesc, List<Validator<Long>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<Long> valueType, Trimmer trimmer,
			String helpText) {
		
		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}
	
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

			return new LngProp(_defaultValue, _required, _shortDesc, _validators,
				_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}
		
		public LngBuilder mustBeGreaterThan(long reference) {
			validation(new LngValidator.GreaterThan(reference));
			return instance;
		}
		
		public LngBuilder mustBeGreaterThanOrEqualTo(long reference) {
			validation(new LngValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}
		
		public LngBuilder mustBeLessThan(long reference) {
			validation(new LngValidator.LessThan(reference));
			return instance;
		}
		
		public LngBuilder mustBeLessThanOrEqualTo(long reference) {
			validation(new LngValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
