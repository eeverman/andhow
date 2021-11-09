package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * A Property which must be non-null was left unspecified after value loading completed.
 * 
 */
public abstract class RequirementProblem extends PropertyProblem {


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

}
