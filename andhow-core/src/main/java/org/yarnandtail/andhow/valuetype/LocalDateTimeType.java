package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

import java.time.LocalDateTime;

/**
 * Metadata and parsing for configuration Properties of the {@link java.time.LocalDateTime} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class LocalDateTimeType extends BaseValueType<LocalDateTime> {

	private static final LocalDateTimeType instance = new LocalDateTimeType();

	private LocalDateTimeType() {
		super(LocalDateTime.class);
	}

	/**
	 * @return An instance of the {@link #LocalDateTimeType()}
	 * @deprecated since 0.4.1. Use {@link #instance()} instead
	 */
	@Deprecated
	public static LocalDateTimeType get() {
		return instance();
	}

	/**
	 * @return An instance of the {@link #LocalDateTimeType()}
	 */
	public static LocalDateTimeType instance() {
		return instance;
	}

	/**
	 * Parses a String to a {@link java.time.LocalDateTime}.  The String should already be trimmed.
	 * <p>
	 * Parsing is done by calling the {@link java.time.LocalDateTime#parse(CharSequence)}.
	 * <p>
	 *
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws ParsingException if unparsable.
	 */
	@Override
	public LocalDateTime parse(String sourceValue) throws ParsingException {

		if (sourceValue != null) {
			try {
				return LocalDateTime.parse(sourceValue);
			} catch (Exception e) {
				throw new ParsingException("Unable to convert to a LocalDateTime", sourceValue, e);
			}
		} else {
			return null;
		}
	}

	@Override
	public LocalDateTime cast(Object o) throws RuntimeException {
		return (LocalDateTime) o;
	}

}
