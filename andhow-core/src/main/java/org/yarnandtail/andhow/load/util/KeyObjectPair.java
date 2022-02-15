package org.yarnandtail.andhow.load.util;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * Key-Object Pair
 * Contains a String name and an Object value.
 * Names are always trimmed (leading and trailing spaces removed) because they are
 * expected to match up to AndHow Property canonical names or alias, which don't includes spaces.
 * After trimming, names must be non-empty and non-null.
 */
public class KeyObjectPair {

	private String name;
	private Object value;

	/**
	 * Construct an instance with a null value.
	 * @param name A name that will be trimmed to remove all leading and trailing spaces.
	 * @throws ParsingException If the name is empty after trimming.
	 */
	public KeyObjectPair(final String name) throws ParsingException {
		this(name, null);
	}

	/**
	 * New instance with name and value.
	 * @param name A name that will be trimmed to remove all leading and trailing spaces.
	 * @param value The value, which will not be trimmed or modified.
	 * @throws ParsingException If the name is empty after trimming.
	 */
	public KeyObjectPair(final String name, final Object value) throws ParsingException {
		String cleanName = TextUtil.trimToNull(name);

		if (cleanName == null) {
			throw new ParsingException("The key (parameter name) cannot be empty or null", name);
		}

		this.name = cleanName;
		this.value = value;
	}

	/**
	 * The KeyObjectPair name, which has been trimmed to remove leading and trailing spaces.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * The value exactly as passed in the constructor, which may be null.
	 * @return The value as set.
	 */
	public Object getValue() {
		return value;
	}

	//Hashcode and Equals
	//Normally a class like this would override these methods, but AndHow never needs to compare
	//equality of this class, or if it does, is only concerned with the name and equality based
	//only on name seems 'broken'.

	/**
	 * String representation:  <i>name</i> : "<i>value</i>"
	 *
	 * If the value is null, the string <i>[null]</i> is used.
	 * @return A formatted string version of the instance.
	 */
	@Override
	public String toString() {
		return name + " : " + "\"" + (value!=null?value.toString():"[null]") + "\"";
	}
}
