package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.api.Validator;

import java.math.BigDecimal;

/**
 * Abstract class implementing Validator interface for BigDec.
 * Extended by nested static classes. The nested classes implement
 * constraints that may be used when building the property.
 */
public abstract class BigDecValidator  implements Validator<BigDecimal> {

    @Override
    public boolean isSpecificationValid() {
        return true;
    }

    @Override
    public String getInvalidSpecificationMessage() {
        return "THIS VALIDATION IS ALWAYS VALID";
    }

    /**
     * Validate that a BigDecimal is greater than a specified reference.
     */
    public static class GreaterThan extends BigDecValidator {

        private final BigDecimal ref;

        public GreaterThan(BigDecimal ref) {
            this.ref = ref;
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

        private final BigDecimal ref;

        public GreaterThanOrEqualTo(BigDecimal ref) {
            this.ref = ref;
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

        private final BigDecimal ref;

        public LessThan(BigDecimal ref) {
            this.ref = ref;
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

        private final BigDecimal ref;

        public LessThanOrEqualTo(BigDecimal ref) {
            this.ref = ref;
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
