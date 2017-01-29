package yarnandtail.andhow.load;

import yarnandtail.andhow.internal.LoaderProblem;
import yarnandtail.andhow.ParsingException;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.internal.ConstructionDefinitionMutable;

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
 * For FlgProp properties (flags), the CmdLineLoader will interpret the presence of
 * the property name as setting the property true.
 * 
 * The JVM considers whitespace as breaks between values, however, it can be
 * escaped with a backslash to include it in the value passed to the CmdLineLoader.
 * After the CmdLineLoader receives the value, each individual Property will use
 * its Trimmer to remove whitespace according to its own rules.  Generally that
 * means the QuotedSpacePreservingTrimmer for strings and the TrimToNullTrimmer
 * for everything else.
 * 
 * @author eeverman
 */
public class CmdLineLoader extends BaseLoader {
	
	public CmdLineLoader() {
	}
	
	@Override
	public LoaderValues load(ConstructionDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
		ArrayList<PropertyValue> values = new ArrayList();
		ProblemList<Problem> problems = new ProblemList();
		
		if (cmdLineArgs != null) {
			for (String s : cmdLineArgs) {
				try {
					KVP kvp = KVP.splitKVP(s, AndHow.KVP_DELIMITER);

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
		return "arguments on the command line at startup";
	}
	
	@Override
	public boolean isTrimmingRequiredForStringValues() {
		return true;
	}
	
	@Override
	public boolean isUnrecognizedPropertyNamesConsideredAProblem() {
		return true;
	}
	
}
