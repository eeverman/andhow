package yarnandtail.andhow;

import yarnandtail.andhow.ProblemList.UnmodifiableProblemList;

import static yarnandtail.andhow.ProblemList.EMPTY_PROBLEM_LIST;

/**
 * Simple class to bundle a Property, its value and any associated problems with the Property.
 * 
 * @author eeverman
 */
public class PropertyValue {

	private Property<?> property;
	private Object value;
	private ProblemList<Problem> problems;

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
			
			if (basicPropsEq) {
				//Don't do a deep dive into the problems - if there are no problems,
				//great, but if there are problems its a complex state that we can't
				//really compare.
				return (! hasProblems() && ! other.hasProblems());
			}
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		if (property != null) hash*=property.hashCode();
		if (value != null) hash*=value.hashCode();
		
		for (Problem p : problems) {
			hash*=p.hashCode();
		}
		
		return hash;
	}

	public PropertyValue(Property<?> prop, Object value) {
		this.property = prop;
		this.value = value;
		this.problems = EMPTY_PROBLEM_LIST;
	}
	
	public PropertyValue(Property<?> prop, Object value, ProblemList<Problem> inIssues) {
		this.property = prop;
		this.value = value;
		
		if (inIssues != null && inIssues.size() > 0) {
			problems = new UnmodifiableProblemList(inIssues);
		} else {
			this.problems = EMPTY_PROBLEM_LIST;
		}
	}

	public Property<?> getProperty() {
		return property;
	}

	public Object getValue() {
		return value;
	}

	public boolean hasProblems() {
		return problems.size() > 0;
	}
	
	public ProblemList<Problem> getProblems() {
		return problems;	//Already unmodifiable
	}
	
}
