package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.ValueProblem;

import static org.yarnandtail.andhow.api.ProblemList.EMPTY_PROBLEM_LIST;

/**
 * Simple class to bundle a Property, its value and any associated problems with the Property.
 * 
 * @author eeverman
 */
public class PropertyValue<T> {

	private final Property<T> property;
	private final T value;
	private ProblemList<ValueProblem> problems;

	@Override
	public boolean equals(Object obj) {
		boolean basicPropsEq = false;
		
		if (obj instanceof PropertyValue) {
			PropertyValue other = (PropertyValue)obj;
			if (property == other.property) {
				if (value != null && other.value != null) {
					basicPropsEq = (value.equals(other.value));
				} else if (value == null && other.value == null) {
					basicPropsEq = true;
				}
			} 
			
			//Ignore the transient Problem state
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		if (property != null) hash*=property.hashCode();
		if (value != null) hash*=value.hashCode();
		
		//Ignore the transient Problem state
		
		return hash;
	}

	/**
	 * New instance w/o Problems
	 * Problems can be added later.
	 * 
	 * @param prop
	 * @param value 
	 */
	public PropertyValue(Property<T> prop, T value) {
		this.property = prop;
		this.value = value;
	}
	
	/**
	 * Construct w/ a known set of problems.
	 * 
	 * The problem list is taken by reference if it is non-empty.  Callers should
	 * build up a fresh list of Problems and not reuse lists.
	 * 
	 * @param prop
	 * @param value
	 * @param inIssues 
	 */
	public PropertyValue(Property<T> prop, T value, ProblemList<ValueProblem> inIssues) {
		this.property = prop;
		this.value = value;
		
		if (inIssues != null && inIssues.size() > 0) {
			problems = inIssues;
		}
	}

	public Property<T> getProperty() {
		return property;
	}

	public T getValue() {
		return value;
	}
	
	public void addProblem(ValueProblem problem) {
		if (problems == null) {
			problems = new ProblemList();
		}
		problems.add(problem);
	}

	public boolean hasProblems() {
		return problems != null && problems.size() > 0;
	}
	
	/**
	 * Returns an unmodifiable list of Problems.
	 * 
	 * @return 
	 */
	public ProblemList<Problem> getProblems() {
		if (problems != null) {
			return new ProblemList.UnmodifiableProblemList(problems);
		} else {
			return EMPTY_PROBLEM_LIST;
		}
		
	}
	
}
