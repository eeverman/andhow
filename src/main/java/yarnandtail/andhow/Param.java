package yarnandtail.andhow;

/**
 * A parameter, including its type, original value, and value (if it has one).
 * 
 * @author eeverman
 */
public interface Param {

	String getOriginalText();

	/**
	 * The definition object that defines this parameter.
	 * @return May be null if an unmatched parameter.
	 */
	ConfigPoint getParamDefinition();
	
	/**
	 * Convenience to access the type, which is part of the definition.
	 * @return May be null.
	 */
	default ParamType getParamType() {
		if (getParamDefinition() != null) {
			return getParamDefinition().getParamType();
		} else {
			return null;
		}
	}

	String getName();
	
	/**
	 * For name-value type parameters, this is the actual value text as found in
	 * the configuration.  
	 * 
	 * Note that for flag type parameters, this may be empty,
	 * null, or some version of 'yes', 'true', 'y' or 't' and still equate to 
	 * true.  Use isTrue() for flag values, or use getEffectiveValue for general
	 * cases.
	 * @return 
	 */
	String getValue();

	Boolean isValid();
	
	/**
	 * Get the effective value of this parameter, which may differ from getValue.
	 * For name/value pairs, this will return the user assigned String value,
	 * which is the same as getValue().  For flags, a Boolean is returned w/
	 * the converted value of the parameter.
	 * 
	 * @return Boolean, String or null (null for name/value pairs which are unset)
	 */
	default Object getEffectiveValue() {
		if (getParamType() != null && getParamType().isFlag()) {
			return isTrue();
		} else {
			return getValue();
		}
	}
	
	/**
	 * Get the effective value of this parameter, which may differ from getValue.
	 * For name/value pairs, this will return the user assigned String value,
	 * which is the same as getValue().  For flags, a Boolean is returned w/
	 * the converted value of the parameter.
	 * 
	 * @return A String or null (null for name/value pairs which are unset)
	 */
	default String getEffectiveValueString() {
		Object o = getEffectiveValue();
		return (o != null)?o.toString():null;
	}
	
	/**
	 * True if the contained value (or no value for flags) would be considered
	 * 'true-ish'.
	 * @return 
	 */
	default Boolean isTrue() {
		if (getParamDefinition() != null) {
			if (getParamType().isFlag()) {
				return getValue() == null || ConfigParamUtil.toBoolean(getValue());
			} else {
				return ConfigParamUtil.toBoolean(getValue());
			}
		} else {
			return ConfigParamUtil.toBoolean(getValue());
		}
	}
	
	Param toImmutable();
	
}
