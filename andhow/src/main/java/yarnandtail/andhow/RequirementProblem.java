package yarnandtail.andhow;

/**
 * A problem of required values not being specified.
 * @author eeverman
 */
public class RequirementProblem {
	
	protected final Property<?> point;
	protected final Class<? extends PropertyGroup> group;

	public RequirementProblem(Property<?> point) {
		this.point = point;
		this.group = null;
	}

	public RequirementProblem(Class<? extends PropertyGroup> group) {
		this.point = null;
		this.group = group;
	}	

	public Property<?> getPoint() {
		return point;
	}

	public Class<? extends PropertyGroup> getGroup() {
		return group;
	}
	
	/**
	 * Builds a message describing the issue with the value within the context
 of the Property or group.
	 * 
	 * Assume that the user already sees the name of the config point or group:
	 * Its name does not need to be repeated, only the problem.
	 * @return 
	 */
	public String getMessageWithinFullContext() {
		
		if (point != null) {
			return "This property is required: "
				+ "A value must be found by one of the loaders";
		} else {
			return "This PropertyGroup is required: "
				+ "At value for at least one its property must be found by one of the loaders";	
		}
	}
	
}
