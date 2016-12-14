package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.TextUtil;

/**
 *
 * @author eeverman
 */
public class IntType extends BaseValueType<Integer> {

	private static final IntType instance = new IntType();
	
	private IntType() {
		super(Integer.class, false, true, TrimStyle.TO_NULL);
	}
	
	public static IntType get() {
		return instance;
	}
	
	public static IntType instance() {
		return instance;
	}

	@Override
	public Integer convert(String sourceValue) throws IllegalArgumentException {
		
		String effVal = TextUtil.trimToNull(sourceValue);
		
		if (effVal != null) {
				
			return Integer.parseInt(effVal);

		} else {
			return null;
		}
	}
	
	@Override
	public Integer cast(Object o) throws RuntimeException {
		return (Integer)o;
	}
	
}
