package yarnandtail.andhow.staticparam;

import yarnandtail.andhow.*;
import java.util.List;
import yarnandtail.andhow.valuetype.ValueType;

/**
 * Interface for an enum representing command line arguments and/or configuration parameters.
 * @author eeverman
 */
public interface ConfigPoint<T> {
	
	
	/**
	 * Name of the entire set of parameters, i.e., for this entire enum and all its values.
	 * @return May be empty, but not null.
	 */
	String getGroupDescription();
	
	/**
	 * Name of the entire set of parameters, i.e., for this entire enum and all its values.
	 * @return May be empty, but not null.
	 */
	String getEntireSetDescription();
	
	/**
	 * The basic type of the configuration point:  Flag, name/value, multi=value.
	 * @return 
	 */
	ConfigPointType getPointType();
	
	/**
	 * The type of the value (String, Number, Integer, etc).
	 * For ConfigPointTypes that allow multiple values, an array of values of
	 * the specified type can be fetched.
	 * @return 
	 */
	ValueType getValueType();
	
	/**
	 * If true, indicates that this parameter is not intended to be exposed.
	 * Creates a fixed value, documented parameter that has a hardcoded value
 set via the default value of the ConfigPointDef.
 
 These values can take advantage of expressions in the default value.
	 * @return 
	 */
	boolean isPrivate();
	
	/**
	 * Long-form option name as explicitly set during in the constructor.
	 * This may be null, in which case the implicit base name is just the name of
	 * the enum itself, typically of the form: SOME_CONFIG_PARAM_NAME.
	 * 
	 * This is the <i>base</i> name: the NamingStrategy is applied in the usage
 of the ConfigPointDef to determine its actual effective name.
   *
	 * @return The explicitly set name, or null.
	 */
	String getExplicitBaseName();
	
	/**
	 * A short sentence description.
	 * @return 
	 */
	String getShortDescription();
	
	/**
	 * Added details that might be shown if the user requests help.
	 * Assume that the short description is already shown.
	 * @return 
	 */
	String getHelpText();

	
	/**
	 * Alias (short) form.  Similar to 'nix single letter options (dashes not required).
	 * These are the <i>base</i> aliases: the NamingStrategy is applied in the usage
 of the ConfigPointDef to determine actual alias names.
	 * 
	 * @return The list of aliases or an empty list if there are none.
	 */
	List<String> getBaseAliases();

	/**
	 * If the parameter is unspecified, this value is used instead.
	 * Any type of Object is possible to store as a default.
 For name-value pairs, this will typically be a string.
 
 This is the default as specified in the ConfigPointDef itself.  At the
 application level, the effective default can be overridden to provide
 application specific default values.
	 * @return 
	 */
	T getBaseDefaultValue();
	
	/**
	 * Some parameters may have a defined set of possible values, which
	 * are specified as a list of enums.  This returns that list.
	 * 
	 * @return A non-null list of Enums.
	 */
	//List<Enum> getPossibleValueEnums();

}
