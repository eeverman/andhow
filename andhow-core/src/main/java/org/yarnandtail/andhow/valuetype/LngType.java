package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Metadata and parsing for configuration Properties of the {@link Long} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class LngType extends BaseValueType<Long> {

	private static final LngType instance = new LngType();

	protected LngType() {
		super(Long.class);
	}

	/**
	 * Fetch the single, shared instace of this ValueType
	 * <p>
	 * @return An instance of the {@link #LngType()}
	 */
	public static LngType instance() {
		return instance;
	}

	/**
	 * Parses a String to a {@link Long}.  The String should already be trimmed.
	 * <p>
	 * Parsing is done via {@link Long#parseLong(String)}.
	 * <p>
	 *
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws ParsingException if unparsable.
	 */
	@Override
	public Long parse(String sourceValue) throws ParsingException {

		if (sourceValue != null) {
			try {
				return Long.parseLong(sourceValue);
			} catch (Exception e) {
				throw new ParsingException("Unable to convert to a Long numeric value", sourceValue, e);
			}
		} else {
			return null;
		}
	}

	@Override
	public Long cast(Object o) throws RuntimeException {
		return (Long) o;
	}

}
