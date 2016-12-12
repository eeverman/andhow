package yarnandtail.andhow.load;

import yarnandtail.andhow.LoaderException;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.AndHow;
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
			ValueMapWithContext existingValues, List<LoaderException> loaderExceptions) {
		
		ArrayList<PropertyValue> values = new ArrayList();
			
		if (cmdLineArgs != null) {
			for (String s : cmdLineArgs) {
				try {
					KVP kvp = KVP.splitKVP(s, AndHow.KVP_DELIMITER);

					attemptToAdd(appConfigDef, values, kvp.getName(), kvp.getValue());

				} catch (ParsingException e) {
					loaderExceptions.add(
							new LoaderException(e, this, null, "Command line parameters")
					);
				}
			}

			values.trimToSize();
		}
		
		return new LoaderValues(this, values);
	}
	
	@Override
	public String getSpecificLoadDescription() {
		return "arguments passed in from the command line at startup";
	}
	
}
