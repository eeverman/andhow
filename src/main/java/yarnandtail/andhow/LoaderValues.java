package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ConfigPoints and values loaded by a Loader.
 * 
 * Only ConfigPoints for which a Loader found a value will be in the collection.
 * 
 * @author eeverman
 */
public class LoaderValues implements AppConfigValues {
	public static final List<PointValue> EMPTY_POINT_VALUE_LIST = Collections.emptyList();
	
	private final Loader loader;
	private final List<PointValue> values;
	private boolean problem = false;

	public LoaderValues(Loader loader, List<PointValue> inValues) {
		
		if (loader == null) {
			throw new RuntimeException("The loader cannot be null");
		}
		
		this.loader = loader;
		
		if (inValues != null && inValues.size() > 0) {
			ArrayList newValues = new ArrayList();
			newValues.addAll(inValues);
			newValues.trimToSize();
			values = Collections.unmodifiableList(newValues);
			
			
			//check for problems
			for (PointValue pv : values) {
				if (pv.hasIssues()) {
					problem = true;
					break;
				}
			}
			
		} else {
			values = EMPTY_POINT_VALUE_LIST;
		}
	}

	public Loader getLoader() {
		return loader;
	}

	public List<PointValue> getValues() {
		return values;
	}
	
	

	/**
	 * A linear search for the ConfigPoint in the values loaded by this loader.
	 * 
	 * @param point
	 * @return 
	 */
	@Override
	public <T> T getValue(ConfigPoint<T> point) {
		if (point == null) {
			return null;
		}
		return point.cast(
				values.stream().filter(pv -> point.equals(pv.getPoint())).
						findFirst().map(pv -> pv.getValue()).orElse(null)
		);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		if (point == null) {
			return null;
		}
		
		if (isPointPresent(point)) {
			return getValue(point);
		} else {
			return point.getDefaultValue();
		}
	}

	/**
	 * A linear search for the ConfigPoint in the values loaded by this loader.
	 * @param point
	 * @return 
	 */
	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return values.stream().anyMatch(p -> p.getPoint().equals(point));
	}
	
	/**
	 * Returns true if any value or loader has any sort of issue (invalid value,
	 * parsing error, etc).
	 * 
	 * @return 
	 */
	public boolean hasProblems() {
		return problem;
	}
	
	
}
