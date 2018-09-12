package org.yarnandtail.andhow.api;

import org.yarnandtail.andhow.internal.ValueProblem;

import static org.yarnandtail.andhow.api.ProblemList.EMPTY_PROBLEM_LIST;

/**
 * Simple class to bundle a Property, its value and any associated problems with the Property.
 * 
 * @author eeverman
 */
public class ValidatedValue<T> {

	private final Property<T> property;
	private final T value;
	private ProblemList<ValueProblem> problems;


	/**
	 * New instance w/o Problems
	 * Problems can be added later.
	 * 
	 * @param prop
	 * @param value 
	 */
	public ValidatedValue(Property<T> prop, T value) {
		this.property = prop;
		this.value = value;
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
