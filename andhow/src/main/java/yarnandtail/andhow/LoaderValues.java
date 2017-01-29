package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Properties and values loaded by a Loader.
 * 
 * Only Properties for which a Loader found a value will be in the collection.
 * 
 * @author eeverman
 */
public class LoaderValues implements ValueMap {
	public static final List<PropertyValue> EMPTY_PROP_VALUE_LIST = Collections.emptyList();
	
	private final Loader loader;
	private final List<PropertyValue> values;
	private final ProblemList<Problem> problems;

	public LoaderValues(Loader loader, List<PropertyValue> inValues, ProblemList<Problem> problems) {
		
		
		this.problems = new ProblemList();
		
		if (loader == null) {
			throw new RuntimeException("The loader cannot be null");
		}
		
		this.loader = loader;
		this.problems.addAll(problems);
		
		if (inValues != null && inValues.size() > 0) {
			ArrayList newValues = new ArrayList();
			newValues.addAll(inValues);
			newValues.trimToSize();
			values = Collections.unmodifiableList(newValues);
			
			
			//check for value problems
			for (PropertyValue pv : values) {
				this.problems.addAll(pv.getIssues());
			}
			
		} else {
			values = EMPTY_PROP_VALUE_LIST;
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
	 * @param prop
	 * @return 
	 */
	@Override
	public <T> T getExplicitValue(Property<T> prop) {
		if (prop == null) {
			return null;
		}
		return prop.getValueType().cast(values.stream().filter(pv -> prop.equals(pv.getProperty())).
						findFirst().map(pv -> pv.getValue()).orElse(null)
		);
	}
	
	@Override
	public <T> T getEffectiveValue(Property<T> prop) {
		if (prop == null) {
			return null;
		}
		
		if (isExplicitlySet(prop)) {
			return getExplicitValue(prop);
		} else {
			return prop.getDefaultValue();
		}
	}

	/**
	 * A linear search for the Property in the values loaded by this loader.
	 * @param prop
	 * @return 
	 */
	@Override
	public boolean isExplicitlySet(Property<?> prop) {
		return values.stream().anyMatch(p -> p.getProperty().equals(prop));
	}
	

	/**
	 * Returns loader and value problems, if any.
	 * @return Never null
	 */
	public ProblemList<Problem> getProblems() {
		return problems;
	}
}
