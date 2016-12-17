package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple class to bundle a Property, its value and any associated issues with the Property.
 * 
 * @author eeverman
 */
public class PropertyValue {
	private static final List<ValueProblem> EMPTY_ISSUE_LIST = Collections.emptyList();
	
	private Property<?> property;
	private Object value;
	private List<ValueProblem> issues;

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
				return (! hasIssues() && ! other.hasIssues());
			}
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		if (property != null) hash*=property.hashCode();
		if (value != null) hash*=value.hashCode();
		
		for (ValueProblem p : issues) {
			hash*=p.hashCode();
		}
		
		return hash;
	}

	public PropertyValue(Property<?> prop, Object value) {
		this.property = prop;
		this.value = value;
		this.issues = EMPTY_ISSUE_LIST;
	}
	
	public PropertyValue(Property<?> prop, Object value, List<ValueProblem> inIssues) {
		this.property = prop;
		this.value = value;
		
		if (inIssues != null && inIssues.size() > 0) {
			List<ValueProblem> newIssues = new ArrayList(inIssues.size());
			newIssues.addAll(inIssues);
			issues = Collections.unmodifiableList(newIssues);
		} else {
			this.issues = EMPTY_ISSUE_LIST;
		}
	}

	public Property<?> getProperty() {
		return property;
	}

	public Object getValue() {
		return value;
	}

	public boolean hasIssues() {
		return issues.size() > 0;
	}
	
	public List<ValueProblem> getIssues() {
		return issues;	//Already unmodifiable
	}
	
}
