package yarnandtail.andhow;

import java.util.List;

/**
 * A parameter, including its type, original value, and value (if it has one).
 * 
 * @author eeverman
 */
public interface ConfigPointValue {

	/**
	 * The config point defining this value.
	 * 
	 * @return Shouldn't be null - There should be an undefined Param instance instead.
	 */
	ConfigPointDef getConfigPoint();
	
	/**
	 * Get the effective value of this parameter, which may differ from getExplicitString.
	 * For name/value pairs, this will return the user assigned String value,
 which is the same as getExplicitString().  For flags, a Boolean is returned w/
 the converted value of the parameter.
	 * 
	 * @return Boolean, String or null (null for name/value pairs which are unset)
	 */
	Object getObject();
	
	/**
	 * Get the effective value of this parameter, which may differ from getExplicitString.
	 * For name/value pairs, this will return the user assigned String value,
 which is the same as getExplicitString().  For flags, a Boolean is returned w/
 the converted value of the parameter.
	 * 
	 * @return A String or null (null for name/value pairs which are unset)
	 */
	String getString();
	
	/**
	 * True if the contained value (or no value for flags) would be considered
	 * 'true-ish'.
	 * @return 
	 */
	Boolean isTrue();
	
	
}
