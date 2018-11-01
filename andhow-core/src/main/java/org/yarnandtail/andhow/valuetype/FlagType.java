package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.util.TextUtil;

/**
 * Metadata and parsing for a Boolean type which is never null (nix flag behavior).
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
 */
public class FlagType extends BaseValueType<Boolean> {

	private static final FlagType instance = new FlagType();
	
	private FlagType() {
		super(Boolean.class);
	}

    /**
     * @deprecated since 0.4.1. Use {@link #instance()} instead
     *
     * @return An instance of the {@link #FlagType()}
     */
	@Deprecated()
	public static FlagType get() {
		return instance();
	}

    /**
     * @return An instance of the {@link #FlagType()}
     */
	public static FlagType instance() {
		return instance;
	}

	@Override
	public Boolean parse(String sourceValue) throws IllegalArgumentException {

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
