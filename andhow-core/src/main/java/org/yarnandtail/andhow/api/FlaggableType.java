package org.yarnandtail.andhow.api;

/**
 * A type which is never null and has a value just by the presence of the property without a value,
 * similar to a 'nix flag behavior.
 * <p>
 * A FlaggableType could be used for a Property that is expected to be set true or false via a
 * command line argument just by its presence, e.g.:<br>
 * {@code java MyClass launch}<br>
 * If {@code launch} is an AndHow property using a FlaggableType
 * (i.e. {@link org.yarnandtail.andhow.property.FlagProp}), <em>launch</em> will be considered true
 * just by its presence.
 */
public interface FlaggableType<T> extends ValueType<T> {


	/**
	 * Attempts to parse the passed String into the destination type with flag semantics.
	 * <p>
	 * This method behaves as {@link ValueType#parse(String)}, but with these differences:
	 * <ul>
	 * <li>Null is interpreted as 'property is present' and thus set to a non-null value
	 * The actual value is implementation dependant -
	 * see {@link org.yarnandtail.andhow.property.FlagProp} for an example.</li>
	 * <li>Null is never returned, since the intent to give null sourceValue values a
	 * non-null destination value.</li>
	 * <li>It is not reversible with {@link #toString(Object)}</li>
	 * </ul>
	 * <p>
	 * This method should only be called by loaders that support Flags.  For instance, loading a
	 * non-null value from the properties file entry {@code 'name = '} would be unexpected,
	 * so a properties file loader should use the 'parse' method.  However, a command line loader
	 * would be expected to support flags, so could call this method instead of 'parse'.
	 * <p>
	 * This method may still throw a {@link ParsingException}, since non-null values may still be parsed.
	 * As with {@link #parse(String)}, all trimming (removing whitespace from around a value) should
	 * be assumed to already have happened for the incoming sourceValue.
	 * <p>
	 * Implementations should be careful to ONLY throw a ParsingException -
	 * Integers and other types may throw other unchecked exceptions when trying to convert values,
	 * which should be handled in this method and rethrown as a ParsingException.
	 * <p>
	 * @param sourceValue
	 * @return A non-null value.
	 * @throws ParsingException for any type of failure.
	 */
	T parseFlag(String sourceValue) throws ParsingException;

}
