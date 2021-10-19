package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

import java.math.BigDecimal;

/**
 * Metadata and parsing for configuration Properties of the {@link BigDecimal} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class BigDecType extends BaseValueType<BigDecimal> {

	private static final BigDecType INSTANCE = new BigDecType();

	private BigDecType() {
		super(BigDecimal.class);
	}

	/**
	 * Construct an instance of BigDecType
	 */
	public static BigDecType instance() {
		return INSTANCE;
	}


	/**
	 * Parses a String to a {@link BigDecType}.  The String should already be trimmed.
	 * <p>
	 * Parsing is done by calling the {@link BigDecimal#BigDecimal(String)} constructor,
	 * which accepts many different forms - refer there for valid String formats.
	 * <p>
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, or null if null is passed.
	 * @throws ParsingException if unparsable.
	 */
	@Override
	public BigDecimal parse(String sourceValue) throws ParsingException {
		if (sourceValue == null) {
			return null;
		}
		try {
			return new BigDecimal(sourceValue);
		} catch (Exception e) {
			throw new ParsingException("Unable to convert to a BigDecimal numeric value", sourceValue, e);
		}
	}

	@Override
	public BigDecimal cast(Object o) throws RuntimeException {
		return (BigDecimal) o;
	}
}
