package yarnandtail.andhow;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eeverman
 */
public class ConfigPointValueWriter implements ConfigPointValue {
	
	

	private ConfigPointUsage configPointUsage;
	private String explicitValue;
	private String explicitKey;
	
	
	


	/**
	 * The definition object that defines this parameter.
	 * @return May be null if an unmatched parameter.
	 */
	public ConfigPointUsage getConfigPointUsage() {
		return configPointUsage;
	}

	/**
	 * Returns the coerced true/false value that was explicitly set.
	 * If no value was set, this returns null even if there is a default.
	 * In the case of a flag, if the flag is not set, this will return null.
	 * @return
	 */
	public Boolean getExplicitBoolean() {
		if (explicitValue != null) {
			
			if (configPointUsage != null) {
				
				Object convertedObject = null;
				
				if (configPointUsage.getValueType().isConvertable(explicitValue)) {
					try {
						convertedObject = configPointUsage.getValueType().convert(explicitValue);
					} catch (ParsingException ex) {
						//Ignore in the writter, since this only happens during loading
					}
				}
				
				if (convertedObject == null) {
					return null;
				} else if (convertedObject instanceof Boolean) {
					return (Boolean)convertedObject;
				} else {
					String s = convertedObject.toString();
					
					if (s == null) {
						return false;	//There *is* a value, but w/o a toString val, it must be considered false
					} else {
						return ConfigParamUtil.toBoolean(s);
					}
				}
			} else {
				return ConfigParamUtil.toBoolean(explicitValue);
			}
				
		} else {
			return null;
		}
	}
	

	/**
	 * Returns the key value that the user actually used.
	 * Since alias can be used, this provides easier debug traceability.
	 *
	 * @return
	 */
	public String getExplicitKey() {
		return explicitKey;
	}

	/**
	 * Returns the coerced value that was explicitly set.
	 * If no value was set, this returns null even if there is a default.
	 * @return
	 */
	public Object getExplicitObject();

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
	public String getExplicitString();

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
	public boolean isExplicitValue();

	/**
	 * If the value is explicitly set, this return true if the value is valid.
	 * If not set, this will return true even if the value is required b/c
	 * required is a config-wide setting (it can't be enforced on a single loaded
	 * value b/c it may have been loaded by another loader).
	 * @return
	 */
	public Boolean isValid();

	public ConfigPointValue toConfigPointValue();
	
}
