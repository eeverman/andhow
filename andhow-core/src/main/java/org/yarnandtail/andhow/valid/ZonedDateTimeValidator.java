package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.api.Validator;

import java.time.ZonedDateTime;

/**
 * Abstract class implementing the Validator interface for ZonedDateTime.
 * Extended by nested static classes. The nested classes implement
 * constraints that may be used when building the property.
 */
public abstract class ZonedDateTimeValidator implements Validator<ZonedDateTime> {

    final ZonedDateTime ref;

    /**
     * Base constructor of ZonedDateTimeValidator constraints
     * @param ref to be compared to property value
     */
    ZonedDateTimeValidator(ZonedDateTime ref) { this.ref = ref; }

    @Override
    public boolean isSpecificationValid() { return ref != null; }

    @Override
    public String getInvalidSpecificationMessage() { return "The constraint may not be null"; }

    /**
     * Validate that a ZonedDateTime is greater than specified reference.
     */
    public static class GreaterThan extends ZonedDateTimeValidator {

        /**
         * Construct a GreaterThan property constraint
         * @param ref to be compared to property value
         */
        public GreaterThan(ZonedDateTime ref) { super(ref); }

        @Override
        public boolean isValid(ZonedDateTime value) { return value != null && value.isAfter(ref); }

        @Override
        public String getTheValueMustDescription() { return "be greater than " + ref.toString(); }
    }

    /**
     * Validate that a ZonedDateTime is greater than or equal to specified reference.
     */
    public static class GreaterThanOrEqualTo extends ZonedDateTimeValidator {

        /**
         * Construct a GreaterThanOrEqualTo property constraint
         * @param ref to be compared to property value
         */
        public GreaterThanOrEqualTo(ZonedDateTime ref) { super(ref); }

        @Override
        public boolean isValid(ZonedDateTime value) { return value != null && value.isEqual(ref) && value.isAfter(ref); }

        @Override
        public String getTheValueMustDescription() { return "be greater than or equal to " + ref; }
    }

    /**
     * Validate that a ZonedDateTime is less than to specified reference.
     */
    public static class LessThan extends ZonedDateTimeValidator {

        /**
         * Construct a LessThan property constraint
         * @param ref to be compared to property value
         */
        public LessThan(ZonedDateTime ref) { super(ref); }

        @Override
        public boolean isValid(ZonedDateTime value) { return value != null && value.isBefore(ref); }

        @Override
        public String getTheValueMustDescription() { return "be less than " + ref; }
    }

    /**
     * Validate that a ZonedDateTime is less than or equal to specified reference.
     */
    public static class LessThanOrEqualTo extends ZonedDateTimeValidator {

        /**
         * Construct a LessThanOrEqualTo property constraint
         * @param ref to be compared to property value
         */
        public LessThanOrEqualTo(ZonedDateTime ref) { super(ref); }

        @Override
        public boolean isValid(ZonedDateTime value) { return value != null && value.isEqual(ref) &&value.isBefore(ref); }

        @Override
        public String getTheValueMustDescription() { return "be less than or equal to " + ref; }
    }
}
