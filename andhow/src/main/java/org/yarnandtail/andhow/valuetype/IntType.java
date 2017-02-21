package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.ParsingException;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Type representation of Java Integer objects.
 * 
 * @author eeverman
 */
public class IntType extends BaseValueType<Integer> {

	private static final IntType instance = new IntType();
	
	private IntType() {
		super(Integer.class);
	}
	
	public static IntType get() {
		return instance;
	}
	
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
