package yarnandtail.andhow;

/**
 *
 * @author eeverman
 */
public interface ConfigPointLoaderValue extends ConfigPointValue {

	/**
	 * The definition object that defines this parameter.
	 * @return May be null if an unmatched parameter.
	 */
	ConfigPointUsage getConfigPointUsage();

	/**
	 * Returns the coerced true/false value that was explicitly set.
	 * If no value was set, this returns null even if there is a default.
	 * In the case of a flag, if the flag is not set, this will return null.
	 * @return
	 */
	Boolean getExplicitBoolean();

	/**
	 * Returns the key value that the user actually used.
	 * Since alias can be used, this provides easier debug traceability.
	 *
	 * @return
	 */
	String getExplicitKey();

	/**
	 * Returns the coerced value that was explicitly set.
	 * If no value was set, this returns null even if there is a default.
	 * @return
	 */
	Object getExplicitObject();

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
	 * Returns true if this value was explicitly by the configuration.
	 * If this returns false, then the default value would be returned by the
	 * getValue methods.
	 *
	 * Need some careful definitions of what set and non-set mean.
	 * Is an empty string param set?  How do you set a param to an empty string?
	 * How about an empty Number type param - would that clear it if it were set
	 * at a lower level?
	 * @return
	 */
	boolean isExplicitValue();

	/**
	 * If the value is explicitly set, this return true if the value is valid.
	 * If not set, this will return true even if the value is required b/c
	 * required is a config-wide setting (it can't be enforced on a single loaded
	 * value b/c it may have been loaded by another loader).
	 * @return
	 */
	Boolean isValid();

	ConfigPointValue toImmutable();
	
}
