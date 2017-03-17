package org.yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.internal.LoaderProblem;

/**
 * Intended to reads properties from the command line, but could be used for
 * other sources where properties can be passed as an array of strings, each
 * of the form name=value.
 * 
 * This loader trims incoming values for String type properties using the
 * Trimmer of the associated Property.
 * This loader considers it a problem to find unrecognized properties
 * on the command line and will throw a RuntimeException if that happens.
 *
 * For FlgProp properties (flags), the StringArgumentLoader will interpret the presence of
 * the property name as setting the property true.
 *
 * The JVM considers whitespace as breaks between values, however, it can be
 * escaped with a backslash to include it in the value passed to the StringArgumentLoader.
 * After the StringArgumentLoader receives the value, each individual Property will use
 * its Trimmer to remove whitespace according to its own rules.  Generally that
 * means the QuotedSpacePreservingTrimmer for strings and the TrimToNullTrimmer
 * for everything else.
 * 
 * @author eeverman
 */
public class StringArgumentLoader extends BaseLoader {

	/**
	 * The default delimiter between a key and a value.
	 */
	public static final String KVP_DELIMITER = "=";
	
	private final List<String> cmdLineArgs;
	
	/**
	 * Construct using a list of Strings, each string containing a key-value pair.
	 * 
	 * KVPs are split by KVP.splitKVP using '=' as the delimiter, as defined in
	 * AndHow.KVP_DELIMITER.
	 * 
	 * @param inCmdLineArgs 
	 */
	public StringArgumentLoader(List<String> inCmdLineArgs) {
		if (inCmdLineArgs != null && inCmdLineArgs.size() > 0) {
			cmdLineArgs = new ArrayList();
			cmdLineArgs.addAll(inCmdLineArgs);
		} else {
			cmdLineArgs = Collections.emptyList();
		}
	}
	
	/**
	 * Construct using an array of Strings, each string containing a key-value pair.
	 * 
	 * KVPs are split by KVP.splitKVP using '=' as the delimiter, as defined in
	 * AndHow.KVP_DELIMITER.
	 * 
	 * @param inCmdLineArgs 
	 */
	public StringArgumentLoader(String[] inCmdLineArgs) {
		if (inCmdLineArgs != null && inCmdLineArgs.length > 0) {
			cmdLineArgs = Arrays.asList(inCmdLineArgs);
		} else {
			cmdLineArgs = Collections.emptyList();
		}
	}
	
	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, ValueMapWithContext existingValues) {
		
		ArrayList<PropertyValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();
		
		if (cmdLineArgs != null) {
			for (String s : cmdLineArgs) {
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
		return "string arguments from the command line at startup";
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}
	
	@Override
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return true;
	}
	
	
	@Override
	public String getLoaderType() {
		return "KeyValuePair";
	}
	
	@Override
	public String getLoaderDialect() {
		return "FromSingleString";
	}
}
