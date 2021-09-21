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
 * If a DblProp is configured as a string, such as from a properties file, on
 * command line, environment variable, etc., the value MAY include a trailing
 * 'D' of 'F' (lower case is ok too), as is done with Java literals.  This is
 * different than the behavior of LngProp, but is a result of how Java parses
 * these values.
 *
 * E.g., here are several correct ways to spec double value in a properties file:
 * <code>
 * name.of.my.double.property.MY_PROPERTY_1 = 90.00
 * name.of.my.double.property.MY_PROPERTY_2 = 80.00D
 * name.of.my.double.property.MY_PROPERTY_3 = 70.00F
 * name.of.my.double.property.MY_PROPERTY_4 = 60.00d
 * name.of.my.double.property.MY_PROPERTY_5 = 4
 * </code>
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

			return new DblProp(_defaultValue, _nonNull, _desc, _validators,
				_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);

		}

		/**
		 * @deprecated Use {@code DblBuilder.greaterThan()}
		 */
		@Deprecated
		public DblBuilder mustBeGreaterThan(double reference) {
			return this.greaterThan(reference);
		}

		public DblBuilder greaterThan(double reference) {
			validation(new DblValidator.GreaterThan(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code DblBuilder.greaterThanOrEqualTo()}
		 */
		@Deprecated
		public DblBuilder mustBeGreaterThanOrEqualTo(double reference) {
			return this.greaterThanOrEqualTo(reference);
		}

		public DblBuilder greaterThanOrEqualTo(double reference) {
			validation(new DblValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code DblBuilder.lessThan()}
		 */
		@Deprecated
		public DblBuilder mustBeLessThan(double reference) {
			return this.lessThan(reference);
		}

		public DblBuilder lessThan(double reference) {
			validation(new DblValidator.LessThan(reference));
			return instance;
		}

		/**
		 * @deprecated Use {@code DblBuilder.lessThanOrEqualTo()}
		 */
		@Deprecated
		public DblBuilder mustBeLessThanOrEqualTo(double reference) {
			return this.lessThanOrEqualTo(reference);
		}

		public DblBuilder lessThanOrEqualTo(double reference) {
			validation(new DblValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
