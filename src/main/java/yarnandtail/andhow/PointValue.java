package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author eeverman
 */
public class PointValue {
	private static final List<PointValueProblem> EMPTY_ISSUE_LIST = Collections.emptyList();
	
	private ConfigPoint<?> point;
	private Object value;
	private List<PointValueProblem> issues;

	public PointValue(ConfigPoint<?> point, Object value) {
		this.point = point;
		this.value = value;
		this.issues = EMPTY_ISSUE_LIST;
	}
	
	public PointValue(ConfigPoint<?> point, Object value, List<PointValueProblem> inIssues) {
		this.point = point;
		this.value = value;
		
		if (inIssues != null && inIssues.size() > 0) {
			List<PointValueProblem> newIssues = new ArrayList(inIssues.size());
			newIssues.addAll(inIssues);
			issues = Collections.unmodifiableList(newIssues);
		} else {
			this.issues = EMPTY_ISSUE_LIST;
		}
	}

	public ConfigPoint<?> getPoint() {
		return point;
	}

	public Object getValue() {
		return value;
	}

	public boolean hasIssues() {
		return issues.size() > 0;
	}
	
	public List<PointValueProblem> getIssues() {
		return issues;	//Already unmodifiable
	}
	
}
