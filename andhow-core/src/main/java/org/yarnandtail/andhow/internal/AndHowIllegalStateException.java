package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.Property;

/**
 * Runtime exceptions for illegal operations / states
 */
public abstract class AndHowIllegalStateException extends RuntimeException {
	public AndHowIllegalStateException(String message) {
		super(message);
	}

	public static class UnrecognizedPropertyException extends AndHowIllegalStateException {
		final Property<?> _property;

		public UnrecognizedPropertyException(Property<?> property) {

			super("Unrecognized Property of type '" + property.getClass().getCanonicalName() + "'. " +
					"Likely caused by one of:" + System.lineSeparator() +
					" - AndHow error'ed on startup, but the error was caught and did not cause app " +
					"startup to abort.  Check the logs and remove try-catch that intercepts RuntimeExceptions." +
					System.lineSeparator() +
					" - Code was compiled without AndHow's annotation processor." + System.lineSeparator() +
					" - The Property was created in some exotic way that was not detected by the AndHow " +
					"annotation processor.  Review the creation of this Property and make sure it follows " +
					"AndHow guidelines.");

			_property = property;
		}

		public Property<?> getProperty() {
			return _property;
		}
	}
}
