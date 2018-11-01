package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Type representation of Java Integer objects.
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
 */
public class IntType extends BaseValueType<Integer> {

	private static final IntType instance = new IntType();
	
	private IntType() {
		super(Integer.class);
	}

    /**
     * @deprecated since 0.4.1. Use {@link #instance()} instead
     *
     * @return An instance of the {@link #IntType()}
     */
	@Deprecated()
	public static IntType get() {
		return instance();
	}

    /**
     * @return An instance of the {@link #IntType()}
     */
	public static IntType instance() {
		return instance;
	}

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
		return (Integer)o;
	}
	
}
