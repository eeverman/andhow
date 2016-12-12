package yarnandtail.andhow.load;

import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.PointValueProblem;
import yarnandtail.andhow.internal.RuntimeDefinition;

/**
 *
 * @author eeverman
 */
public abstract class BaseLoader implements Loader {
	
	
	@Override
	public Class<? extends ConfigPointGroup> getLoaderConfig() {
		return null;
	}
	
	protected void attemptToAdd(RuntimeDefinition appConfigDef, List<PointValue> values, 
			String key, String strValue) throws ParsingException {
		
		key = TextUtil.trimToNull(key);
		
		if (key != null && strValue != null) {
			ConfigPoint cp = appConfigDef.getPoint(key);

			if (cp != null) {
				
				PointValue pv = createValue(cp, strValue);
				values.add(pv);
				
			} else {
				//need a way to deal w/ these
			}

		}
	}
	
	protected <T> PointValue createValue(ConfigPoint<T> point, String strValue) throws ParsingException {
		
		T value = point.convertString(strValue);
		
		ArrayList<PointValueProblem> issues = new ArrayList();

		for (Validator<T> v : point.getValidators()) {
			if (! v.isValid(value)) {
				issues.add(new PointValueProblem(this, point, value, v));
			}
		}
		
		return new PointValue(point, value, issues);
	}
	
}
