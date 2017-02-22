package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.ParsingException;

/**
 * Type representation of Java Long objects.
 * 
 * @author eeverman
 */
public class LngType extends BaseValueType<Long> {

	private static final LngType instance = new LngType();
	
	private LngType() {
		super(Long.class);
	}
	
	public static LngType get() {
		return instance;
	}
	
	public static LngType instance() {
		return instance;
	}

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
		return (Long)o;
	}
	
}
