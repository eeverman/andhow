package yarnandtail.andhow.valuetype;

import yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author eeverman
 */
public class FlagType extends BaseValueType<Boolean> {

	private static final FlagType instance = new FlagType();
	
	private FlagType() {
		super(Boolean.class);
	}
	
	public static FlagType get() {
		return instance;
	}
	
	public static FlagType instance() {
		return instance;
	}

	@Override
	public Boolean convert(String sourceValue) throws IllegalArgumentException {

		if (TextUtil.trimToNull(sourceValue) == null) {
			//regardless of trimming, all whitespace is considered == to the flag is present
			return true;
		} else {
			return TextUtil.toBoolean(sourceValue);
		}
	}
	
	@Override
	public Boolean cast(Object o) throws RuntimeException {
		return (Boolean)o;
	}
	
}
