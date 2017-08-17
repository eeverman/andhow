package org.yarnandtail.andhow.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Properties and values loaded by a single Loader.
 * 
 * Only Properties for which a Loader found a value will be in the collection.
 * 
 * @author eeverman
 */
public class LoaderValues implements PropertyValues {
	public static final List<PropertyValue> EMPTY_PROP_VALUE_LIST = Collections.emptyList();
	
	private final Loader loader;
	private final List<PropertyValue> values;
	private final ProblemList<Problem> problems;
	
	
	/**
	 * A constructor when there is just a problem to report.
	 * 
	 * @param loader
	 * @param problem
	 */
	public LoaderValues(Loader loader, Problem problem) {
		ProblemList<Problem> probs = new ProblemList();
		probs.add(problem);
		this.problems = new ProblemList.UnmodifiableProblemList(probs);
		
		this.loader = loader;
		values = EMPTY_PROP_VALUE_LIST;
	}
	
	/**
	 * A constructor when there is no problem, but there were no values loaded.
	 * 
	 * @param loader
	 */
	public LoaderValues(Loader loader) {
		this.problems = new ProblemList.UnmodifiableProblemList();
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
		
		this.problems = new ProblemList.UnmodifiableProblemList(myProblems);
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
	public <T> T getValue(Property<T> prop) {
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
		return getExplicitValue(prop) != null;
	}
	

	/**
	 * Returns loader and value problems, if any.
	 * @return Never null
	 */
	public ProblemList<Problem> getProblems() {
		return problems;
	}
}
