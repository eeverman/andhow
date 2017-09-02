package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

/**
 * Type representation of Java Double objects.
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
 */
public class DblType extends BaseValueType<Double> {

	private static final DblType instance = new DblType();
	
	private DblType() {
		super(Double.class);
	}
	
	public static DblType get() {
		return instance;
	}
	
	public static DblType instance() {
		return instance;
	}

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
		return (Double)o;
	}
	
}
