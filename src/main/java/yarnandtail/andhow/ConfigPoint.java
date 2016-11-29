package yarnandtail.andhow;

import yarnandtail.andhow.load.ParsingException;
import java.util.Collections;
import yarnandtail.andhow.valuetype.ValueType;
import java.util.List;

/**
 * Interface for an enum representing command line arguments and/or configuration parameters.
 * @author eeverman
 */
public interface ConfigPoint<T> {
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	public static final List<String> EMPTY_STRING_LIST = Collections.emptyList();
	

	T getValue(AppConfigValues values);
	
	T getValue();
	
	T getExplicitValue(AppConfigValues values);
	
	T getExplicitValue();
	
	T getDefaultValue();
	
	boolean isRequired();
	
	/**
	 * Statelessly convert a String to the target type.
	 * Used during loading to statelessly build values which will later be
	 * available (statefully) from the Config point.
	 * @param str
	 * @return
	 * @throws ParsingException 
	 */
	T convertString(String str) throws ParsingException;
	
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
	 * Attempt to cast the passed object to the Value type of this CP.
	 * AndHow should never attempt to do this for values it knows cannot be converted
	 * so this can throw a RuntimeException if used inappropriately.
	 * @param o
	 * @return
	 * @throws RuntimeException 
	 */
	T cast(Object o) throws RuntimeException;
	


}
