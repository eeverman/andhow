package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * A problem of required values not being specified.
 * @author eeverman
 * 
 */
public abstract class RequirementProblem implements Problem {
	
	/** The Property that actually has the problem */
	protected PropertyCoord propertyCoord;
		
	/**
	 * The required property that has not been given a value.
	 * 
	 * This may have a null Property if the issue is a required BasePropertyGroup.
	 * @return 
	 */
	public PropertyCoord getPropertyCoord() {
		return propertyCoord;
	}
	
	
	@Override
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}
		
	public static class NonNullPropertyProblem extends RequirementProblem {
		
		public NonNullPropertyProblem(Class<?> group, Property<?> prop) {
			propertyCoord = new PropertyCoord(group, prop);
		}
		
		@Override
		public String getProblemContext() {
			return TextUtil.format("Property {}", propertyCoord.getPropName());
		}
		
		@Override
		public String getProblemDescription() {
			return "This Property must be non-null - It must have a non-null default or be loaded by one of the loaders to a non-null value";
		}
	}
	

	public static class RequiredPropertyGroupProblem extends RequirementProblem {
		
		public RequiredPropertyGroupProblem(Class<?> group) {
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
