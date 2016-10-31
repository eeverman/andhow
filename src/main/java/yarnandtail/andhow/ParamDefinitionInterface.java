package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Interface for an enum representing command line arguments and/or configuration parameters.
 * @author eeverman
 */
public interface ParamDefinitionInterface {
	
	/**
	 * The class of the enclosing Enum, which must implement the ParamDefinition
	 * interface.
	 * @return 
	 */
	Class<? extends ParamDefinition> getEnumClass();
	
	/**
	 * Name of the entire set of parameters, i.e., for this entire enum and all its values.
	 * @return May be empty, but not null.
	 */
	String getEntireSetName();
	
	/**
	 * Name of the entire set of parameters, i.e., for this entire enum and all its values.
	 * @return May be empty, but not null.
	 */
	String getEntireSetDescription();
	
	/**
	 * The basic type of the parameter
	 * @return 
	 */
	ParamType getParamType();
	
	/**
	 * Long-form option name.  Similar to the full name nix options (dashes not required).
	 * @return 
	 */
	String getExplicitName();
	
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
	 * Alias (short) form.  Similar to nix single letter options (dashes not required).
	 * @return 
	 */
	List<String> getAlias();

	/**
	 * If the parameter is unspecified, the effective value is considered to be this default value.
	 * For name-value pairs, this will be a string.
	 * Flags, which are normally considered to be true if specified, can be
	 * converted to be true _unless_ specified w/ a false value by setting a
	 * Boolean False here.
	 * @return 
	 */
	Object getDefaultValue();
	
	/**
	 * Some parameters may have a defined set of possible values, which
	 * are specified as a list of enums.  This returns that list.
	 * 
	 * @return A non-null list of Enums.
	 */
	List<Enum> getPossibleValueEnums();

}
