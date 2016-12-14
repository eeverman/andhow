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
public class LoaderValues implements ValueMap {
	public static final List<PropertyValue> EMPTY_POINT_VALUE_LIST = Collections.emptyList();
	
	private final Loader loader;
	private final List<PropertyValue> values;
	private boolean problem = false;

	public LoaderValues(Loader loader, List<PropertyValue> inValues) {
		
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
			for (PropertyValue pv : values) {
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

	public List<PropertyValue> getValues() {
		return values;
	}
	
	

	/**
	 * A linear search for the Property in the values loaded by this loader.
	 * 
	 * @param point
	 * @return 
	 */
	@Override
	public <T> T getExplicitValue(Property<T> point) {
		if (point == null) {
			return null;
		}
		return point.getValueType().cast(
				values.stream().filter(pv -> point.equals(pv.getProperty())).
						findFirst().map(pv -> pv.getValue()).orElse(null)
		);
	}
	
	@Override
	public <T> T getEffectiveValue(Property<T> point) {
		if (point == null) {
			return null;
		}
		
		if (isExplicitlySet(point)) {
			return getExplicitValue(point);
		} else {
			return point.getDefaultValue();
		}
	}

	/**
	 * A linear search for the Property in the values loaded by this loader.
	 * @param point
	 * @return 
	 */
	@Override
	public boolean isExplicitlySet(Property<?> point) {
		return values.stream().anyMatch(p -> p.getProperty().equals(point));
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
