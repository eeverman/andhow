package yarnandtail.andhow;

import java.util.Collections;
import java.util.List;

/**
 * Represents a configuration point of an application.
 * 
 * Implementations are typed so that they return String, Integer, Boolean, etc.,
 rather than just configuration strings.
 
 Implementor's Notes:
 See IntConfigPoint as a best example of how to implement a new Property.
 The reason for creating a new implementation would be to handle a new type, 
 such as a DateTime type.
 
 <ul>
 * <li>All implementations can use the ConfigPointBase as a base class.  Most
 * methods are already present w/ just a few methods left to implement.
 * <li>All implementations should have a static builder() method that returns
 a builder capable of building an instance.  by convention, builders are
 inner classes of their associated Property.  The ConfigPointBuilder is an
 easy base class to extend that provide nearly all needed functionality.
 <li>Builders should provide easy access to Validators for their appropriate type.
 * For instance the StringConfigPoint has a value type of String and has an
 * associated StringRegex Validator.  By convention, the builder methods to add
 * validators use the 'must' terminology, as in:  mustMatchRegex(String regex),
 * or mustStartWith(String prefix).
 * </ul>
 * @author eeverman
 */
public interface Property<T> {
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	public static final List<String> EMPTY_STRING_LIST = Collections.emptyList();
	

	T getValue(ValueMap values);
	
	T getValue();
	
	T getExplicitValue(ValueMap values);
	
	T getExplicitValue();
	
	T getDefaultValue();
	
	boolean isRequired();
	
	/**
	 * The basic type of the property:  Flag, name/value, multi=value.
	 * @return 
	 */
	PropertyType getPropertyType();
	
	/**
	 * The type of the value (String, Number, Integer, etc).
	 * For Properties that allow multiple values (not yet implemented), an array
	 * of values of the specified type can be fetched.
	 * @return 
	 */
	ValueType<T> getValueType();
	
	/**
	 * A short sentence description.
	 * @return 
	 */
	String getShortDescription();
	
	/**
	 * List of validators to validate the converted value.
	 * @return 
	 */
	List<Validator<T>> getValidators();
	
	/**
	 * Added details that might be shown if the user requests help.
	 * Assume that the short description is already shown.
	 * @return 
	 */
	String getHelpText();

	
	/**
	 * Alias (short) form.  Similar to 'nix single letter options (dashes not required).
	 * These are the <i>base</i> aliases: the NamingStrategy is applied in the usage
	 * of the Property to determine actual alias names.
	 * 
	 * @return The list of aliases or an empty list if there are none.
	 */
	List<String> getBaseAliases();

}
