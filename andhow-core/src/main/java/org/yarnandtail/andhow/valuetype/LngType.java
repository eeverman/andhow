package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Type representation of Java Long objects.
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
 */
public class LngType extends BaseValueType<Long> {

	private static final LngType instance = new LngType();
	
	private LngType() {
		super(Long.class);
	}

    /**
     * @deprecated since 0.4.1. Use {@link #instance()} instead
     *
     * @return An instance of the {@link #LngType()}
     */
	@Deprecated()
	public static LngType get() {
		return instance();
	}

    /**
     * @return An instance of the {@link #LngType()}
     */
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
