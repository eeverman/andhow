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

	public LoaderValues(Loader loader, List<PointValue> inValues) {
		this.loader = loader;
		
		if (inValues != null && inValues.size() > 0) {
			ArrayList newValues = new ArrayList();
			newValues.addAll(inValues);
			newValues.trimToSize();
			values = Collections.unmodifiableList(newValues);
		} else {
			values = EMPTY_POINT_VALUE_LIST;
		}
	}

	/**
	 * A linear search for the ConfigPoint in the values loaded by this loader.
	 * 
	 * @param point
	 * @return 
	 */
	@Override
	public Object getValue(ConfigPoint<?> point) {
		if (point == null) {
			return null;
		}
		return values.stream().filter((pv) -> point.equals(pv.point)).findFirst().map((pv) -> pv.value).orElse(null);
	}

	/**
	 * A linear search for the ConfigPoint in the values loaded by this loader.
	 * @param point
	 * @return 
	 */
	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return values.stream().anyMatch((p) -> p.point.equals(point));
	}
	
	public static class PointValue {
		private ConfigPoint<?> point;
		private Object value;
		
		PointValue(ConfigPoint<?> point, Object value) {
			this.point = point;
			this.value = value;
		}

		public ConfigPoint<?> getPoint() {
			return point;
		}

		public Object getValue() {
			return value;
		}
	}
	
}
