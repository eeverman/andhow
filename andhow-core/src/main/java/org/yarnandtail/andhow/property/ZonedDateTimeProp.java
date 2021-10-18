package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.valid.ZonedDateTimeValidator;
import org.yarnandtail.andhow.valuetype.ZonedDateTimeType;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * A Property that refers to a ZonedDateTime value.
 *
 * By default this uses the TrimToNullTrimmer, which removes all whitespace from
 * the value and ultimately null if the value is all whitespace. The String
 * constructor version is used when creating instances of BigDecimal.
 *
 * @author chace86
 */
public class ZonedDateTimeProp extends PropertyBase<ZonedDateTime> {

    public ZonedDateTimeProp(ZonedDateTime defaultValue, boolean required, String shortDesc, List<Validator<ZonedDateTime>> validators,
                             List<Name> aliases, PropertyType paramType, ValueType<ZonedDateTime> valueType, Trimmer trimmer, String helpText) {
        super(defaultValue, required, shortDesc, validators, aliases, paramType, valueType, trimmer, helpText);
    }

    /**
     * Return an instance of ZonedDateTimeBuilder
     */
    public static ZonedDateTimeBuilder builder() { return new ZonedDateTimeBuilder(); }

    /**
     * Build a ZonedDateTimeProp
     */
    public static class ZonedDateTimeBuilder extends PropertyBuilderBase<ZonedDateTimeBuilder, ZonedDateTimeProp, ZonedDateTime> {

        /**
         * Construct an instance of ZonedDateTimeBuilder
         */
        public ZonedDateTimeBuilder() {
            instance = this;
            valueType(ZonedDateTimeType.instance());
            trimmer(TrimToNullTrimmer.instance());
        }

        @Override
        public ZonedDateTimeProp build() {
            return new ZonedDateTimeProp(_defaultValue, _nonNull, _desc, _validators,
                    _aliases, PropertyType.SINGLE_NAME_VALUE, _valueType, _trimmer, _helpText);
        }

        /**
         * The property must be greater than the reference
         * @param reference value the property must be greater than
         * @return the builder instance
         */
        public ZonedDateTimeBuilder mustBeGreaterThan(ZonedDateTime reference) {
            validation(new ZonedDateTimeValidator.GreaterThan(reference));
            return instance;
        }

        /**
         * The property must be greater than or equal to the reference
         * @param reference value the property must be greater than or equal to
         * @return the builder instance
         */
        public ZonedDateTimeBuilder mustBeGreaterThanOrEqualTo(ZonedDateTime reference) {
            validation(new ZonedDateTimeValidator.GreaterThanOrEqualTo(reference));
            return instance;
        }

        /**
         * The property must be less than the reference
         * @param reference value the property must be less than
         * @return the builder instance
         */
        public ZonedDateTimeBuilder mustBeLessThan(ZonedDateTime reference) {
            validation(new ZonedDateTimeValidator.LessThan(reference));
            return instance;
        }

        /**
         * The property must be less than or equal to the reference
         * @param reference value the property must be less than or equal to
         * @return the builder instance
         */
        public ZonedDateTimeBuilder mustBeLessThanOrEqualTo(ZonedDateTime reference) {
            validation(new ZonedDateTimeValidator.LessThanOrEqualTo(reference));
            return instance;
        }
    }
}
