package yarnandtail.andhow.cmdline;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.ConfigParamValue;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderState;

/**
 *
 * @author eeverman
 */
public class CmdLineLoader implements Loader {

	@Override
	public List<ConfigParamValue> load(LoaderState state) {
		
		ArrayList<ConfigParamValue> values = new ArrayList();
		if (state.getCmdLineArgs() != null && state.getCmdLineArgs().length > 0) {
			
			for (String s : state.getCmdLineArgs()) {
				
				
				
				
				
			}
			
			
		}
	}
	
	
}
