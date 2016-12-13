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
	private static final List<PropertyValueProblem> EMPTY_ISSUE_LIST = Collections.emptyList();
	
	private Property<?> property;
	private Object value;
	private List<PropertyValueProblem> issues;

	public PropertyValue(Property<?> prop, Object value) {
		this.property = prop;
		this.value = value;
		this.issues = EMPTY_ISSUE_LIST;
	}
	
	public PropertyValue(Property<?> prop, Object value, List<PropertyValueProblem> inIssues) {
		this.property = prop;
		this.value = value;
		
		if (inIssues != null && inIssues.size() > 0) {
			List<PropertyValueProblem> newIssues = new ArrayList(inIssues.size());
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
	
	public List<PropertyValueProblem> getIssues() {
		return issues;	//Already unmodifiable
	}
	
}
