package org.yarnandtail.andhow.valid;

import java.time.LocalDateTime;

/**
 * Validator implementations for LocalDateTime ValueTypes.
 */
public abstract class LocalDateTimeValidator extends BaseValidator<LocalDateTime> {

	protected LocalDateTime ref;

	@Override
	public boolean isSpecificationValid() {
		return ref != null;
	}

	@Override
	public String getInvalidSpecificationMessage() {
		return "The reference LocalDateTime cannot be null";
	}

	public static class Before extends LocalDateTimeValidator {

		public Before(LocalDateTime ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final LocalDateTime value) {
			return value.isBefore(ref);
		}

		@Override
		public String getTheValueMustDescription() {
			return "be before " + ref.toString();
		}
	}

	public static class SameTimeOrBefore extends LocalDateTimeValidator {

		public SameTimeOrBefore(LocalDateTime ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final LocalDateTime value) {
			return value.isBefore(ref) || value.isEqual(ref);
		}

		@Override
		public String getTheValueMustDescription() {
			return "be the same date and time or before " + ref.toString();
		}
	}

	public static class After extends LocalDateTimeValidator {

		public After(LocalDateTime ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final LocalDateTime value) {
			return value.isAfter(ref);
		}

		@Override
		public String getTheValueMustDescription() {
			return "be after " + ref.toString();
		}
	}

	public static class SameTimeOrAfter extends LocalDateTimeValidator {

		public SameTimeOrAfter(LocalDateTime ref) {
			this.ref = ref;
		}

		@Override
		public boolean isValidWithoutNull(final LocalDateTime value) {
			return value.isAfter(ref) || value.isEqual(ref);
		}

		@Override
		public String getTheValueMustDescription() {
			return "be the same date and time or after " + ref.toString();
		}
	}

}
