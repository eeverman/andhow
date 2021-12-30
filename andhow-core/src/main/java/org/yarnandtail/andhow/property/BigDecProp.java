package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.BigDecValidator;
import org.yarnandtail.andhow.valuetype.BigDecType;

import java.math.BigDecimal;
import java.util.List;

/**
 * A {@link BigDecimal} configuration Property
 * <p>
 * Parsing values from strings is done by the {@link BigDecType}, which parses values using the
 * {@link BigDecimal#BigDecimal(String)}.  The constructor accepts many different forms -
 * refer there for valid String formats.
 * <p>
 * Before parsing String values, the {@link TrimToNullTrimmer} is applied by default.
 */
public class BigDecProp extends PropertyBase<BigDecimal> {

	/**
	 * Construct an instance of BigDecProp
	 *
	 * @param defaultValue default value
	 * @param required     make the property required or not
	 * @param shortDesc    short description of the property
	 * @param validators   list of validators for the property
	 * @param aliases      aliases of the property
	 * @param paramType    property type
	 * @param valueType    property value type
	 * @param trimmer      trimmer associated with the property
	 * @param helpText     help text of the property
	 */
	public BigDecProp(BigDecimal defaultValue, boolean required, String shortDesc, List<Validator<BigDecimal>> validators,
										List<Name> aliases, PropertyType paramType, ValueType<BigDecimal> valueType, Trimmer trimmer,
										String helpText) {

		super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
	}

	/**
	 * A chainable builder for this property that should terminate with {@code build()}
	 * <p>
	 * Use as {@code BigDecProp.builder()...series of builder methods...build();}
	 * <p>
	 * @return The builder instance that can be chained
	 */
	public static BigDecBuilder builder() {
		return new BigDecBuilder();
	}

	/**
	 * Build a BigDecProp
	 */
	public static class BigDecBuilder extends PropertyBuilderBase<BigDecBuilder, BigDecProp, BigDecimal> {

		/**
		 * Construct an instance of BigDecBuilder
		 */
		public BigDecBuilder() {
			instance = this;
			valueType(BigDecType.instance());
			trimmer(TrimToNullTrimmer.instance());
		}

		@Override
		public BigDecProp build() {
			return new BigDecProp(_defaultValue, _nonNull, _desc, _validators,
					_aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);
		}

		/**
		 * The property must be greater than the reference
		 *
		 * @param reference value the property must be greater than
		 * @return the builder instance
		 */
		public BigDecBuilder greaterThan(BigDecimal reference) {
			validation(new BigDecValidator.GreaterThan(reference));
			return instance;
		}

		/**
		 * The property must be greater than or equal to the reference
		 *
		 * @param reference value the property must be greater than or equal to
		 * @return the builder instance
		 */
		public BigDecBuilder greaterThanOrEqualTo(BigDecimal reference) {
			validation(new BigDecValidator.GreaterThanOrEqualTo(reference));
			return instance;
		}

		/**
		 * The property must be less than the reference
		 *
		 * @param reference value the property must be less than
		 * @return the builder instance
		 */
		public BigDecBuilder lessThan(BigDecimal reference) {
			validation(new BigDecValidator.LessThan(reference));
			return instance;
		}

		/**
		 * The property must be less than or equal to the reference
		 *
		 * @param reference value the property must be less than or equal to
		 * @return the builder instance
		 */
		public BigDecBuilder lessThanOrEqualTo(BigDecimal reference) {
			validation(new BigDecValidator.LessThanOrEqualTo(reference));
			return instance;
		}
	}
}
