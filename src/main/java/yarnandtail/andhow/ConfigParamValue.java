package yarnandtail.andhow;

/**
 * A parameter, including its type, original value, and value (if it has one).
 * 
 * @author eeverman
 */
public interface ConfigParamValue {

	/**
	 * The definition object that defines this parameter.
	 * @return May be null if an unmatched parameter.
	 */
	ConfigPointUsage getConfigPointUsage();
	
	
	/**
	 * The string value that was explicitly set.
	 * 
	 * Note that for flag type parameters, this may be empty,
	 * null, or some version of 'yes', 'true', 'y' or 't' and still equate to 
	 * true.  Use isTrue() for flag values, or use getEffectiveValue for general
	 * cases.
	 * 
	 * Other types of objects will have their toString() returned.
	 * @return 
	 */
	String getExplicitString();
	
	/**
	 * Returns the coerced value that was explicitly set.
	 * If no value was set, this returns null even if there is a default.
	 * @return 
	 */
	Object getExplicitObject();
	
	/**
	 * Returns the coerced true/false value that was explicitly set.
	 * If no value was set, this returns null even if there is a default.
	 * In the case of a flag, if the flag is not set, this will return null.
	 * @return 
	 */
	Boolean getExplicitBoolean();
	
	/**
	 * If the value is explicitly set, this return true if the value is valid.
	 * If not set, this will return true even if the value is required b/c
	 * required is a config-wide setting (it can't be enforced on a single loaded
	 * value b/c it may have been loaded by another loader).
	 * @return 
	 */
	Boolean isValid();
	
	/**
	 * Get the effective value of this parameter, which may differ from getExplicitString.
	 * For name/value pairs, this will return the user assigned String value,
 which is the same as getExplicitString().  For flags, a Boolean is returned w/
 the converted value of the parameter.
	 * 
	 * @return Boolean, String or null (null for name/value pairs which are unset)
	 */
	default Object getObject() {
		
		Object o;
		
		if (isValid()) {
			
			
			if (getConfigPointUsage() != null && getConfigPointUsage().getParamType().isFlag()) {
				o = getExplicitBoolean();
			} else {
				o = getExplicitObject();
			}

		}
		
		if (o == null) {
			o = getConfigPointUsage().getEffectiveDefault();
		}
		
		return o;
	}
	
	/**
	 * Get the effective value of this parameter, which may differ from getExplicitString.
	 * For name/value pairs, this will return the user assigned String value,
 which is the same as getExplicitString().  For flags, a Boolean is returned w/
 the converted value of the parameter.
	 * 
	 * @return A String or null (null for name/value pairs which are unset)
	 */
	default String getString() {
		Object o = getObject();
		return (o != null)?o.toString():null;
	}
	
	/**
	 * True if the contained value (or no value for flags) would be considered
	 * 'true-ish'.
	 * @return 
	 */
	default Boolean isTrue() {
		if (getConfigPointUsage() != null && getConfigPointUsage().getParamType().isFlag()) {
			if (getExplicitString() != null) {
				return ConfigParamUtil.toBoolean(getExplicitString());
			} else {
				return ConfigParamUtil.toBoolean(getConfigPointUsage().getEffectiveDefault());
			}
		} else {
			if (getExplicitObject() != null) {
				return ConfigParamUtil.toBoolean(getExplicitObject());
			} else {
				return ConfigParamUtil.toBoolean(getConfigPointUsage().getEffectiveDefault());
			}
		}
	}
	
	ConfigParamValue toImmutable();
	
}
