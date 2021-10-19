package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Metadata and parsing for configuration Properties of the {@link String} type.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely share the same instance.
 */
public class StrType extends BaseValueType<String> {

	private static final StrType instance = new StrType();
	
	private StrType() {
		super(String.class);
	}

	/**
	 * @return An instance of the {@link #StrType()}
	 */
	public static StrType instance() {
		return instance;
	}

	/**
	 * To parse the String type, the passed String is simply returned.
	 * @param sourceValue The source string, which should already be trimmed and may be null
	 * @return The parsed value, which is simply the passed value.
	 * @throws ParsingException (never thrown)
	 */
	@Override
	public String parse(String sourceValue) throws ParsingException {
		return sourceValue;		//Too simple!  Trimming is handled separately
	}

	@Override
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
