package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

import java.util.*;

/**
 * Intended to read properties from the command line, but could be used for
 * other sources where properties can be passed as an array of strings, each
 * of the form name=value.
 * <p>
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property.
 * This loader considers it a problem to find unrecognized properties
 * and will throw a RuntimeException if that happens.
 * <p>
 * For FlgProp properties (flags), the KeyValuePairLoader will interpret the presence of
 * the property name as setting the property true.
 * <p>
 * The java command considers whitespace delimiter between values, however, it can be
 * escaped with a backslash to include it in the value passed to the KeyValuePairLoader.
 * After the KeyValuePairLoader receives the value, each individual Property will use
 * its Trimmer to remove whitespace according to its own rules.  Generally that
 * means the QuotedSpacePreservingTrimmer for strings and the TrimToNullTrimmer
 * for everything else.
 */
public class KeyValuePairLoader extends BaseKeyValuePairLoader {


	private List<String> keyValuePairs;

	/**
	 * Sets the list of string arguments, each string containing a key-value pair
	 * or just a key for flag type values.
	 *
	 * Values are copied, so changes to the passed list are not tracked.
	 *
	 * KVPs are split by KVP.splitKVP using '=' as the delimiter, as defined in
	 * AndHow.KVP_DELIMITER.
	 *
	 * @param keyValuePairs
	 */
	public void setKeyValuePairs(List<String> keyValuePairs) {
		this.keyValuePairs = keyValuePairs == null ? null : new ArrayList<>(keyValuePairs);
	}

	@Override
	public LoaderValues load(final PropertyConfigurationInternal runtimeDef,
			final LoaderEnvironment environment, final ValidatedValuesWithContext existingValues) {

		return load(runtimeDef, keyValuePairs, KVP_DELIMITER);
	}
	
	@Override
	public void releaseResources() {
		keyValuePairs = null;
	}
}
