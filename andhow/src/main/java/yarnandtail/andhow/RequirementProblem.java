package yarnandtail.andhow;

/**
 * A problem of required values not being specified.
 * @author eeverman
 * 
 */
public abstract class RequirementProblem extends Problem {
	
	/** The Property that actually has the problem */
	protected PropertyCoord propertyCoord;
		
	/**
	 * The required property that has not been given a value.
	 * 
	 * This may have a null Property if the issue is a required PropertyGroup.
	 * @return 
	 */
	public PropertyCoord getPropertyCoord() {
		return propertyCoord;
	}
		
	public static class RequiredPropertyProblem extends RequirementProblem {
		
		public RequiredPropertyProblem(Class<? extends PropertyGroup> group, Property<?> prop) {
			propertyCoord = new PropertyCoord(group, prop);
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("Property {}", propertyCoord.getName());
		}
		
		@Override
		public String getProblemDescription() {
			return "This Property is required - value must be found by one of the loaders";
		}
	}
	

	public static class RequiredPropertyGroupProblem extends RequirementProblem {
		
		public RequiredPropertyGroupProblem(Class<? extends PropertyGroup> group) {
			propertyCoord = new PropertyCoord(group, null);
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("PropertyGroup {}", propertyCoord.getGroup().getCanonicalName());
		}
		
		@Override
		public String getProblemDescription() {
			return "This PropertyGroup is required - A value for at least one its " +
					"propertides must be found by one of the loaders";
		}
	}
}
