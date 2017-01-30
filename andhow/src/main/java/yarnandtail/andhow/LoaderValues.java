package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import yarnandtail.andhow.ProblemList.UnmodifiableProblemList;

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
	
	
	/**
	 * A constructor when there is just a problem to report.
	 * 
	 * @param loader
	 * @param problems 
	 */
	public LoaderValues(Loader loader, Problem problem) {
		ProblemList<Problem> probs = new ProblemList();
		probs.add(problem);
		this.problems = new UnmodifiableProblemList(probs);
		
		this.loader = loader;
		values = EMPTY_PROP_VALUE_LIST;
	}
	
	public LoaderValues(Loader loader, List<PropertyValue> inValues, ProblemList<Problem> problems) {
		
		ProblemList<Problem> myProblems = new ProblemList();
		myProblems.addAll(problems);
		
		if (loader == null) {
			throw new RuntimeException("The loader cannot be null");
		}
		
		this.loader = loader;
		
		
		if (inValues != null && inValues.size() > 0) {
			ArrayList newValues = new ArrayList();
			newValues.addAll(inValues);
			newValues.trimToSize();
			values = Collections.unmodifiableList(newValues);
			
			
			//check for value problems
			for (PropertyValue pv : values) {
				myProblems.addAll(pv.getProblems());
			}
			
		} else {
			values = EMPTY_PROP_VALUE_LIST;
		}
		
		this.problems = new UnmodifiableProblemList(myProblems);
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
