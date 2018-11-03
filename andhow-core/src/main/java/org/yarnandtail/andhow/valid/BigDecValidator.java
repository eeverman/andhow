package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.api.Validator;

import java.math.BigDecimal;

/**
 * Abstract class implementing Validator interface for BigDec.
 * Extended by nested static classes. The nested classes implement
 * constraints that may be used when building the property.
 */
public abstract class BigDecValidator implements Validator<BigDecimal> {

    final BigDecimal ref;

    /**
     * Base constructor of BigDecValidator constraints
     * @param ref to be compared to property value
     */
    BigDecValidator(BigDecimal ref) {
        this.ref = ref;
    }

    @Override
    public boolean isSpecificationValid() {
        return ref != null;
    }

    @Override
    public String getInvalidSpecificationMessage() {
        return "The constraint may not be null";
    }

    /**
     * Validate that a BigDecimal is greater than a specified reference.
     */
    public static class GreaterThan extends BigDecValidator {
        /**
         * Construct a GreaterThan property constraint
         * @param ref to be compared to property value
         */
        public GreaterThan(BigDecimal ref) {
            super(ref);
        }

        @Override
        public boolean isValid(BigDecimal value) {
            return (value != null) && (value.compareTo(ref) > 0);
        }

        @Override
        public String getTheValueMustDescription() {
            return "be greater than " + ref;
        }
    }

    /**
     * Validate that a BigDecimal is greater than or equal to a specified reference.
     */
    public static class GreaterThanOrEqualTo extends BigDecValidator {
        /**
         * Construct a GreaterThanOrEqualTo property constraint
         * @param ref to be compared to property value
         */
        public GreaterThanOrEqualTo(BigDecimal ref) {
            super(ref);
        }

        @Override
        public boolean isValid(BigDecimal value) {
            return (value != null) && (value.compareTo(ref) >= 0);
        }

        @Override
        public String getTheValueMustDescription() {
            return "be greater than or equal to " + ref;
        }
    }

    /**
     * Validate that a BigDecimal is less than a specified reference.
     */
    public static class LessThan extends BigDecValidator {
        /**
         * Construct a LessThan property constraint
         * @param ref to be compared to property value
         */
        public LessThan(BigDecimal ref) {
            super(ref);
        }

        @Override
        public boolean isValid(BigDecimal value) {
            return (value != null) && (value.compareTo(ref) < 0);
        }

        @Override
        public String getTheValueMustDescription() {
            return "be less than " + ref;
        }
    }

    /**
     * Validate that a BigDecimal is less than or equal to a specified reference.
     */
    public static class LessThanOrEqualTo extends BigDecValidator {
        /**
         * Construct a LessThanOrEqualTo property constraint
         * @param ref to be compared to property value
         */
        public LessThanOrEqualTo(BigDecimal ref) {
            super(ref);
        }

        @Override
        public boolean isValid(BigDecimal value) {
            return (value != null) && (value.compareTo(ref) <= 0);
        }

        @Override
        public String getTheValueMustDescription() {
            return "be less than or equal to " + ref;
        }
    }
}
