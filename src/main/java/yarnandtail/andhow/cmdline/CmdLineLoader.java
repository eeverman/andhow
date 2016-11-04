package yarnandtail.andhow.cmdline;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigPointUsage;
import yarnandtail.andhow.KVP;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderState;
import yarnandtail.andhow.UnparsableKVPException;
import yarnandtail.andhow.ConfigPointValue;

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
					KVP kvp = parseRawArg(s);
					
					if (kvp.getName() != null) {
						ConfigPointUsage cpu = state.getConfigPointUsages().get(kvp.getName());
						
						if (cpu != null) {
							//great!
						} else {
							//need a way to deal w/ these
						}
				
					}
				} catch (UnparsableKVPException e) {
					throw new UnparsableKVPException(e.getReason(), StringUtils.abbreviate(s, 40));
				}
			}
			
			
		}
	}
	
	protected KVP parseRawArg(String arg) {
		arg = StringUtils.trimToNull(arg);
		
		if (arg != null) {
			return new KVP(arg.split(KVP_DELIMITER, 2));
		} else {
			return KVP.NULL_KVP;
		}
	}
	
	
}
