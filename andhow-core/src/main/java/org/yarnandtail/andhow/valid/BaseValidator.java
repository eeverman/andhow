package org.yarnandtail.andhow.valid;

import org.yarnandtail.andhow.api.Validator;

public abstract class BaseValidator<T> implements Validator<T> {

	@Override
	public boolean isValid(final T value) throws IllegalArgumentException {
		if (value != null) {
			return isValidWithoutNull(value);
		} else {
			throw new IllegalArgumentException("The value to be validated cannot be null. " +
					"This is likely due to a logic error in a Loader implementation.");
		}
	}

	/**
	 * Same as isValid, but no null checking is needed.
	 * <p>
	 * @param value The value, which is guaranteed to not be null.
	 * @return Is the value valid according to the rule this Validator enforces?
	 */
	public abstract boolean isValidWithoutNull(final T value);
}
