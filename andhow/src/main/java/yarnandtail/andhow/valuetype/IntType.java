package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.ParsingException;
import yarnandtail.andhow.util.TextUtil;

/**
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
	public Integer convert(String sourceValue) throws ParsingException {
		
		String effVal = TextUtil.trimToNull(sourceValue);
		
		if (effVal != null) {
			
			try {
				return Integer.parseInt(effVal);
			} catch (Exception e) {
				throw new ParsingException("Unable to convert to an integer", effVal, e);
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
