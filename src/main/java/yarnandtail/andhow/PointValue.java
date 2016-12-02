package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author eeverman
 */
public class PointValue {
	private static final List<ValueIssue> EMPTY_ISSUE_LIST = Collections.emptyList();
	
	private ConfigPoint<?> point;
	private Object value;
	private List<ValueIssue> issues;

	public PointValue(ConfigPoint<?> point, Object value) {
		this.point = point;
		this.value = value;
		this.issues = EMPTY_ISSUE_LIST;
	}
	
	public PointValue(ConfigPoint<?> point, Object value, List<ValueIssue> inIssues) {
		this.point = point;
		this.value = value;
		
		if (inIssues.size() > 0) {
			List<ValueIssue> newIssues = new ArrayList(inIssues.size());
			newIssues.addAll(inIssues);
			issues = Collections.unmodifiableList(newIssues);
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
	
	public List<ValueIssue> getIssues() {
		return issues;	//Already unmodifiable
	}
	
}
