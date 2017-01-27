package yarnandtail.andhow;

import java.util.List;

/**
 * Represents a configuration point of an application.
 * 
 * Implementations are typed so that they return String, Integer, Boolean, etc.,
 rather than just configuration strings.
 
 Implementor's Notes:
 See IntProp as a best example of how to implement a new Property.
 The reason for creating a new implementation would be to handle a new type, 
 such as a DateTime type.
 
 <ul>
 * <li>All implementations can use the PropertyBase as a base class.  Most
 * methods are already present w/ just a few methods left to implement.
 * <li>All implementations should have a static builder() method that returns
 * a builder capable of building an instance.  by convention, builders are
 * inner classes of their associated Property.  The PropertyBuilderBase is an
 * easy base class to extend that provide nearly all needed functionality.
 * <li>Builders should provide easy access to Validators for their appropriate type.
 * For instance the StrProp has a value type of String and has an
 * associated StringRegex Validator.  By convention, the builder methods to add
 * validators use the 'must' terminology, as in:  mustMatchRegex(String regex),
 * or mustStartWith(String prefix).
 * </ul>
 * @author eeverman
 */
public interface Property<T> {

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
	 * The Trimmer responsible for trimming String values before they are converted
	 * to the appropriate property type.
	 * 
	 * @return 
	 */
	Trimmer getTrimmer();
	
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
	 * The list of Aliases requested by this property in its declaration.
	 * 
	 * CAUTION:  This is NOT necessarily the list of Alias actually available
	 * for this property - use AndHow.getAlias() for that.
	 * 
	 * Since Properties from unrelated libraries can be
	 * used in the same application, the main AndHow application level configuration
	 * must be able to disable aliases that conflict (i.e., two properties are
	 * aliased as 'my-prop'.
	 * 
	 * Note that the returned aliases may be coalesced.  For instance, if a property
	 * is build as:
	 * <code>IntProp MyProp = IntProp.builder().inAlias("Bob").outAlias("Bob").build();</code>
	 * getConfiguredAliases() may return a single Alias object w/ the name "Bob",
	 * marked as being both both in and out.
	 * 
	 * @return A list of Alias assigned to this Property in the property
	 *	declaration or an empty list if there are none.
	 */
	List<Name> getRequestedAliases();
	
	
	/**
	 * Added details that might be shown if the user requests help.
	 * Assume that the short description is already shown.
	 * @return 
	 */
	String getHelpText();

}
