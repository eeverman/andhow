package org.yarnandtail.andhow.property;

import java.util.List;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.DblValidator;
import org.yarnandtail.andhow.valuetype.DblType;

/**
 * A Property that refers to a Double value.
 * 
 * All the basic Java types use a three letter abv. to keep declaration lines
 * short, in the form of:  [Type]Prop
 * 
 * By default this uses the TrimToNullTrimmer, which removes all whitespace from
 * the value and ultimately null if the value is all whitespace.
 * 
 * @author eeverman
 */
public class DblProp extends PropertyBase<Double> {
	
	public DblProp(
			Double defaultValue, boolean required, String shortDesc, List<Validator<Double>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<Double> valueType, Trimmer trimmer,
			String helpText) {
		
		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}
	
	public static DblBuilder builder() {
		return new DblBuilder();
	}
	
	public static class DblBuilder extends PropertyBuilderBase<DblBuilder, DblProp, Double> {

		public DblBuilder() {
			instance = this;
			valueType(DblType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public DblProp build() {

			return new DblProp(_defaultValue, _nonNull, _shortDesc, _validators,
				_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}
		
		public DblBuilder mustBeGreaterThan(double reference) {
			validation(new DblValidator.GreaterThan(reference));
			return instance;
		}
		
		public DblBuilder mustBeGreaterThanOrEqualTo(double reference) {
			validation(new DblValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}
		
		public DblBuilder mustBeLessThan(double reference) {
			validation(new DblValidator.LessThan(reference));
			return instance;
		}
		
		public DblBuilder mustBeLessThanOrEqualTo(double reference) {
			validation(new DblValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
