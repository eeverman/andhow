package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Metadata and parsing for the String type.
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
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

	@Override
	public String parse(String sourceValue) throws ParsingException {
		return sourceValue;		//Too simple!  Trimming is handled separately
	}

	@Override
	public String cast(Object o) throws RuntimeException {
		return (String)o;
	}
	
}
