package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Metadata and parsing for configuration Properties of the {@link Integer} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class IntType extends BaseValueType<Integer> {

	private static final IntType instance = new IntType();

	protected IntType() {
		super(Integer.class);
	}

	/**
	 * Fetch the single, shared instace of this ValueType
	 * <p>
	 * @return An instance of the {@link #IntType()}
	 */
	public static IntType instance() {
		return instance;
	}

	/**
	 * Parses a String to an {@link Integer}.  The String should already be trimmed.
	 * <p>
	 * Parsing is done via {@link Integer#parseInt(String)}.
	 * <p>
	 *
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws ParsingException if unparsable.
	 */
	@Override
	public Integer parse(String sourceValue) throws ParsingException {

		if (sourceValue != null) {
			try {
				return Integer.parseInt(sourceValue);
			} catch (Exception e) {
				throw new ParsingException("Unable to convert to an integer", sourceValue, e);
			}
		} else {
			return null;
		}
	}

	@Override
	public Integer cast(Object o) throws RuntimeException {
		return (Integer) o;
	}

}
