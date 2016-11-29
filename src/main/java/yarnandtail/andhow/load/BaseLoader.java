package yarnandtail.andhow.load;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import yarnandtail.andhow.*;
import yarnandtail.andhow.LoaderValues.PointValue;
import yarnandtail.andhow.appconfig.AppConfigDefinition;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends ConfigPointGroup> getLoaderConfig() {
		return null;
	}
	
	protected void attemptToAdd(AppConfigDefinition appConfigDef, List<LoaderValues.PointValue> values, 
			String key, String value) throws ParsingException {
		
		key = StringUtils.trimToNull(key);
		
		if (key != null && value != null) {
			ConfigPoint cp = appConfigDef.getPoint(key);

			if (cp != null) {
				values.add(new PointValue(cp, cp.convertString(value)));
			} else {
				//need a way to deal w/ these
			}

		}
	}
	
}
