package yarnandtail.andhow.load;

import yarnandtail.andhow.ParsingException;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.AndHow;
import yarnandtail.andhow.LoaderProblem;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.internal.RuntimeDefinition;
import yarnandtail.andhow.ValueMapWithContext;

/**
 *
 * @author eeverman
 */
public class CmdLineLoader extends BaseLoader {
	
	public CmdLineLoader() {
	}
	
	@Override
	public LoaderValues load(RuntimeDefinition appConfigDef, List<String> cmdLineArgs,
			ValueMapWithContext existingValues) {
		
		ArrayList<PropertyValue> values = new ArrayList();
		ArrayList<LoaderProblem> problems = new ArrayList(0);
		
		if (cmdLineArgs != null) {
			for (String s : cmdLineArgs) {
				try {
					KVP kvp = KVP.splitKVP(s, AndHow.KVP_DELIMITER);

					attemptToAdd(appConfigDef, values, kvp.getName(), kvp.getValue());

				} catch (ParsingException e) {
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
	
}
