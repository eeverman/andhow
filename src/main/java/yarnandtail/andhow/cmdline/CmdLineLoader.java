package yarnandtail.andhow.cmdline;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.*;

/**
 *
 * @author eeverman
 */
public class CmdLineLoader implements Loader {

	public static final String KVP_DELIMITER = "=";
	
	@Override
	public List<ConfigPointValue> load(LoaderState state) {
		
		ArrayList<ConfigPointValue> values = new ArrayList();
		if (state.getCmdLineArgs() != null && state.getCmdLineArgs().length > 0) {
			
			for (String s : state.getCmdLineArgs()) {
				try {
					KVP kvp = KVP.splitKVP(s, KVP_DELIMITER);
					
					if (kvp.getName() != null) {
						ConfigPointUsage cpu = state.getConfigPointUsages().get(kvp.getName());
						
						if (cpu != null) {
							//great!
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
