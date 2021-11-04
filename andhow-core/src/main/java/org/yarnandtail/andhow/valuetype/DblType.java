package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Metadata and parsing for configuration Properties of the {@link Double} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class DblType extends BaseValueType<Double> {

	private static final DblType instance = new DblType();

	protected DblType() {
		super(Double.class);
	}

	/**
	 * @return An instance of the {@link #DblType()}
	 * @deprecated since 0.4.1. Use {@link #instance()} instead
	 */
	@Deprecated
	public static DblType get() {
		return instance();
	}

	/**
	 * Fetch the single, shared instace of this ValueType
	 * <p>
	 * @return An instance of the {@link #DblType}
	 */
	public static DblType instance() {
		return instance;
	}

	/**
	 * Parses a String to a {@link Double}.  The String should already be trimmed.
	 * <p>
	 * Parsing is done via {@link Double#parseDouble(String)}.
	 * <p>
	 *
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws ParsingException if unparsable.
	 */
	@Override
	public Double parse(String sourceValue) throws ParsingException {

		if (sourceValue != null) {
			try {
				return Double.parseDouble(sourceValue);
			} catch (Exception e) {
				throw new ParsingException("Unable to convert to a Double numeric value", sourceValue, e);
			}
		} else {
			return null;
		}
	}

	@Override
	public Double cast(Object o) throws RuntimeException {
		return (Double) o;
	}

}
