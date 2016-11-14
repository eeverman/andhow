package yarnandtail.andhow.staticparam.load;

import java.util.HashMap;
import java.util.Map;
import yarnandtail.andhow.ParsingException;
import yarnandtail.andhow.staticparam.ConfigPoint;
//import yarnandtail.andhow.*;

/**
 *
 * @author eeverman
 */
public class CmdLineLoader implements Loader {

	public static final String KVP_DELIMITER = "=";
	
	@Override
	public Map<ConfigPoint, String> load(LoaderState state) {
		
		Map<ConfigPoint, String> values = new HashMap();
		if (state.getCmdLineArgs() != null && state.getCmdLineArgs().length > 0) {
			
			for (String s : state.getCmdLineArgs()) {
				try {
					KVP kvp = KVP.splitKVP(s, KVP_DELIMITER);
					
					if (kvp.getName() != null) {
						ConfigPoint cp = state.getRegisteredConfigPoints().get(kvp.getName());
						
						if (cp != null) {
							values.put(cp, kvp.getValue());
							
							//Don't do this - no reason to force an error until
							//all values are supplied - maybe missing values will
							//be supplied
							//cp.convertString(kvp.getValue());	//try converting to force error
						} else {
							//need a way to deal w/ these
						}
				
					}
				} catch (ParsingException e) {
					state.getLoaderExceptions().add(
							new LoaderException(e, this, null, "Command line parameters")
					);
				}
			}
			
			
		}
		
		return values;
	}
	
	
	
}
