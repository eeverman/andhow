package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.util.TextUtil;

/**
 * Metadata and parsing for the Boolean type.
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
 */
public class BolType extends BaseValueType<Boolean> {

	private static final BolType instance = new BolType();
	
	private BolType() {
		super(Boolean.class);
	}
	
	public static BolType get() {
		return instance;
	}
	
	public static BolType instance() {
		return instance;
	}

	@Override
	public Boolean parse(String sourceValue) throws IllegalArgumentException {

		if (TextUtil.trimToNull(sourceValue) != null) {
			return TextUtil.toBoolean(sourceValue);
		} else {
			return null;
		}
	}
	
	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}
	
}
