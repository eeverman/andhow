package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.BigDecValidator;
import org.yarnandtail.andhow.valuetype.BigDecType;

import java.math.BigDecimal;
import java.util.List;

/**
 * A Property that refers to a BigDecimal value.
 *
 * By default this uses the TrimToNullTrimmer, which removes all whitespace from
 * the value and ultimately null if the value is all whitespace. The String
 * constructor version is used when creating instances of BigDecimal.
 *
 * @author chace86
 */
public class BigDecProp extends PropertyBase<BigDecimal> {

    /**
     * Construct an instance of BigDecProp
     * @param defaultValue default value
     * @param required make the property required or not
     * @param shortDesc short description of the property
     * @param validators list of validators for the property
     * @param aliases aliases of the property
     * @param paramType property type
     * @param valueType property value type
     * @param trimmer trimmer associated with the property
     * @param helpText help text of the property
     */
    public BigDecProp(BigDecimal defaultValue, boolean required, String shortDesc, List<Validator<BigDecimal>> validators,
                      List<Name> aliases, PropertyType paramType, ValueType<BigDecimal> valueType, Trimmer trimmer,
                      String helpText) {

        super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
    }

    /**
     * Return an instance of BigDecBuilder
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
         * @param reference value the property must be greater than
         * @return the builder instance
         */
        public BigDecBuilder mustBeGreaterThan(BigDecimal reference) {
            validation(new BigDecValidator.GreaterThan(reference));
            return instance;
        }

        /**
         * The property must be greater than or equal to the reference
         * @param reference value the property must be greater than or equal to
         * @return the builder instance
         */
        public BigDecBuilder mustBeGreaterThanOrEqualTo(BigDecimal reference) {
            validation(new BigDecValidator.GreaterThanOrEqualTo(reference));
            return instance;
        }

        /**
         * The property must be less than the reference
         * @param reference value the property must be less than
         * @return the builder instance
         */
        public BigDecBuilder mustBeLessThan(BigDecimal reference) {
            validation(new BigDecValidator.LessThan(reference));
            return instance;
        }

        /**
         * The property must be less than or equal to the reference
         * @param reference value the property must be less than or equal to
         * @return the builder instance
         */
        public BigDecBuilder mustBeLessThanOrEqualTo(BigDecimal reference) {
            validation(new BigDecValidator.LessThanOrEqualTo(reference));
            return instance;
        }
    }
}
