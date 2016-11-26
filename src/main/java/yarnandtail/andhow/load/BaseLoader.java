package yarnandtail.andhow.load;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.Loader;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends ConfigPointGroup> getLoaderConfig() {
		return null;
	}
	
	protected void attemptToPut(LoaderState state, Map<ConfigPoint<?>, Object> values, 
			String key, String value) throws ParsingException {
		
		key = StringUtils.trimToNull(key);
		
		if (key != null && value != null) {
			ConfigPoint cp = state.getAppConfigDef().getPoint(key);

			if (cp != null) {
				values.put(cp, cp.convertString(value));
			} else {
				//need a way to deal w/ these
			}

		}
	}
	
}
