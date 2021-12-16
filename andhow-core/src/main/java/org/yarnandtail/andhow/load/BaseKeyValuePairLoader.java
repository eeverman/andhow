package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.PropertyValue;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;

import java.util.*;

/**
 * An abstract Loader for use where properties are passed as an array of strings, each
 * of the form name[delimiter]value.
 * <p>
 * To implement a non-abstract class, override the
 * {@link #load(PropertyConfigurationInternal, LoaderEnvironment, ValidatedValuesWithContext)}
 * method with a call to {@link #load(PropertyConfigurationInternal, List, String)} and supply your
 * own list of key-value pair strings.  For a simple example, see the
 * {@link org.yarnandtail.andhow.load.std.StdMainStringArgsLoader}.
 * <p>
 * By default, this loader trims incoming values for String type properties and will
 * throw a RuntimeException if passed unrecognized properties.  Both of these behaviours
 * can be changed by modifying the return values of {@link #isTrimmingRequiredForStringValues} and
 * {@link #isUnknownPropertyAProblem}.
 */
public abstract class BaseKeyValuePairLoader extends BaseLoader implements ReadLoader {

	protected boolean unknownPropertyAProblem = true;

	/**
	 * The default delimiter between a key and a value.
	 */
	public static final String KVP_DELIMITER = "=";

	/**
	 * Load from the passed list of keyValuePairs using the passed delimiter.
	 *
	 * @param runtimeDef The definition of all known Properties and naming metadata.
	 * @param keyValuePairs	List of key-value pairs to load from.  Each item in the list
	 * 		is assumed to have a name and value separated by the delimiter.
	 * @param delimiter The separator between the name and value in each key-value entry.
	 * @return The Property values loaded by this loader and/or the problems discovered while
	 * 		attempting to load those Property values.
	 */
	public LoaderValues load(PropertyConfigurationInternal runtimeDef,
			List<String> keyValuePairs, String delimiter) {

		ArrayList<ValidatedValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();

		if (keyValuePairs != null) {
			for (String s : keyValuePairs) {
				try {
					KVP kvp = KVP.splitKVP(s, delimiter);

					attemptToAdd(runtimeDef, values, problems, kvp.getName(), kvp.getValue());

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

	// false is a reasonable default, since its only a special case (the command line loader) that
	// would ever set this true.
	@Override
	public boolean isFlaggable() { return false; }

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


}
