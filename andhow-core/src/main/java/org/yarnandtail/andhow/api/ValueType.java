package org.yarnandtail.andhow.api;

/**
 * The datatype and type conversion handler of a Property.
 * <p>
 * Handles parsing and casting of Strings and Objects to the destination
 * type represented by an instance of a ValueType.
 *
 */
public interface ValueType<T> {
	
	Class<T> getDestinationType();
	
	/**
	 * Attempts to parse the passed String into the destinationType.
	 * 
	 * All trimming (removing whitespace from around a value) should be assumed
	 * to already have happened for the incoming sourceValue by a Trimmer.
	 * It is also assumed that null values are not passed to this method, however, since user Loaders
	 * with custom behaviors are possible, a null should not be considered an error, it just returns
	 * a null.
	 * 
	 * Implementations should be careful to ONLY throw a ParsingException -
	 * Integers and other types may throw other unchecked exceptions when trying
	 * to convert values, which should be handled in this method and rethrown as
	 * a ParsingException.
	 * 
	 * This method and toString(T) should be reversible:  The object generated here
	 * should be toString-able to a String equal to the one passed this method.
	 * 
	 * @param sourceValue
	 * @return null if null, or a value of type T
	 * @throws ParsingException for any type of failure.
	 */
	T parse(String sourceValue) throws ParsingException;
	
	/**
	 * Converts an instance of the destination type to String.
	 * 
	 * For most types, simply calling T.toString() is a good enough implementation.
	 * For dates, times and other more complex types, more detailed conversion may
	 * be needed.
	 * 
	 * This method and parse() should be reversible:  The string generated here
	 * should be parsable to an object equal to the one passed this methond.
	 * 
	 * @param value
	 * @return null if the passed value is null.
	 */
	String toString(T value);
	
	/**
	 * Attempt to cast the object to the generic type T, which is a concrete class in subclasses.
	 * <p>
	 * AndHow uses this internally to cast values known to already be of type T, but were stored
	 * in a generic way.  If used for unknown types that are not castable to T, it will throw
	 * a RuntimeException, though this should never happen unless used by application code.
	 * <p>
	 * @param o Object to cast
	 * @return A cast obeject
	 * @throws RuntimeException 
	 */
	T cast(Object o) throws RuntimeException;
}
