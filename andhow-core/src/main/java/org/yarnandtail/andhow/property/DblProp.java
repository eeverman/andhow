package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.DblValidator;
import org.yarnandtail.andhow.valuetype.DblType;

import java.math.BigDecimal;
import java.util.List;

/**
 * A {@link BigDecimal} configuration Property
 * <p>
 * Parsing values from strings is done by the {@link DblType}, which parses values using the
 * {@link Double#parseDouble(String)}.  Note that the {@code parseDouble} method accepts a trailing
 * 'D' of 'F' (lower case is ok too).
 * <p>
 * E.g., here are several acceptable ways to spec double value in a properties file:
 * <code>
 * name.of.my.double.property.MY_PROPERTY_1 = 90.00
 * name.of.my.double.property.MY_PROPERTY_2 = 80.00D
 * name.of.my.double.property.MY_PROPERTY_3 = 70.00F
 * name.of.my.double.property.MY_PROPERTY_4 = 60.00d
 * name.of.my.double.property.MY_PROPERTY_5 = 4
 * </code>
 * <p>
 * Before parsing String values, the {@link TrimToNullTrimmer} is applied by default.
 */
public class DblProp extends PropertyBase<Double> {

	public DblProp(
			Double defaultValue, boolean required, String shortDesc, List<Validator<Double>> validators,
			List<Name> aliases, PropertyType paramType, ValueType<Double> valueType, Trimmer trimmer,
			String helpText) {

		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code DblProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
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

		public DblBuilder greaterThan(double reference) {
			validation(new DblValidator.GreaterThan(reference));
			return instance;
		}

		public DblBuilder greaterThanOrEqualTo(double reference) {
			validation(new DblValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}

		public DblBuilder lessThan(double reference) {
			validation(new DblValidator.LessThan(reference));
			return instance;
		}

		public DblBuilder lessThanOrEqualTo(double reference) {
			validation(new DblValidator.LessThanOrEqualTo(reference));
			return instance;
		}

	}

}
