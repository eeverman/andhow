package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.internal.StaticPropertyConfigurationInternal;
import java.util.*;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;

/**
 * Intended to reads properties from the command line, but could be used for
 * other sources where properties can be passed as an array of strings, each
 * of the form name=value.
 * 
 * This loader trims incoming values for String type properties using the
 Trimmer of the associated Property.
 This loader considers it a problem to find unrecognized properties
 on the command line and will throw a RuntimeException if that happens.

 For FlgProp properties (flags), the KeyValuePairLoader will interpret the presence of
 the property name as setting the property true.

 The JVM considers whitespace as breaks between values, however, it can be
 escaped with a backslash to include it in the value passed to the KeyValuePairLoader.
 After the KeyValuePairLoader receives the value, each individual Property will use
 its Trimmer to remove whitespace according to its own rules.  Generally that
 means the QuotedSpacePreservingTrimmer for strings and the TrimToNullTrimmer
 for everything else.
 * 
 * @author eeverman
 */
public class KeyValuePairLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;
	
	/**
	 * The default delimiter between a key and a value.
	 */
	public static final String KVP_DELIMITER = "=";

	private List<String> keyValuePairs;

	public KeyValuePairLoader() {
		
	}
	
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
	
	/**
	 * Sets the list of string arguments, each string containing a key-value pair
	 * or just a key for flag type values.
	 * 
	 * Values are copied, so changes to the passed array are not tracked.
	 * 
	 * KVPs are split by KVP.splitKVP using '=' as the delimiter, as defined in
	 * AndHow.KVP_DELIMITER.
	 *
	 * @deprecated Use {@code KeyValuePairLoader.setKeyValuePairs(List<String>)} instead.
	 * @param keyValuePairs
	 */
	@Deprecated
	public void setKeyValuePairs(String... keyValuePairs) {
		this.setKeyValuePairs(keyValuePairs != null ? Arrays.asList(keyValuePairs) : null);
	}
	
	@Override
	public LoaderValues load(StaticPropertyConfigurationInternal appConfigDef, ValidatedValuesWithContext existingValues) {
		
		ArrayList<ValidatedValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();
		
		if (keyValuePairs != null) {
			for (String s : keyValuePairs) {
				try {
					KVP kvp = KVP.splitKVP(s, KVP_DELIMITER);

					attemptToAdd(appConfigDef, values, problems, kvp.getName(), kvp.getValue());

				} catch (ParsingException e) {
					//thrown by KVP.split - this is aloader level problem if we cannot
					//determine even what the Property is.
					problems.add(new LoaderProblem.ParsingLoaderProblem(this, null, null, e));
				}
			}

			values.trimToSize();
		}
		
		return new LoaderValues(this, values, problems);
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "string key value pairs";
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}
	
	@Override
	public String getLoaderType() {
		return "KeyValuePair";
	}
	
	@Override
	public String getLoaderDialect() {
		return null;
	}

	@Override
	public void setUnknownPropertyAProblem(boolean isAProblem) {
		unknownPropertyAProblem = isAProblem;
	}
	
	@Override
	public boolean isUnknownPropertyAProblem() {
		return unknownPropertyAProblem;
	}
	
	@Override
	public void releaseResources() {
		keyValuePairs = null;
	}
}
